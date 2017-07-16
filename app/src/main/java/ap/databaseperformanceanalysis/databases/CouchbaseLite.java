package ap.databaseperformanceanalysis.databases;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ap.databaseperformanceanalysis.model.Author;
import ap.databaseperformanceanalysis.model.Post;

public class CouchbaseLite extends Analysis {

    private Database couchDbAuthor;
    private Database couchDbPost;
    private Manager manager;

    public CouchbaseLite(Context context){
        try {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            couchDbAuthor = manager.getDatabase("dbauthor");
            couchDbPost = manager.getDatabase("dbpost");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void cleanDatabase() {
        try {
            couchDbAuthor.delete();
            couchDbPost.delete();
            //Create again
            couchDbAuthor = manager.getDatabase("dbauthor");
            couchDbPost = manager.getDatabase("dbpost");
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void insertAuthor(Author author) {
        insertData(couchDbAuthor, "author/", author.getKey(), author);
    }

    private void insertData(Database database, String documentName, String key, Object object) {
        try {
            Map<String, Object> docContent = new HashMap<>();
            docContent.put(documentName+key, object);
            Document document = new Document(database, documentName+key);
            document.putProperties(docContent);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateAuthorAndYourPosts(final Author author) {
        try {
            couchDbAuthor.getDocument("author/" + author.getKey()).delete();
            insertAuthor(author);
        } catch (CouchbaseLiteException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void deleteAuthorAndYourPosts(Author author) {
        try {
            List<Post> postList = searchPostsByAuthor(author);
            for (Post post: postList) {
                couchDbPost.getDocument("post/" + post.getKey()).delete();
            }
            couchDbAuthor.getDocument("author/" + author.getKey()).delete();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void insertPost(Post post) {
        insertData(couchDbPost, "post/", post.getKey(), post);
    }

    @Override
    protected List<Post> searchPostsByAuthor(final Author author) {
        List<Post> postList = new ArrayList<>();
        try {
            com.couchbase.lite.View viewsByAuthor = couchDbPost.getView("viewsByAuthor");
            viewsByAuthor.setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    emitter.emit(author.getKey(), document);

                }
            }, "1.0");


            Query query = viewsByAuthor.createQuery();
            List<Object> keyArray = new ArrayList<>();
            keyArray.add(author.getKey());
            query.setKeys(keyArray);

            QueryEnumerator rowEnum = query.run();
            for (; rowEnum.hasNext(); ) {
                QueryRow row = rowEnum.next();
                postList.add(fillPost(row.getDocument(), row.getDocumentId()));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return postList;
    }

    @Override
    protected Post searchPostById(Post post) {
        String key = "post/"+post.getKey();
        return fillPost(couchDbPost.getDocument(key), key);
    }

    private Post fillPost(Document document, String postKey) {
        Object object = document.getProperties().get(postKey);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new CustomSerializerDate())
                .registerTypeAdapter(Date.class, new CustomDeserializerDate()).create();

        Post result = gson.fromJson(object.toString(), Post.class);
        result.setAuthor(searchAuthorById(result.getAuthor().getKey()));
        return result;
    }

    protected Author searchAuthorById(String authorKey) {
        String key = "author/"+authorKey;
        Document document = couchDbAuthor.getDocument(key);
        Object object = document.getProperties().get(key);

        return new Gson().fromJson(object.toString(), Author.class);
    }

    @Override
    protected List<Post> searchAllPostsOrderByDate() {
        List<Post> postList = new ArrayList<>();
        try {
            Query query = couchDbPost.createAllDocumentsQuery();
            query.setAllDocsMode(Query.AllDocsMode.ONLY_CONFLICTS);
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext();) {
                QueryRow row = it.next();
                postList.add(fillPost(row.getDocument(), row.getDocumentId()));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        Collections.sort(postList, new PostComparator());
        return postList;
    }

    @Override
    protected void close() {
        couchDbAuthor.close();
        couchDbPost.close();
        manager.close();
    }

    @Override
    protected long selectCountPosts() {
        return couchDbPost.getDocumentCount();
    }

    @Override
    protected long selectCountAuthors() {
        return couchDbAuthor.getDocumentCount();
    }
}
class CustomSerializerDate implements JsonSerializer<Date> {
    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
            context) {
        return src == null ? null : new JsonPrimitive(src.getTime());
    }
}

class CustomDeserializerDate implements JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context) throws JsonParseException {
        return json == null ? null : new Date(json.getAsLong());
    }
}
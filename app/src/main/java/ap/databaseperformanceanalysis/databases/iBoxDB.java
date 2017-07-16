package ap.databaseperformanceanalysis.databases;

import java.util.ArrayList;
import java.util.List;
import ap.databaseperformanceanalysis.model.Author;
import ap.databaseperformanceanalysis.model.Post;
import iBoxDB.LocalServer.Box;
import iBoxDB.LocalServer.BoxSystem;
import iBoxDB.LocalServer.DB;

public class iBoxDB extends Analysis {

    private DB.AutoBox auto;
    private DB db;

    public iBoxDB(){
        DB.root(android.os.Environment.getDataDirectory().getAbsolutePath() + "/data/ap.databaseperformanceanalysis/");
        db = new DB(1);
        db.getConfig().ensureTable(Author.class, "/author", "key(30)", "name(30)", "biography(30)");
        db.getConfig().ensureTable(Post.class, "/post", "key(30)", "subject(30)", "title(30)", "content(30)", "dateString(50)", "authorKey(30)");
        auto = db.open();
    }
    @Override
    protected void cleanDatabase() {
        BoxSystem.DBDebug.DeleteDBFiles(1);
    }

    @Override
    protected void insertAuthor(Author author) {
        insertData("/author", author);
    }

    private void insertData(String boxName, Object object){
        try (Box box = auto.cube()) {
            box.d(boxName).insert(object);
            box.commit();
        }
    }

    @Override
    protected void updateAuthorAndYourPosts(Author author) {
        try (Box box = auto.cube()) {
            box.d("/author").update(author);
        }
    }

    @Override
    protected void deleteAuthorAndYourPosts(Author author) {
        List<Post> posts = searchPostsByAuthor(author);
        for (Post post: posts) {
            auto.delete("/post", post);
        }
        //Delete doesn't work without search
        auto.delete("/author", searchAuthorById(author.getKey()));
    }

    private Author searchAuthorById(String authorKey) {
        try (Box box = auto.cube()) {
            for (Author result : box.select(Author.class, "from /author where key==?", authorKey)) {
                return result;
            }
        }
        return null;
    }

    @Override
    protected void insertPost(Post post) {
        insertData("/post", post);
    }

    @Override
    protected List<Post> searchPostsByAuthor(Author author) {
        List<Post> result = new ArrayList<>();
        try (Box box = auto.cube()) {
            for (Post post : box.select(Post.class, "from /post where authorKey==?", author.getKey())) {
                result.add(post);
            }
        }
        return result;
    }

    @Override
    protected Post searchPostById(Post post) {
        try (Box box = auto.cube()) {
            for (Post result : box.select(Post.class, "from /post where key==?", post.getKey())) {
                return result;
            }
        }
        return null;
    }

    @Override
    protected List<Post> searchAllPostsOrderByDate() {
        List<Post> result = new ArrayList<>();
        try (Box box = auto.cube()) {
            for (Post post : box.select(Post.class, "from /post order by dateString")) {
                result.add(post);
            }
        }
        return result;
    }

    @Override
    protected long selectCountPosts() {
        try (Box box = auto.cube()) {
            return box.selectCount("from /post");
        }
    }

    @Override
    protected long selectCountAuthors() {
        try (Box box = auto.cube()) {
            return box.selectCount("from /author");
        }
    }

    @Override
    protected void close() {
        db.close();
    }
}

package ap.databaseperformanceanalysis.databases;

import android.content.Context;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ap.databaseperformanceanalysis.model.Author;
import ap.databaseperformanceanalysis.model.Post;

public class SnappyDB extends Analysis {

    private final String AUTHOR_PREFIX = "author_";
    private final String POST_PREFIX = "post_";
    private final String AUTHOR_POST_PREFIX = "authorpost_";
    private DB snappydb;
    private Context context;

    public SnappyDB(Context context){
        try {
            snappydb = DBFactory.open(context);
            this.context = context;
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void cleanDatabase() {
        try {
            snappydb.destroy();
            //Create again
            snappydb = DBFactory.open(context);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void insertAuthor(Author author) {
        try {
            snappydb.put(AUTHOR_PREFIX+author.getKey(), author);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateAuthorAndYourPosts(Author author) {
        try {
            snappydb.del(AUTHOR_PREFIX+author.getKey());
            snappydb.put(AUTHOR_PREFIX+author.getKey(), author);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void deleteAuthorAndYourPosts(Author author) {
        try {
            snappydb.del(AUTHOR_PREFIX+author.getKey());
            Post[] posts = snappydb.getObjectArray(AUTHOR_POST_PREFIX+author.getKey(), Post.class);
            for (Post post: posts) {
                snappydb.del(POST_PREFIX+post.getKey());
            }
            snappydb.del(AUTHOR_POST_PREFIX+author.getKey());
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void insertPost(Post post) {
        try {
            snappydb.put(POST_PREFIX+post.getKey(), post);
            //Insert posts by author
            Post[] posts = null;
            int length = 1;
            try {
                posts = snappydb.getObjectArray(AUTHOR_POST_PREFIX+post.getAuthor().getKey(), Post.class);
                if(posts!=null){
                    length += posts.length;
                }
            } catch (SnappydbException e) {
                e.printStackTrace();
            }
            Post[] newPostsArray = new Post[length];
            int index = 0;
            if(posts!=null){
                for (Post element: posts) {
                    newPostsArray[index] = element;
                    index++;
                }
            }
            newPostsArray[index] = post;
            snappydb.del(AUTHOR_POST_PREFIX+post.getAuthor().getKey());
            snappydb.put(AUTHOR_POST_PREFIX+post.getAuthor().getKey(), newPostsArray);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected List<Post> searchPostsByAuthor(Author author) {
        try {
            Post[] posts = snappydb.getObjectArray(AUTHOR_POST_PREFIX+author.getKey(), Post.class);
            return Arrays.asList(posts);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Post searchPostById(Post post) {
        try {
            return snappydb.getObject(POST_PREFIX+post.getKey(), Post.class);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected List<Post> searchAllPostsOrderByDate() {
        List<Post> result = new ArrayList<>();
        try {
            String keys[] = snappydb.findKeys(POST_PREFIX);
            for (String key: keys) {
                Post post = snappydb.getObject(key, Post.class);
                result.add(post);
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }

        Collections.sort(result, new PostComparator());
        return result;
    }

    @Override
    protected long selectCountPosts() {
        try {
            return snappydb.countKeys(POST_PREFIX);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected long selectCountAuthors() {
        try {
            return snappydb.countKeys(AUTHOR_PREFIX);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void close() {
        try {
            snappydb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }
}
class PostComparator implements Comparator<Post> {
    @Override
    public int compare(Post one, Post two) {
        return one.date.compareTo(two.date);
    }
}

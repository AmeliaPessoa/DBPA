package ap.databaseperformanceanalysis.databases;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ap.databaseperformanceanalysis.model.Author;
import ap.databaseperformanceanalysis.model.Post;
import io.realm.Realm;

/**
 * Created by Iago Belo on 26/07/17.
 */

public class RealmDB extends Analysis {

    // Constants
    private static final String KEY = "key";
    private static final String AUTHOR_KEY = "authorKey";

    // Realm
    private Realm mRealm;

    public RealmDB(Context context) {
        Realm.init(context);

        mRealm = Realm.getDefaultInstance();
    }

    @Override
    protected void cleanDatabase() {
        mRealm.close();

        Realm.deleteRealm(mRealm.getConfiguration());

        mRealm = Realm.getDefaultInstance();
    }

    @Override
    protected void insertAuthor(Author author) {
        mRealm.beginTransaction();
        mRealm.copyToRealm(author);
        mRealm.commitTransaction();
    }

    @Override
    protected void updateAuthorAndYourPosts(Author author) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(author);
        mRealm.commitTransaction();
    }

    @Override
    protected void deleteAuthorAndYourPosts(Author author) {
        mRealm.beginTransaction();

        mRealm.where(Author.class)
                .equalTo(KEY, author.getKey())
                .findAll()
                .deleteAllFromRealm();

        mRealm.where(Post.class)
                .equalTo(AUTHOR_KEY, author.getKey())
                .findAll()
                .deleteAllFromRealm();

        mRealm.commitTransaction();
    }

    @Override
    protected void insertPost(Post post) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(post);
        mRealm.commitTransaction();
    }

    @Override
    protected List<Post> searchPostsByAuthor(Author author) {
        return mRealm.where(Post.class)
                .equalTo(AUTHOR_KEY, author.getKey())
                .findAll();
    }

    @Override
    protected Post searchPostById(Post post) {
        return mRealm.where(Post.class).equalTo(KEY, post.getKey()).findFirst();
    }

    @Override
    protected List<Post> searchAllPostsOrderByDate() {
        List<Post> posts = new ArrayList<>(mRealm.where(Post.class).findAll());

        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                if (o1.getDate() == null || o2.getDate() == null) return 0;

                return o1.getDate().compareTo(o2.getDate());
            }
        });

        return posts;
    }

    @Override
    protected long selectCountPosts() {
        return mRealm.where(Post.class).count();
    }

    @Override
    protected long selectCountAuthors() {
        return mRealm.where(Author.class).count();
    }

    @Override
    protected void close() {
        mRealm.close();
    }
}

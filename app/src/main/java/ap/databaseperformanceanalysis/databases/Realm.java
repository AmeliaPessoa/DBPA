package ap.databaseperformanceanalysis.databases;

import android.content.Context;

import java.util.List;

import ap.databaseperformanceanalysis.model.Author;
import ap.databaseperformanceanalysis.model.Post;
import io.realm.RealmResults;
import io.realm.Sort;

public class Realm extends Analysis {
    private final io.realm.Realm realm;

    public Realm(Context context){
        io.realm.Realm.init(context);
        realm = io.realm.Realm.getDefaultInstance();
    }

    @Override
    protected void cleanDatabase() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    @Override
    protected void insertAuthor(Author author) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(author);
        realm.commitTransaction();
    }

    @Override
    protected void updateAuthorAndYourPosts(Author author) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(author);
        realm.commitTransaction();
    }

    @Override
    protected void deleteAuthorAndYourPosts(Author author) {
        RealmResults<Post> realmPosts = realm.where(Post.class)
                .equalTo("author.key", author.key)
                .findAll();
        Author realmAuthor = realm.where(Author.class)
                .equalTo("key", author.key)
                .findFirst();

        realm.beginTransaction();
        realmPosts.deleteAllFromRealm();
        realmAuthor.deleteFromRealm();
        realm.commitTransaction();
    }

    @Override
    protected void insertPost(Post post) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(post);
        realm.commitTransaction();
    }

    @Override
    protected List<Post> searchPostsByAuthor(Author author) {
        return realm.where(Post.class)
                .equalTo("author.key", author.key)
                .findAll();
    }

    @Override
    protected Post searchPostById(Post post) {
        return realm.where(Post.class)
                .equalTo("key", post.key)
                .findFirst();
    }

    @Override
    protected List<Post> searchAllPostsOrderByDate() {
        return realm.where(Post.class)
                .findAllSorted("date", Sort.ASCENDING);
    }

    @Override
    protected long selectCountPosts() {
        return realm.where(Post.class)
                .count();
    }

    @Override
    protected long selectCountAuthors() {
        return realm.where(Author.class)
                .count();
    }

    @Override
    protected void close() {
        if(!realm.isClosed()) {
            realm.close();
        }
    }
}
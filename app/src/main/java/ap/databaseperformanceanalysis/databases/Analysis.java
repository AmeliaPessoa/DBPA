package ap.databaseperformanceanalysis.databases;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ap.databaseperformanceanalysis.model.Author;
import ap.databaseperformanceanalysis.model.Post;

public abstract class Analysis {

    protected abstract void cleanDatabase();
    protected abstract void insertAuthor(Author author);
    protected abstract void updateAuthorAndYourPosts(Author author);
    protected abstract void deleteAuthorAndYourPosts(Author author);
    protected abstract void insertPost(Post post);
    protected abstract List<Post> searchPostsByAuthor(Author author);
    protected abstract Post searchPostById(Post post);
    protected abstract List<Post> searchAllPostsOrderByDate();
    protected abstract long selectCountPosts();
    protected abstract long selectCountAuthors();
    protected abstract void close();

    public static final int ITERATIONS = 300;
    public static final String LOG_TAG = "DBPA ";

    private Date initialTime;
    private Date finalTime;
    private List<Author> authors = new ArrayList<>();
    private List<Author> updatedAuthors = new ArrayList<>();
    private List<Post> posts = new ArrayList<>();

    public void run(){
        initialTime = new Date();
        cleanDatabase();
        finalTime = new Date();
        logEvent("Clean Database");

        insertData(); //Insert Authors and Posts

        updateData(); //Update the Authors name

        selectCount(); //Count Author and Posts

        searchData(); //Search Posts by Author, Posts by id, All Posts order by date

        deleteData(); //Delete Authors and your Posts

        selectCount(); //Count Author and Posts

        close();
    }

    private void selectCount() {
        initialTime = new Date();
        long posts = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            posts = selectCountPosts();
        }
        finalTime = new Date();
        logEvent("Select count Posts ("+ ITERATIONS +"x) ["+posts+"]");

        initialTime = new Date();
        long authors = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            authors = selectCountAuthors();
        }
        finalTime = new Date();
        logEvent("Select count Authors ("+ ITERATIONS +"x) ["+authors+"]");
    }

    private void deleteData() {
        initialTime = new Date();
        for (int i = 0; i < ITERATIONS; i++) {
            deleteAuthorAndYourPosts(updatedAuthors.get(i));
        }
        finalTime = new Date();
        logEvent("Delete "+ ITERATIONS +" Authors with Posts");
    }

    private void searchData() {
        initialTime = new Date();
        for (int i = 0; i < ITERATIONS; i++) {
            searchPostsByAuthor(authors.get(i));
        }
        finalTime = new Date();
        logEvent("Search Posts by Authors ("+ ITERATIONS +"x)");

        initialTime = new Date();
        for (int i = 0; i < ITERATIONS; i++) {
            searchPostById(posts.get(i));
        }
        finalTime = new Date();
        logEvent("Search Post by Id ("+ ITERATIONS +"x)");

        initialTime = new Date();
        for (int i = 0; i < ITERATIONS; i++) {
            searchAllPostsOrderByDate();
        }
        finalTime = new Date();
        logEvent("Search All Posts order by Date ("+ ITERATIONS +"x)");
    }

    private void updateData() {
        initialTime = new Date();
        for (int i = 0; i < ITERATIONS; i++) {
            Author author = authors.get(i);
            author.setName("nameupdated"+i);
            updateAuthorAndYourPosts(author);
            updatedAuthors.add(author);
        }
        finalTime = new Date();
        logEvent("Update "+ ITERATIONS +" Authors");
    }

    private void insertData() {
        initialTime = new Date();
        for (int i = 0; i < ITERATIONS; i++) {
            Author author = new Author();
            author.setKey(""+i);
            author.setName("name"+i);
            author.setBiography("biography"+i);
            insertAuthor(author);
            authors.add(author);
        }
        finalTime = new Date();
        logEvent("Insert "+ ITERATIONS +" Authors");

        initialTime = new Date();
        for (int i = 0; i < ITERATIONS; i++) {
            Post post = new Post();
            post.setKey(""+i);
            post.setAuthor(authors.get(i));
            post.setContent("content"+i);
            post.setSubject("subject"+i);
            post.setTitle("title"+i);
            post.setDate(new Date());
            posts.add(post);
            insertPost(post);
        }
        finalTime = new Date();
        logEvent("Insert "+ ITERATIONS +" Posts");
    }

    private void logEvent(String event){
        Log.i(LOG_TAG + this.getClass().getSimpleName(), event+" (miliseconds) "+ getDeltaTimeInMilliseconds());
    }

    private long getDeltaTimeInMilliseconds(){
        return finalTime.getTime() - initialTime.getTime();
    }
}

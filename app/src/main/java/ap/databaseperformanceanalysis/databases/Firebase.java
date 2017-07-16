package ap.databaseperformanceanalysis.databases;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ap.databaseperformanceanalysis.model.Author;
import ap.databaseperformanceanalysis.model.Post;


public class Firebase {

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mAuthorReference;
    private DatabaseReference mPostReference;

    public Firebase() {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mAuthorReference = mFirebaseInstance.getReference("author");
        mPostReference = mFirebaseInstance.getReference("post");
    }

    protected void cleanDatabase() {
        mAuthorReference.removeValue();
        mPostReference.removeValue();
    }

    public void insertAuthor(Author author) {
        mAuthorReference.child(author.getKey()).setValue(author).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, e.getLocalizedMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                insertAuthorCount++;
                if(insertAuthorCount== ITERACTIONS){
                    logEvent("Insert "+ ITERACTIONS +" Authors", initialTimeAuthorCount, new Date());
                }
            }
        });
        mAuthorReference.push();
    }

    protected void updateAuthorAndYourPosts(final Author author) {
        mAuthorReference.child(author.getKey()).setValue(author).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, e.getLocalizedMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateAuthorCount++;
                if(updateAuthorCount== ITERACTIONS){
                    logEvent("Update "+ ITERACTIONS +" Authors", initialTimeUpdateAuthor, new Date());
                    selectCount();
                }
            }
        });
        mAuthorReference.push();
    }

    protected void deleteAuthorAndYourPosts(final Author author) {
        mAuthorReference.child(author.getKey().toString()).setValue(null).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, e.getLocalizedMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                deletePostsByAuthor(author);
            }
        });
        mAuthorReference.push();
    }

    private void deletePostsByAuthor(Author author) {
        DatabaseReference myRef = mFirebaseInstance.getReference("post");
        myRef.orderByChild("author/key").equalTo(author.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Post post = child.getValue(Post.class);
                    deletePost(post);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    protected void deletePost(Post post) {
        mPostReference.child(post.getKey().toString()).setValue(null).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, e.getLocalizedMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                deleteAuthorsAndPostsCount++;
                if(deleteAuthorsAndPostsCount== ITERACTIONS){
                    deleteAuthorsAndPostsCount = 0;
                    logEvent("Delete "+ ITERACTIONS +" Authors with Posts", initialTimeDeleteAuthorsAndPosts, new Date());
                    selectCount();
                }
            }
        });
        mPostReference.push();
    }

    protected void insertPost(Post post) {
        mPostReference.child(post.getKey()).setValue(post).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, e.getLocalizedMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                insertPostCount++;
                if(insertPostCount== ITERACTIONS){
                    logEvent("Insert "+ ITERACTIONS +" Posts", initialTimePostCount, new Date());
                    updateData();
                }
            }
        });
        mPostReference.push();
    }

    protected List<Post> searchPostsByAuthor(Author author) {
        DatabaseReference myRef = mFirebaseInstance.getReference("post");
        myRef.orderByChild("author/key").equalTo(author.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> result = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Post post = child.getValue(Post.class);
                    result.add(post);
                }
                searchPostsByAuthorCount++;
                if(searchPostsByAuthorCount== ITERACTIONS){
                    logEvent("Search Posts by Authors ("+ ITERACTIONS +")", initialTimeSearchPostsByAuthor, new Date());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return null;
    }

    protected Post searchPostById(Post post) {
        DatabaseReference myRef = mFirebaseInstance.getReference("post");
        myRef.orderByChild("key").equalTo(post.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    child.getValue(Post.class);
                }
                searchPostsByIdCount++;
                if(searchPostsByIdCount== ITERACTIONS){
                    logEvent("Search Posts by Id ("+ ITERACTIONS +")", initialTimeSearchPostsByID, new Date());
                    deleteData();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return null;
    }

    protected List<Post> searchAllPostsOrderByDate() {
        DatabaseReference myRef = mFirebaseInstance.getReference("post");
        myRef.orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> result = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Post post = child.getValue(Post.class);
                    result.add(post);
                }
                searchPostsOrderByDateCount++;
                if(searchPostsOrderByDateCount== ITERACTIONS){
                    logEvent("Search All Posts order by Date ("+ ITERACTIONS +")", initialTimeSearchPostsOrderByDate, new Date());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return null;
    }

    protected void close() {}

    protected long selectCountPosts() {
        DatabaseReference myRef = mFirebaseInstance.getReference("post");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();

                postCountIteractions++;
                if(postCountIteractions == ITERACTIONS){
                    logEvent("Select count Posts ("+ ITERACTIONS +"x) ["+count+"]", initialTimeSelectCountPosts, new Date());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return 0;
    }

    protected long selectCountAuthors() {
        DatabaseReference myRef = mFirebaseInstance.getReference("author");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();

                authorCountIteractions++;
                if(authorCountIteractions == ITERACTIONS){
                    logEvent("Select count Authors ("+ ITERACTIONS +"x) ["+count+"]", initialTimeSelectCountAuthors, new Date());
                    if(countIndex==0){
                        countIndex++;
                        searchData();
                    } else {
                        close();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return 0;
    }

    //----- Replicate Analysis code because the Firebase is event oriented --------//
    public static final int ITERACTIONS = Analysis.ITERACTIONS;
    public static final String LOG_TAG = Analysis.LOG_TAG;
    private Date initialTimeUpdateAuthor;
    private Date initialTimeSearchPostsByAuthor;
    private List<Author> authors = new ArrayList<>();
    private List<Author> updatedAuthors = new ArrayList<>();
    private List<Post> posts = new ArrayList<>();

    public void run(){
        Date initialTime = new Date();
        cleanDatabase();
        logEvent("Clean Database", initialTime, new Date());

        insertData(); //Insert Authors and Posts

        //Others methods are called when the previous is done
    }

    private Integer countIndex = 0;
    private Integer insertAuthorCount = 0;
    private Integer updateAuthorCount = 0;
    private Integer insertPostCount = 0;
    private Integer searchPostsByAuthorCount = 0;
    private Integer searchPostsOrderByDateCount = 0;
    private Integer searchPostsByIdCount = 0;
    private Integer deleteAuthorsAndPostsCount = 0;
    private Integer postCountIteractions = 0;
    private Integer authorCountIteractions = 0;
    private Date initialTimeAuthorCount = null;
    private Date initialTimePostCount = null;
    private Date initialTimeSearchPostsByID = null;
    private Date initialTimeSearchPostsOrderByDate = null;
    private Date initialTimeSelectCountPosts = null;
    private Date initialTimeSelectCountAuthors = null;
    private Date initialTimeDeleteAuthorsAndPosts = null;

    private void selectCount() {
        initialTimeSelectCountPosts = new Date();
        postCountIteractions = 0;
        for (int i = 0; i < ITERACTIONS; i++) {
            selectCountPosts();
        }

        initialTimeSelectCountAuthors = new Date();
        authorCountIteractions = 0;
        for (int i = 0; i < ITERACTIONS; i++) {
            selectCountAuthors();
        }
    }

    private void deleteData() {
        initialTimeDeleteAuthorsAndPosts = new Date();
        for (int i = 0; i < ITERACTIONS; i++) {
            deleteAuthorAndYourPosts(updatedAuthors.get(i));
        }
    }

    private void searchData() {
        initialTimeSearchPostsByAuthor = new Date();
        for (int i = 0; i < ITERACTIONS; i++) {
            searchPostsByAuthor(authors.get(i));
        }

        initialTimeSearchPostsByID = new Date();
        for (int i = 0; i < ITERACTIONS; i++) {
            searchPostById(posts.get(i));
        }

        initialTimeSearchPostsOrderByDate = new Date();
        for (int i = 0; i < ITERACTIONS; i++) {
            searchAllPostsOrderByDate();
        }
    }

    private void updateData() {
        initialTimeUpdateAuthor = new Date();
        for (int i = 0; i < ITERACTIONS; i++) {
            Author author = authors.get(i);
            author.setName("nameupdated"+i);
            updateAuthorAndYourPosts(author);
            updatedAuthors.add(author);
        }
    }

    private void insertData() {
        initialTimeAuthorCount = new Date();
        for (int i = 0; i < ITERACTIONS; i++) {
            Author author = new Author();
            author.setKey(""+i);
            author.setName("name"+i);
            author.setBiography("biography"+i);
            insertAuthor(author);
            authors.add(author);
        }

        initialTimePostCount = new Date();
        for (int i = 0; i < ITERACTIONS; i++) {
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
    }

    private void logEvent(String event, Date initialTime, Date finalTime){
        Log.i(LOG_TAG + this.getClass().getSimpleName(), event+" (miliseconds) "+ getDeltaTimeInMilliseconds(initialTime, finalTime));
    }

    private long getDeltaTimeInMilliseconds(Date initialTime, Date finalTime){
        return finalTime.getTime() - initialTime.getTime();
    }
}
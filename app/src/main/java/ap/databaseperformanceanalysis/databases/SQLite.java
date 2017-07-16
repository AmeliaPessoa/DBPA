package ap.databaseperformanceanalysis.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ap.databaseperformanceanalysis.model.Author;
import ap.databaseperformanceanalysis.model.Post;


public class SQLite extends Analysis {

    private SQLiteHelper sqLiteHelper;

    public SQLite(Context context){
        this.sqLiteHelper = new SQLiteHelper(context);
    }
    @Override
    protected void cleanDatabase() {
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        db.execSQL("DELETE FROM "+ SQLiteHelper.TABLE_AUTHOR);
        db.execSQL("DELETE FROM "+ SQLiteHelper.TABLE_POST);
        db.close();
    }

    @Override
    protected void insertAuthor(Author author) {
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

        ContentValues values = fillContentValues(author);

        db.insert(SQLiteHelper.TABLE_AUTHOR, null, values);
        db.close();
    }

    @NonNull
    private ContentValues fillContentValues(Author author) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.AUTHOR_KEY, author.getKey());
        values.put(SQLiteHelper.AUTHOR_NAME, author.getName());
        values.put(SQLiteHelper.AUTHOR_BIOGRAPHY, author.getBiography());
        return values;
    }

    @Override
    protected void updateAuthorAndYourPosts(Author author) {
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

        ContentValues values = fillContentValues(author);

        db.update(SQLiteHelper.TABLE_AUTHOR, values, SQLiteHelper.AUTHOR_KEY + " = ?",
                new String[] { String.valueOf(author.getKey()) });
        db.close();
    }

    @Override
    protected void deleteAuthorAndYourPosts(Author author) {
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        db.delete(SQLiteHelper.TABLE_POST, SQLiteHelper.POST_AUTHOR + " = ?",
                new String[] { String.valueOf(author.getKey()) });

        db.delete(SQLiteHelper.TABLE_AUTHOR, SQLiteHelper.AUTHOR_KEY + " = ?",
                new String[] { String.valueOf(author.getKey()) });
        db.close();
    }

    @Override
    protected void insertPost(Post post) {
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.POST_KEY, post.getKey());
        values.put(SQLiteHelper.POST_AUTHOR, post.getAuthor().getKey());
        values.put(SQLiteHelper.POST_CONTENT, post.getContent());
        values.put(SQLiteHelper.POST_DATE, post.getDate().getTime());
        values.put(SQLiteHelper.POST_SUBJECT, post.getSubject());
        values.put(SQLiteHelper.POST_TITLE, post.getTitle());

        db.insert(SQLiteHelper.TABLE_POST, null, values);
        db.close();
    }

    @Override
    protected List<Post> searchPostsByAuthor(Author author) {
        String query = "SELECT  * FROM " + SQLiteHelper.TABLE_POST
                + " WHERE "+ SQLiteHelper.POST_AUTHOR+"="+author.getKey();

        return searchPosts(query);
    }

    private List<Post> searchPosts(String query) {
        List<Post> result = new ArrayList<>();

        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor!=null) {
            cursor.moveToFirst();
            do {
                Post post = fillPostObject(cursor);
                result.add(post);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return result;
    }

    @Override
    protected Post searchPostById(Post post) {
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

        Cursor cursor = db.query(SQLiteHelper.TABLE_POST, new String[] { SQLiteHelper.POST_KEY,
                        SQLiteHelper.POST_AUTHOR, SQLiteHelper.POST_CONTENT, SQLiteHelper.POST_DATE,
                        SQLiteHelper.POST_SUBJECT, SQLiteHelper.POST_TITLE}, SQLiteHelper.POST_KEY + "=?",
                new String[] { String.valueOf(post.getKey()) }, null, null, null, null);
        Post result = null;
        if (cursor != null) {
            cursor.moveToFirst();
            result = fillPostObject(cursor);
            cursor.close();
        }
        db.close();

        return result;
    }

    @NonNull
    private Post fillPostObject(Cursor cursor) {
        Post result = new Post();
        result.setKey(cursor.getString(cursor.getColumnIndex(SQLiteHelper.POST_KEY)));
        result.setContent(cursor.getString(cursor.getColumnIndex(SQLiteHelper.POST_CONTENT)));
        result.setDate(new Date(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.POST_DATE))));
        result.setSubject(cursor.getString(cursor.getColumnIndex(SQLiteHelper.POST_SUBJECT)));
        result.setTitle(cursor.getString(cursor.getColumnIndex(SQLiteHelper.POST_TITLE)));

        String authorKey = cursor.getString(cursor.getColumnIndex(SQLiteHelper.POST_AUTHOR));
        result.setAuthor(searchAuthorById(authorKey));
        return result;
    }

    private Author searchAuthorById(String authorKey) {
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

        Cursor cursor = db.query(SQLiteHelper.TABLE_AUTHOR, new String[]{SQLiteHelper.AUTHOR_KEY,
                        SQLiteHelper.AUTHOR_NAME, SQLiteHelper.AUTHOR_BIOGRAPHY}, SQLiteHelper.AUTHOR_KEY + "=?",
                new String[]{String.valueOf(authorKey)}, null, null, null, null);
        Author result = new Author();
        if (cursor != null) {
            cursor.moveToFirst();

            result.setKey(cursor.getString(0));
            result.setName(cursor.getString(1));
            result.setBiography(cursor.getString(2));

            cursor.close();
        }
        db.close();

        return result;
    }

    @Override
    protected List<Post> searchAllPostsOrderByDate() {
        String query = "SELECT  * FROM " + SQLiteHelper.TABLE_POST + " ORDER BY "+ SQLiteHelper.POST_DATE;

        return searchPosts(query);
    }

    @Override
    protected long selectCountPosts() {
        return getCount(SQLiteHelper.TABLE_POST);
    }

    @Override
    protected long selectCountAuthors() {
        return getCount(SQLiteHelper.TABLE_AUTHOR);
    }

    private int getCount(String table) {
        String countQuery = "SELECT  * FROM " + table;
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int result = cursor.getCount();
        cursor.close();
        db.close();

        return result;
    }

    @Override
    protected void close() {
        sqLiteHelper.close();
    }
}
class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sqlite";

    static final String TABLE_POST = "post";
    static final String TABLE_AUTHOR = "author";

    static final String AUTHOR_KEY = "key";
    static final String AUTHOR_NAME = "name";
    static final String AUTHOR_BIOGRAPHY = "biography";

    static final String POST_KEY = "key";
    static final String POST_SUBJECT = "subject";
    static final String POST_TITLE = "title";
    static final String POST_CONTENT = "content";
    static final String POST_DATE = "date";
    static final String POST_AUTHOR = "author";

    SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_AUTHOR_TABLE = "CREATE TABLE " + TABLE_AUTHOR + "("
                + AUTHOR_KEY+" TEXT PRIMARY KEY,"
                + AUTHOR_NAME + " TEXT,"
                + AUTHOR_BIOGRAPHY + " TEXT" + ")";
        db.execSQL(CREATE_AUTHOR_TABLE);


        String CREATE_POST_TABLE = "CREATE TABLE " + TABLE_POST + "("
                + POST_KEY+" TEXT PRIMARY KEY,"
                + POST_SUBJECT + " TEXT,"
                + POST_TITLE + " TEXT,"
                + POST_CONTENT + " TEXT,"
                + POST_DATE + " INTEGER,"
                + POST_AUTHOR + " TEXT NOT NULL,"
                +" FOREIGN KEY("+POST_AUTHOR+") REFERENCES "+TABLE_AUTHOR+"("+AUTHOR_KEY+") "
                + ")";
        db.execSQL(CREATE_POST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHOR);
        onCreate(db);
    }
}
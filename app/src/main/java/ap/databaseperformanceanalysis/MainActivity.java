package ap.databaseperformanceanalysis;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ap.databaseperformanceanalysis.databases.CouchbaseLite;
import ap.databaseperformanceanalysis.databases.Firebase;
import ap.databaseperformanceanalysis.databases.Realm;
import ap.databaseperformanceanalysis.databases.SQLite;
import ap.databaseperformanceanalysis.databases.SnappyDB;
import ap.databaseperformanceanalysis.databases.iBoxDB;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new iBoxDB().run();
        new CouchbaseLite(this).run();
        //new Firebase().run();
        new SnappyDB(this).run();
        new SQLite(this).run();
        new Realm(this).run();
    }
}

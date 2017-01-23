package health.vit.com.healthtracker;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shashankshekhar on 22/01/17.
 */

public class PulseData {

    private static final String KEY_ROWID = "_id";
    private static final String KEY_PULSERATE = "PulseRate";
    private static final String KEY_TIMESTAMP = "TimeStamp";

    private static final String DATABASE_NAME = "PulseDatabase";
    private static final String DATABASE_TABLE = "PulseTable";
    private static final int DATABASE_VERSION = 1;

    private DbHelper dbHelper;
    private final Context context;
    private SQLiteDatabase database;

    public class DbHelper extends SQLiteOpenHelper{

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
           db.execSQL("CREATE TABLE" + DATABASE_TABLE + "(" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_PULSERATE + " INTEGER NOT NULL " +
                    KEY_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        }
    }

    public PulseData(Context c){
        this.context = c;

    }

    public PulseData open(){
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }
    
}

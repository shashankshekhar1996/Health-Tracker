package health.vit.com.healthtracker;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shashankshekhar on 22/01/17.
 */

public class HealthTipsData {
    private static final String KEY_ROWID = "_id";
    private static final String KEY_TIPS= "HealthTips";
    //private static final String KEY_TIMESTAMP = "TimeStamp";

    private static final String DATABASE_NAME = "HealthTipsDb";
    private static final String DATABASE_TABLE = "HelthTipsTable";
    private static final int DATABASE_VERSION = 1;

    private PulseData.DbHelper dbHelper1;
    private final Context context;
    private SQLiteDatabase database;

    public class DbHelper1 extends SQLiteOpenHelper {

        public DbHelper1(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public DbHelper1(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE" + DATABASE_TABLE + "(" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_TIPS + " TEXT NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        }
    }

    public HealthTipsData(Context c){
        this.context = c;

    }

    public HealthTipsData open(){
        dbHelper1 = new DbHelper1(context);
        database = dbHelper1.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper1.close();
    }


}

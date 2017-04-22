package health.vit.com.healthtracker.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import health.vit.com.healthtracker.models.Reminder;
import rx.subjects.PublishSubject;

/**
 * Created by akshaymahajan on 22/04/17.
 */

public class RemindersDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "remindersDb";
    private static final String TABLE_REMINDERS = "reminders";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESC = "desc";
    private static final String KEY_DATE = "date";
    private static final String TAG = "DBHelper";

    private static RemindersDbHelper sInstance;
    PublishSubject subject;

    public RemindersDbHelper(Context context, PublishSubject subject) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.subject = subject;
    }

    public RemindersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * using one DB instance throughout app
     */
    public static synchronized RemindersDbHelper getInstance(PublishSubject subject, Context context) {

        if (sInstance == null) {
            sInstance = new RemindersDbHelper(context.getApplicationContext(), subject);
        }
        return sInstance;
    }

    /**
     * using one DB instance throughout app
     */
    public static synchronized RemindersDbHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new RemindersDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /** Create Contacts Table */
        String QUERY_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_REMINDERS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TITLE + " TEXT," + KEY_DESC + " TEXT," + KEY_DATE + " TEXT" + ")";
        db.execSQL(QUERY_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String QUERY_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_REMINDERS;
        db.execSQL(QUERY_DROP_TABLE);

        onCreate(db);
    }


    /**
     * Insert data into table
     *
     * @param reminder
     * @return boolean: successful or not
     */
    public boolean insertData(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, reminder.getTitle());
        contentValues.put(KEY_DESC, reminder.getDesc());
        contentValues.put(KEY_DATE, reminder.getDate());

        long row_id = db.insert(TABLE_REMINDERS, null, contentValues);
        if (row_id == -1) {
            return false;
        }
        subject.onNext(reminder);
        return true;

    }

    /**
     * Retrieve all reminders
     *
     * @return List of reminders
     */
    public List<Reminder> getAllReminders() {

        List<Reminder> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String QUERY_GET_ALL = "SELECT * FROM " + TABLE_REMINDERS + " ORDER BY " + KEY_ID + " ASC";
        Cursor cursor = db.rawQuery(QUERY_GET_ALL, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                contactList.add(new Reminder(cursor.getInt(cursor.getColumnIndex(KEY_ID)), cursor.getString(cursor.getColumnIndex(KEY_TITLE)), cursor.getString(cursor.getColumnIndex(KEY_DESC)), cursor.getString(cursor.getColumnIndex(KEY_DATE))));
            }
        }
        cursor.close();
        return contactList;
    }

    public void deleteReminder(int reminder_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String QUERY_DELETE_REMINDER = "DELETE FROM " + TABLE_REMINDERS + " WHERE " + KEY_ID + " = " + reminder_id;
        db.execSQL(QUERY_DELETE_REMINDER);
    }
}
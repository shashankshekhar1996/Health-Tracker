package health.vit.com.healthtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.camera2.CaptureResult;
import android.renderscript.Script;
import android.util.ArrayMap;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.order;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.M;
import static health.vit.com.healthtracker.R.id.map;

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

    private Date toDate;
    private Date fromDate;


    public class DbHelper extends SQLiteOpenHelper{

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
           db.execSQL("CREATE TABLE " + DATABASE_TABLE + "(" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_PULSERATE + " INTEGER NOT NULL, " +
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
        try{
            dbHelper = new DbHelper(context);
            database = dbHelper.getWritableDatabase();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return this;
    }

    public void close(){
        dbHelper.close();

    }

    public long createEntry(String a, String b){
        ContentValues cv = new ContentValues();
        cv.put(KEY_PULSERATE,Integer.parseInt(a));
        cv.put(KEY_TIMESTAMP,b);
        return database.insert(DATABASE_TABLE,null, cv);

    }


    public String getData() {
        String[] col = new String[]{KEY_ROWID, KEY_PULSERATE, KEY_TIMESTAMP};
        Cursor c = database.query(DATABASE_TABLE,col,null,null,null,null,null);
        String result = "";
        int iRow = c.getColumnIndex(KEY_ROWID);
        int iPulserate = c.getColumnIndex(KEY_PULSERATE);
        int iTime = c.getColumnIndex(KEY_TIMESTAMP);



        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            result+=c.getInt(iRow) +" "+c.getInt(iPulserate)+" "+c.getString(iTime) + "\n";
        }
        return result;
    }


    public List<Map<String, Integer>> getMinMaxData(){
        List<Map<String, Integer>> list = new ArrayList<>();

        Cursor c;
        c = database.rawQuery("SELECT * FROM " + DATABASE_TABLE +  " ORDER BY " + KEY_ROWID + " DESC LIMIT 4;" , null);

        String result = "";
        int iRow = c.getColumnIndex(KEY_ROWID);
        int iPulserate = c.getColumnIndex(KEY_PULSERATE);
        int iTime = c.getColumnIndex(KEY_TIMESTAMP);

        Integer minRate=0, maxRate=0;
        String minTime="", maxTime="";

        if(c.moveToFirst()){
            minRate = c.getInt(iPulserate);
            maxRate = c.getInt(iPulserate);
            minTime = c.getString(iTime);
            maxTime = c.getString(iTime);
        }


        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){

            Integer pulseRate = c.getInt(iPulserate);

            if(pulseRate > maxRate){
                maxRate = pulseRate;
                maxTime = c.getString(iTime);

            }

            if(pulseRate < minRate){
                minRate = pulseRate;
                minTime = c.getString(iTime);
            }

        }




        Map<String, Integer> minMap = new ArrayMap<>();
        minMap.put(minTime, minRate);
        list.add(minMap);

        Map<String, Integer> maxMap = new ArrayMap<>();
        maxMap.put(maxTime, maxRate);
        list.add(maxMap);

        return list;

    }

   // public

//    public Double getAvg(){
//        Double avg = 0.0;
//        Cursor c;
//
//       // c = database.rawQuery("SELECT SUM(" + KEY_PULSERATE + ") as Total FROM " + DATABASE_TABLE + ";",null);
//        c = database.rawQuery("SELECT SUM(" + KEY_PULSERATE + ") as Total, COUNT(*) as TotalCount FROM " + DATABASE_TABLE + ";",null);
//
//        //Cursor c1;
//       // c1 = database.rawQuery("SELECT * FROM " + DATABASE_TABLE + ";",null);
//
//        String result = "";
//        int iRow = c.getColumnIndex("Total");
//        int itc = c.getColumnIndex("TotalCount");
//
//        int y = c.getCount();
//       // int y1 = c1.getCount();
//        if(c.moveToFirst()) {
//            if (c.getCount() < 50 && c.getCount() > 0) {
//                //avg = Double.valueOf(Double.valueOf(c.getInt(0)) / c1.getCount());
//                avg = Double.valueOf(Double.valueOf(c.getInt(iRow)) / c.getInt(itc));
//            } else if (c.getCount() >= 50) {
//                avg = Double.valueOf(Double.valueOf(c.getInt(iRow)) / 50);
//            } else {
//                avg = 0.0;
//            }
//        }
//
//        return avg;
//    }

    public Double getAvg(){
        Double avg = 0.0;
        Cursor c,c1;

        try
        {
            c = database.rawQuery("SELECT * FROM " + DATABASE_TABLE + " ORDER BY " + KEY_ROWID + " DESC LIMIT 50;",null);
            c1 = database.rawQuery("SELECT COUNT(" + KEY_ROWID + ") as count FROM " + DATABASE_TABLE, null);

            String result = "";
            Integer count = 0;
            int iRow = c.getColumnIndex(KEY_PULSERATE);
            int itc = c1.getColumnIndex("count");
            if(c1.moveToFirst()) {
                count = c1.getInt(itc);
            }
            Integer sum = 0;


            for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
                sum = sum + c.getInt(iRow);
            }

            if(count < 50 && count > 0){
                avg = Double.valueOf(sum)/count;
            }else if(count == 50){
                avg = Double.valueOf(sum)/50;
            }else{
                avg = 0.0;
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.i("Error", e.toString());
        }

        return avg;
    }

    public LinkedHashMap<String, Integer> getAllData(String from, String to, String str) {

        String[] col = new String[]{KEY_ROWID, KEY_PULSERATE, KEY_TIMESTAMP};
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            toDate = sdf.parse(to);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try{
            fromDate = sdf.parse(from);
        }catch (Exception e){
            e.printStackTrace();
        }

        Timestamp fromTime = new Timestamp(fromDate.getTime());
        Timestamp toTime = new Timestamp(toDate.getTime());
        //Date d = (Date)

        //Date t = new Date(fro)
        Cursor c = null;
       // if(fromTime != toTime){
        if(str.equals("DATETIMEASC")) {
            c = database.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE " + KEY_TIMESTAMP + ">='" + fromTime + "' AND " + KEY_TIMESTAMP + "<='" + toTime + "' ORDER BY " + KEY_TIMESTAMP + ";", null);
        }else if(str.equals("PULSEASC")){
            c = database.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE " + KEY_TIMESTAMP + ">='" + fromTime + "' AND " + KEY_TIMESTAMP + "<='" + toTime + "' ORDER BY " + KEY_PULSERATE + ";", null);
        }else if(str.equals("DATETIMEDESC")){
            c = database.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE " + KEY_TIMESTAMP + ">='" + fromTime + "' AND " + KEY_TIMESTAMP + "<='" + toTime + "' ORDER BY " + KEY_TIMESTAMP + " DESC;", null);
        }else if(str.equals("PULSEDESC")){
            c = database.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE " + KEY_TIMESTAMP + ">='" + fromTime + "' AND " + KEY_TIMESTAMP + "<='" + toTime + "' ORDER BY " + KEY_PULSERATE + " DESC;", null);
        }else{
            c = database.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE " + KEY_TIMESTAMP + ">='" + fromTime + "' AND " + KEY_TIMESTAMP + "<='" + toTime + "';", null);
        }
       /* }else{
             c = database.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE " + KEY_TIMESTAMP + "='" + toTime + "';",null);

        }*/


        String result = "";
        int iRow = c.getColumnIndex(KEY_ROWID);
        int iPulserate = c.getColumnIndex(KEY_PULSERATE);
        int iTime = c.getColumnIndex(KEY_TIMESTAMP);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
           // result+=c.getInt(iRow) +" "+c.getString(iPulserate)+" "+c.getString(iTime) + "\n";
            map.put(c.getString(iTime), c.getInt(iPulserate));
        }
        return map;
    }
    
}

package edward.com.scannerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ScannerDatabase";
    private static final String TABLE_SCAN_HISTORY = "ScanHistory";

    private static final String KEY_ID = "id";
    private static final String KEY_SCAN_RESULT = "scan_result";
    private static final String KEY_DATETIME = "time";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_SCAN_HISTORY + "(" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_SCAN_RESULT + " TEXT, " +
                KEY_DATETIME + " TEXT)";
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCAN_HISTORY);
        onCreate(db);
    }

    public DatabaseHandler(@Nullable Context context) {
        super(context, TABLE_SCAN_HISTORY, null, DATABASE_VERSION);
    }

    public void addScanHistory(History history){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SCAN_RESULT, history.getResult());
        values.put(KEY_DATETIME, history.getDateTime());

        db.insert(TABLE_SCAN_HISTORY, null, values);
        db.close();
    }

    // get all records
    public ArrayList<History> getAllScanHistory(){
        ArrayList<History> historyList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM ScanHistory";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                History history = new History();
                history.setId(Integer.parseInt(cursor.getString(0)));
                history.setResult(cursor.getString(1));
                history.setDateTime(cursor.getString(2));

                historyList.add(history);
            } while (cursor.moveToNext());
        }

        return historyList;
    }

//    public History getHistory(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_SCAN_HISTORY, new String[] { KEY_ID,
//                        KEY_SCAN_RESULT, KEY_DATETIME }, KEY_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        History history = new History(Integer.parseInt(cursor.getString(0)),
//                cursor.getString(1));
//        // return contact
//        return history;

//    }

    public void clearScanHistoryTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_SCAN_HISTORY);
    }

    public void deleteHistory(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_SCAN_HISTORY + " where " + KEY_ID + " = " + id);
    }

}

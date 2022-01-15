package edward.com.scannerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import edward.com.scannerapp.model.Bookmark;
import edward.com.scannerapp.model.History;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "ScannerDatabase";
    private static final String TABLE_SCAN_HISTORY = "ScanHistory";
    private static final String TABLE_SCAN_BOOKMARK = "ScanBookmark";

    private static final String KEY_ID = "id";
    private static final String KEY_SCAN_RESULT = "scan_result";
    private static final String KEY_DATETIME = "time";
    private static final String KEY_HISTORY_ID = "history_id";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_SCAN_HISTORY + "(" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_SCAN_RESULT + " TEXT, " +
                KEY_DATETIME + " TEXT)";
        db.execSQL(CREATE_HISTORY_TABLE);

        String CREATE_BOOKMARK_TABLE = "CREATE TABLE " + TABLE_SCAN_BOOKMARK + "(" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_HISTORY_ID + " INTEGER, " +
                KEY_SCAN_RESULT + " TEXT, " +
                KEY_DATETIME + " TEXT)";
        db.execSQL(CREATE_BOOKMARK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCAN_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCAN_BOOKMARK);
        onCreate(db);
    }

    public DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public long addScanHistory(History history){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SCAN_RESULT, history.getResult());
        values.put(KEY_DATETIME, history.getDateTime());

        long insertedId = db.insert(TABLE_SCAN_HISTORY, null, values);
        db.close();

        return insertedId;
    }

    // get all records
    public ArrayList<History> getAllScanHistory(){
        ArrayList<History> historyList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT a.id, a.scan_result, a.time, b.id FROM ScanHistory a LEFT JOIN ScanBookmark b ON a.id = b.history_id";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                History history = new History();
                history.setId(Integer.parseInt(cursor.getString(0)));
                history.setResult(cursor.getString(1));
                history.setDateTime(cursor.getString(2));

                // Jika null, berarti tidak ada di table bookmark
                if (cursor.isNull(3)) {
                    history.setBookmarked(false);
                }
                else {
                    history.setBookmarked(true);
                }

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

    // Bookmark
    public void addScanBookmark(Bookmark bookmark){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SCAN_RESULT, bookmark.getResult());
        values.put(KEY_DATETIME, bookmark.getDateTime());
        values.put(KEY_HISTORY_ID, bookmark.getHistory_id());

        db.insert(TABLE_SCAN_BOOKMARK, null, values);
        db.close();
    }

    public ArrayList<Bookmark> getAllScanBookmark() {
        ArrayList<Bookmark> bookmarkList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM ScanBookmark";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Bookmark bookmark = new Bookmark();
                bookmark.setId(Integer.parseInt(cursor.getString(0)));
                bookmark.setHistory_id(Integer.parseInt(cursor.getString(1)));
                bookmark.setResult(cursor.getString(2));
                bookmark.setDateTime(cursor.getString(3));

                bookmarkList.add(bookmark);
            } while (cursor.moveToNext());
        }

        return bookmarkList;
    }

    public void deleteBookmarkByHistoryID(int history_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_SCAN_BOOKMARK + " where " + KEY_HISTORY_ID + " = " + history_id);
    }

    public void deleteBookmarkByBookmarkID(int bookmark_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_SCAN_BOOKMARK + " where " + KEY_ID + " = " + bookmark_id);
    }

    public void clearScanBookmarkTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_SCAN_BOOKMARK);
    }
}

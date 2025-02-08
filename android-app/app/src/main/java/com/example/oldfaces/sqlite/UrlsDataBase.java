package com.example.oldfaces.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class UrlsDataBase {
    private Context context;
    private static final String DB_NAME = "Oldface.db";
    public static final String TABLE_NAME = "urls";
    public static final String KEY_NAME = "name";
    public static final String KEY_URL = "url";
    //    public static final String KEY_LOOK_UP="look_up";
    private int version = 1;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    public UrlsDataBase(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper();
    }

    private ContentValues enCodeContentValues(String name, String url) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_URL, url);
        return cv;
    }

    public Cursor queryAll() {
        String sql = String.format("select * from %s", TABLE_NAME);
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public void insertData(String name, String url) {
        ContentValues cv = enCodeContentValues(name, url);
        db.insert(TABLE_NAME, null, cv);
    }

    public void deleteData(String name, String url) {
        if (name.contains("[默认]"))
            Toast.makeText(context, "默认网址不能删除", Toast.LENGTH_SHORT).show();
        else {
            String sql = String.format("delete from %s where %s='%s' and %s='%s'",
                    TABLE_NAME, KEY_NAME, name, KEY_URL, url);
            db.execSQL(sql);
        }
    }

    public void updateData(String oldName, String oldUrl, String newName, String newUrl) {

        String sql = String.format("update %s set %s='%s',%s='%s' where %s='%s' and %s='%s'",
                TABLE_NAME, KEY_NAME, newName, KEY_URL, newUrl, KEY_NAME, oldName, KEY_URL, oldUrl);
        db.execSQL(sql);

    }

    public void open() {
        if (db == null || !db.isOpen()) {
            db = databaseHelper.getWritableDatabase();
        }
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public void reset() {
        databaseHelper.reset(db);
    }

    class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper() {
            super(context, DB_NAME, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTableIfNotExists(db);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            createTableIfNotExists(db);
        }

        private void createTableIfNotExists(SQLiteDatabase db) {
            String createTableSQL = String.format(
                    "CREATE TABLE IF NOT EXISTS %s (" +
                            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "%s TEXT, %s TEXT UNIQUE)",
                    TABLE_NAME, KEY_NAME, KEY_URL);
            db.execSQL(createTableSQL);
//            ContentValues cv1 = enCodeContentValues("[默认]必应", "https://cn.bing.com/");
//            db.insert(TABLE_NAME, null, cv1);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            reset(db);
        }

        public void reset(SQLiteDatabase db) {
            String sql = String.format("drop table if exists %s", TABLE_NAME);
            db.execSQL(sql);
            onCreate(db);
        }
    }
}

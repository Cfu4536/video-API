package com.example.oldfaces.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class FavotiteVideoDataBase {
    private Context context;
    private static final String DB_NAME = "Oldface.db";
    public static final String TABLE_NAME = "favorites";
    public static final String KEY_PATH = "path";
    public static final String KEY_TYPE = "type";
    public static final String TYPE_VIDEO = "video";
    private int version = 1;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    public FavotiteVideoDataBase(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper();
    }

    //增删改查
    public boolean insertData(String path, String type) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(KEY_PATH, path);
            cv.put(KEY_TYPE, type);
            db.insert(TABLE_NAME, null, cv);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean deleteData(String path) {
        try {
            db.delete(TABLE_NAME, KEY_PATH + "=?", new String[]{path});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Cursor queryAll() {
        String sql = String.format("select * from %s", TABLE_NAME);
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Boolean isExist(String path) {
        String sql = String.format("select * from %s where %s=?", TABLE_NAME, KEY_PATH);
        Cursor c = db.rawQuery(sql, new String[]{path});
        if (c.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }
    public int countAll(){
        String sql = String.format("SELECT COUNT(*) FROM %s", TABLE_NAME);
        return (int)db.compileStatement(sql).simpleQueryForLong();
    }

    //开 关 重置
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
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s (" +
                            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            " %s text UNIQUE, %s text)",
                    TABLE_NAME, KEY_PATH, KEY_TYPE);
            db.execSQL(sql);
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

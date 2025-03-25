package com.example.sqlite0.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "item_db";
    private static final int DATABASE_VERSION = 3;

    // Bảng users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_CREATED_AT = "created_at";

    // Bảng items
    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID_FK = "user_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DATE = "date";

    private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_CREATED_AT + " TEXT)";
        db.execSQL(createUsersTable);

        String createItemsTable = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID_FK + " INTEGER, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_PRICE + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createItemsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Chuyển đổi định dạng ngày từ dd/MM/yyyy sang yyyy-MM-dd
    public String convertToDbDateFormat(String displayDate) {
        try {
            Date date = displayDateFormat.parse(displayDate);
            return dbDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return displayDate;
        }
    }

    // Chuyển đổi định dạng ngày từ yyyy-MM-dd sang dd/MM/yyyy
    public String convertToDisplayDateFormat(String dbDate) {
        try {
            Date date = dbDateFormat.parse(dbDate);
            return displayDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dbDate;
        }
    }

    // --- Các phương thức cơ bản cho Users ---
    public long insertUser(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    public Cursor queryUser(String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
    }

    // --- Các phương thức cơ bản cho Items ---
    public long insertItem(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(TABLE_ITEMS, null, values);
        db.close();
        return result;
    }

    public int updateItem(ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.update(TABLE_ITEMS, values, whereClause, whereArgs);
        db.close();
        return rowsAffected;
    }

    public int deleteItem(String whereClause, String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_ITEMS, whereClause, whereArgs);
        db.close();
        return rowsAffected;
    }

    public Cursor queryItems(String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ITEMS, null, selection, selectionArgs, null, null, null);
    }

    public void clearItemsTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ITEMS);
        db.close();
    }

    public boolean isTableEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ITEMS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count == 0;
    }
}
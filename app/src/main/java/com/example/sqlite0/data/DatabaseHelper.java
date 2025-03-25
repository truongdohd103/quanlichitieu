package com.example.sqlite0.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.sqlite0.model.Item;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "item_db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_ITEMS = "items";

    // Các cột trong bảng items
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_DATE = "date";

    private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_PRICE + " TEXT, " +
                COLUMN_DATE + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // Chuyển đổi định dạng ngày từ dd/MM/yyyy sang yyyy-MM-dd
    private String convertToDbDateFormat(String displayDate) {
        try {
            Date date = displayDateFormat.parse(displayDate);
            return dbDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return displayDate;
        }
    }

    // Chuyển đổi định dạng ngày từ yyyy-MM-dd sang dd/MM/yyyy
    private String convertToDisplayDateFormat(String dbDate) {
        try {
            Date date = dbDateFormat.parse(dbDate);
            return displayDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dbDate;
        }
    }

    // Thêm một mục chi tiêu vào bảng
    public void addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_CATEGORY, item.getCategory());
        values.put(COLUMN_PRICE, item.getPrice());
        values.put(COLUMN_DATE, convertToDbDateFormat(item.getDate()));
        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    // Cập nhật một mục chi tiêu
    public void updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_CATEGORY, item.getCategory());
        values.put(COLUMN_PRICE, item.getPrice());
        values.put(COLUMN_DATE, convertToDbDateFormat(item.getDate()));
        db.update(TABLE_ITEMS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
    }

    // Xóa một mục chi tiêu
    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Lấy tất cả các mục chi tiêu
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
                int priceIndex = cursor.getColumnIndex(COLUMN_PRICE);
                int dateIndex = cursor.getColumnIndex(COLUMN_DATE);

                if (idIndex == -1 || titleIndex == -1 || categoryIndex == -1 || priceIndex == -1 || dateIndex == -1) {
                    continue;
                }

                String dbDate = cursor.getString(dateIndex);
                String displayDate = convertToDisplayDateFormat(dbDate);

                Item item = new Item(
                        cursor.getInt(idIndex),
                        cursor.getString(titleIndex),
                        cursor.getString(categoryIndex),
                        cursor.getString(priceIndex),
                        displayDate
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }

    // Lấy các mục chi tiêu theo ngày
    public List<Item> getItemsByDate(String date) {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String dbDate = convertToDbDateFormat(date);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_DATE + " = ?", new String[]{dbDate});

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
                int priceIndex = cursor.getColumnIndex(COLUMN_PRICE);
                int dateIndex = cursor.getColumnIndex(COLUMN_DATE);

                if (idIndex == -1 || titleIndex == -1 || categoryIndex == -1 || priceIndex == -1 || dateIndex == -1) {
                    continue;
                }

                String dbDateStored = cursor.getString(dateIndex);
                String displayDate = convertToDisplayDateFormat(dbDateStored);

                Item item = new Item(
                        cursor.getInt(idIndex),
                        cursor.getString(titleIndex),
                        cursor.getString(categoryIndex),
                        cursor.getString(priceIndex),
                        displayDate
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }

    // Lấy các mục chi tiêu theo danh mục
    public List<Item> getItemsByCategory(String category) {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        if (category.equals("All")) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS, null);
        } else {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_CATEGORY + " = ?", new String[]{category});
        }

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
                int priceIndex = cursor.getColumnIndex(COLUMN_PRICE);
                int dateIndex = cursor.getColumnIndex(COLUMN_DATE);

                if (idIndex == -1 || titleIndex == -1 || categoryIndex == -1 || priceIndex == -1 || dateIndex == -1) {
                    continue;
                }

                String dbDate = cursor.getString(dateIndex);
                String displayDate = convertToDisplayDateFormat(dbDate);

                Item item = new Item(
                        cursor.getInt(idIndex),
                        cursor.getString(titleIndex),
                        cursor.getString(categoryIndex),
                        cursor.getString(priceIndex),
                        displayDate
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }

    // Lấy các mục chi tiêu theo khoảng thời gian
    public List<Item> getItemsByDateRange(String startDate, String endDate) {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String dbStartDate = convertToDbDateFormat(startDate);
        String dbEndDate = convertToDbDateFormat(endDate);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_DATE + " BETWEEN ? AND ?",
                new String[]{dbStartDate, dbEndDate});

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
                int priceIndex = cursor.getColumnIndex(COLUMN_PRICE);
                int dateIndex = cursor.getColumnIndex(COLUMN_DATE);

                if (idIndex == -1 || titleIndex == -1 || categoryIndex == -1 || priceIndex == -1 || dateIndex == -1) {
                    continue;
                }

                String dbDate = cursor.getString(dateIndex);
                String displayDate = convertToDisplayDateFormat(dbDate);

                Item item = new Item(
                        cursor.getInt(idIndex),
                        cursor.getString(titleIndex),
                        cursor.getString(categoryIndex),
                        cursor.getString(priceIndex),
                        displayDate
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }

    // Xóa toàn bộ bảng items
    public void clearItemsTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ITEMS);
        db.close();
    }

    // Kiểm tra xem bảng có dữ liệu không
    public boolean isTableEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_ITEMS;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count == 0;
    }
}
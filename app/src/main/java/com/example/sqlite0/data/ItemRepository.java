package com.example.sqlite0.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.example.sqlite0.models.Item;
import java.util.ArrayList;
import java.util.List;

public class ItemRepository {
    private DatabaseHelper dbHelper;

    public ItemRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addItem(Item item, int userId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_ID_FK, userId);
        values.put(DatabaseHelper.COLUMN_TITLE, item.getTitle());
        values.put(DatabaseHelper.COLUMN_CATEGORY, item.getCategory());
        values.put(DatabaseHelper.COLUMN_PRICE, item.getPrice());
        values.put(DatabaseHelper.COLUMN_DATE, dbHelper.convertToDbDateFormat(item.getDate()));
        dbHelper.insertItem(values);
    }

    public void updateItem(Item item) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_ID_FK, item.getUserId());
        values.put(DatabaseHelper.COLUMN_TITLE, item.getTitle());
        values.put(DatabaseHelper.COLUMN_CATEGORY, item.getCategory());
        values.put(DatabaseHelper.COLUMN_PRICE, item.getPrice());
        values.put(DatabaseHelper.COLUMN_DATE, dbHelper.convertToDbDateFormat(item.getDate()));
        dbHelper.updateItem(values, DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(item.getId())});
    }

    public void deleteItem(int id) {
        dbHelper.deleteItem(DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<Item> getAllItems(int userId) {
        List<Item> itemList = new ArrayList<>();
        String selection = DatabaseHelper.COLUMN_USER_ID_FK + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = dbHelper.queryItems(selection, selectionArgs);
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                int userIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID_FK);
                int titleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE);
                int categoryIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY);
                int priceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRICE);
                int dateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE);

                if (idIndex == -1 || userIdIndex == -1 || titleIndex == -1 || categoryIndex == -1 || priceIndex == -1 || dateIndex == -1) {
                    continue;
                }

                String dbDate = cursor.getString(dateIndex);
                String displayDate = dbHelper.convertToDisplayDateFormat(dbDate);

                Item item = new Item(
                        cursor.getInt(idIndex),
                        cursor.getInt(userIdIndex),
                        cursor.getString(titleIndex),
                        cursor.getString(categoryIndex),
                        cursor.getString(priceIndex),
                        displayDate
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    public List<Item> getItemsByDate(String date, int userId) {
        List<Item> itemList = new ArrayList<>();
        String dbDate = dbHelper.convertToDbDateFormat(date);
        String selection = DatabaseHelper.COLUMN_DATE + " = ? AND " + DatabaseHelper.COLUMN_USER_ID_FK + " = ?";
        String[] selectionArgs = {dbDate, String.valueOf(userId)};
        Cursor cursor = dbHelper.queryItems(selection, selectionArgs);
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                int userIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID_FK);
                int titleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE);
                int categoryIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY);
                int priceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRICE);
                int dateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE);

                if (idIndex == -1 || userIdIndex == -1 || titleIndex == -1 || categoryIndex == -1 || priceIndex == -1 || dateIndex == -1) {
                    continue;
                }

                String dbDateStored = cursor.getString(dateIndex);
                String displayDate = dbHelper.convertToDisplayDateFormat(dbDateStored);

                Item item = new Item(
                        cursor.getInt(idIndex),
                        cursor.getInt(userIdIndex),
                        cursor.getString(titleIndex),
                        cursor.getString(categoryIndex),
                        cursor.getString(priceIndex),
                        displayDate
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    public List<Item> getItemsByCategory(String category, int userId) {
        List<Item> itemList = new ArrayList<>();
        String selection;
        String[] selectionArgs;
        if (category.equals("All")) {
            selection = DatabaseHelper.COLUMN_USER_ID_FK + " = ?";
            selectionArgs = new String[]{String.valueOf(userId)};
        } else {
            selection = DatabaseHelper.COLUMN_CATEGORY + " = ? AND " + DatabaseHelper.COLUMN_USER_ID_FK + " = ?";
            selectionArgs = new String[]{category, String.valueOf(userId)};
        }
        Cursor cursor = dbHelper.queryItems(selection, selectionArgs);
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                int userIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID_FK);
                int titleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE);
                int categoryIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY);
                int priceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRICE);
                int dateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE);

                if (idIndex == -1 || userIdIndex == -1 || titleIndex == -1 || categoryIndex == -1 || priceIndex == -1 || dateIndex == -1) {
                    continue;
                }

                String dbDate = cursor.getString(dateIndex);
                String displayDate = dbHelper.convertToDisplayDateFormat(dbDate);

                Item item = new Item(
                        cursor.getInt(idIndex),
                        cursor.getInt(userIdIndex),
                        cursor.getString(titleIndex),
                        cursor.getString(categoryIndex),
                        cursor.getString(priceIndex),
                        displayDate
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    public List<Item> getItemsByDateRange(String startDate, String endDate, int userId) {
        List<Item> itemList = new ArrayList<>();
        String dbStartDate = dbHelper.convertToDbDateFormat(startDate);
        String dbEndDate = dbHelper.convertToDbDateFormat(endDate);
        String selection = DatabaseHelper.COLUMN_DATE + " BETWEEN ? AND ? AND " + DatabaseHelper.COLUMN_USER_ID_FK + " = ?";
        String[] selectionArgs = {dbStartDate, dbEndDate, String.valueOf(userId)};
        Cursor cursor = dbHelper.queryItems(selection, selectionArgs);
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                int userIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID_FK);
                int titleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE);
                int categoryIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY);
                int priceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRICE);
                int dateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE);

                if (idIndex == -1 || userIdIndex == -1 || titleIndex == -1 || categoryIndex == -1 || priceIndex == -1 || dateIndex == -1) {
                    continue;
                }

                String dbDate = cursor.getString(dateIndex);
                String displayDate = dbHelper.convertToDisplayDateFormat(dbDate);

                Item item = new Item(
                        cursor.getInt(idIndex),
                        cursor.getInt(userIdIndex),
                        cursor.getString(titleIndex),
                        cursor.getString(categoryIndex),
                        cursor.getString(priceIndex),
                        displayDate
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    public void clearItemsTable() {
        dbHelper.clearItemsTable();
    }

    public boolean isTableEmpty() {
        return dbHelper.isTableEmpty();
    }
}
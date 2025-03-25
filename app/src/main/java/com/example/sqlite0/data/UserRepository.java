package com.example.sqlite0.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.sqlite0.models.User;

public class UserRepository {

    private DatabaseHelper dbHelper;

    public UserRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Phương thức login trả về userId (int)
    public int login(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "users",
                new String[]{"id"},
                "username = ? AND password = ?",
                new String[]{username, password},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            cursor.close();
            return userId;
        }
        if (cursor != null) {
            cursor.close();
        }
        return -1;
    }

    // Lấy thông tin người dùng theo userId
    public User getUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "users",
                new String[]{"id", "username", "email", "password"},
                "id = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("username")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password"))
            );
            cursor.close();
            return user;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Kiểm tra xem username đã tồn tại chưa
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "users",
                new String[]{"id"},
                "username = ?",
                new String[]{username},
                null, null, null
        );

        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    // Đăng ký người dùng mới
    public long register(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        return db.insert("users", null, values);
    }
}
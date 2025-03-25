package com.example.sqlite0.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_FIRST_LOGIN = "is_first_login";
    private final SharedPreferences sharedPreferences;

    public PreferenceUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserId(int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public void setFirstLogin(boolean isFirstLogin) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_FIRST_LOGIN, isFirstLogin);
        editor.apply();
    }

    public boolean isFirstLogin() {
        return sharedPreferences.getBoolean(KEY_IS_FIRST_LOGIN, true);
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
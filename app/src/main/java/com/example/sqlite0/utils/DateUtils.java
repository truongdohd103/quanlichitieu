package com.example.sqlite0.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // Kiểm tra định dạng ngày
    public static boolean isValidDate(String dateStr) {
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Chuyển đổi Date thành String
    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    // Chuyển đổi String thành Date
    public static Date parseDate(String dateStr) throws ParseException {
        return dateFormat.parse(dateStr);
    }
}
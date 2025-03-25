package com.example.sqlite0.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ValidationUtils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // Kiểm tra các trường có rỗng không
    public static boolean isEmpty(String... fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

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

    // Kiểm tra giá trị giá
    public static boolean isValidPrice(String priceStr, double minValue) {
        try {
            double priceValue = Double.parseDouble(priceStr);
            return priceValue > minValue;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
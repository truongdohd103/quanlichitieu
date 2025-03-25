package com.example.sqlite0.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sqlite0.MainActivity;
import com.example.sqlite0.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Lấy trạng thái đăng nhập từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        // Delay 2 giây trước khi chuyển màn hình
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (userId == -1) {
                // Chưa đăng nhập, chuyển đến LoginActivity
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            } else {
                // Đã đăng nhập, chuyển đến MainActivity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000); // 2000ms = 2 giây
    }
}
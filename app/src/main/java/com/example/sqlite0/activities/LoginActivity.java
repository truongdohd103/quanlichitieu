package com.example.sqlite0.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sqlite0.MainActivity;
import com.example.sqlite0.R;
import com.example.sqlite0.data.UserRepository;
import com.example.sqlite0.utils.PreferenceUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private UserRepository userRepository;
    private PreferenceUtils preferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        userRepository = new UserRepository(this);
        preferenceUtils = new PreferenceUtils(this);

        // Kiểm tra trạng thái đăng nhập
        int userId = preferenceUtils.getUserId();
        if (userId != -1) {
            // Nếu đã đăng nhập, chuyển hướng đến MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int loginUserId = userRepository.login(username, password);
            if (loginUserId != -1) {
                // Đăng nhập thành công, lưu userId vào PreferenceUtils
                preferenceUtils.saveUserId(loginUserId);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
package com.example.sqlite0.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sqlite0.R;
import com.example.sqlite0.data.UserRepository;
import com.example.sqlite0.models.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etEmail;
    private Button btnRegister;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etEmail = findViewById(R.id.et_email);
        btnRegister = findViewById(R.id.btn_register);

        userRepository = new UserRepository(this);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                Toast.makeText(RegisterActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem username đã tồn tại chưa
            if (userRepository.isUsernameExists(username)) {
                Toast.makeText(RegisterActivity.this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng User
            User user = new User(0, username, email, password); // ID sẽ được tự động tạo bởi SQLite
            // Gọi phương thức register
            long result = userRepository.register(user);
            if (result > 0) {
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
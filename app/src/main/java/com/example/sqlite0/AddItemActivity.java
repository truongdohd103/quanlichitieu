package com.example.sqlite0;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sqlite0.data.DatabaseHelper;
import com.example.sqlite0.model.Item;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private EditText etTitle, etPrice, etDate;
    private Button btnAdd, btnCancel;
    private DatabaseHelper databaseHelper;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Khởi tạo các thành phần giao diện
        spinnerCategory = findViewById(R.id.spinner_category);
        etTitle = findViewById(R.id.et_title);
        etPrice = findViewById(R.id.et_price);
        etDate = findViewById(R.id.et_date);
        btnAdd = findViewById(R.id.btn_add);
        btnCancel = findViewById(R.id.btn_cancel);

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Thiết lập Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerCategory.setAdapter(adapter);

        // Vô hiệu hóa nhập tay cho etDate
        etDate.setKeyListener(null);

        // Xử lý sự kiện chọn ngày
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddItemActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        etDate.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Xử lý sự kiện khi nhấn nút Add
        btnAdd.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String priceStr = etPrice.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            // Kiểm tra các trường có rỗng không
            if (title.isEmpty() || priceStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra định dạng ngày
            if (!isValidDate(date)) {
                Toast.makeText(this, "Ngày không hợp lệ, vui lòng chọn lại (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra giá trị giá
            double priceValue;
            try {
                priceValue = Double.parseDouble(priceStr);
                if (priceValue <= 0) {
                    Toast.makeText(this, "Giá phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá không hợp lệ, vui lòng nhập số", Toast.LENGTH_SHORT).show();
                return;
            }

            String price = priceStr + "K";
            Item item = new Item(title, category, price, date);
            databaseHelper.addItem(item);
            Toast.makeText(this, "Đã thêm mục chi tiêu", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Xử lý sự kiện khi nhấn nút Cancel
        btnCancel.setOnClickListener(v -> finish());
    }

    // Kiểm tra định dạng ngày
    private boolean isValidDate(String dateStr) {
        try {
            dateFormat.setLenient(false); // Không cho phép phân tích ngày không hợp lệ
            dateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
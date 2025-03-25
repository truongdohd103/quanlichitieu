package com.example.sqlite0;

import android.app.AlertDialog;
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

public class EditItemActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private EditText etTitle, etPrice, etDate;
    private Button btnUpdate, btnRemove, btnBack;
    private DatabaseHelper databaseHelper;
    private int itemId;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // Khởi tạo các thành phần giao diện
        spinnerCategory = findViewById(R.id.spinner_category);
        etTitle = findViewById(R.id.et_title);
        etPrice = findViewById(R.id.et_price);
        etDate = findViewById(R.id.et_date);
        btnUpdate = findViewById(R.id.btn_update);
        btnRemove = findViewById(R.id.btn_remove);
        btnBack = findViewById(R.id.btn_back);

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Lấy dữ liệu từ Intent
        itemId = getIntent().getIntExtra("item_id", -1);
        String title = getIntent().getStringExtra("item_title");
        String category = getIntent().getStringExtra("item_category");
        String price = getIntent().getStringExtra("item_price").replace("K", "");
        String date = getIntent().getStringExtra("item_date");

        // Hiển thị dữ liệu hiện tại
        etTitle.setText(title);
        etPrice.setText(price);
        etDate.setText(date);

        // Thiết lập Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerCategory.setAdapter(adapter);

        // Đặt danh mục hiện tại trong Spinner
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(category)) {
                spinnerCategory.setSelection(i);
                break;
            }
        }

        // Vô hiệu hóa nhập tay cho etDate
        etDate.setKeyListener(null);

        // Xử lý sự kiện chọn ngày
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditItemActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String dateStr = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        etDate.setText(dateStr);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Xử lý sự kiện khi nhấn nút Update
        btnUpdate.setOnClickListener(v -> {
            String newTitle = etTitle.getText().toString().trim();
            String newCategory = spinnerCategory.getSelectedItem().toString();
            String newPriceStr = etPrice.getText().toString().trim();
            String newDate = etDate.getText().toString().trim();

            // Kiểm tra các trường có rỗng không
            if (newTitle.isEmpty() || newPriceStr.isEmpty() || newDate.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra định dạng ngày
            if (!isValidDate(newDate)) {
                Toast.makeText(this, "Ngày không hợp lệ, vui lòng chọn lại (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra giá trị giá
            double priceValue;
            try {
                priceValue = Double.parseDouble(newPriceStr);
                if (priceValue <= 0) {
                    Toast.makeText(this, "Giá phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá không hợp lệ, vui lòng nhập số", Toast.LENGTH_SHORT).show();
                return;
            }

            String newPrice = newPriceStr + "K";
            Item updatedItem = new Item(itemId, newTitle, newCategory, newPrice, newDate);
            databaseHelper.updateItem(updatedItem);
            Toast.makeText(this, "Đã cập nhật mục chi tiêu", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Xử lý sự kiện khi nhấn nút Remove
        btnRemove.setOnClickListener(v -> {
            // Hiển thị AlertDialog để xác nhận xóa
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa mục chi tiêu này không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        databaseHelper.deleteItem(itemId);
                        Toast.makeText(this, "Đã xóa mục chi tiêu", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Xử lý sự kiện khi nhấn nút Back
        btnBack.setOnClickListener(v -> finish());
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
package com.example.sqlite0.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sqlite0.R;
import com.example.sqlite0.data.ItemRepository;
import com.example.sqlite0.models.Item;
import com.example.sqlite0.utils.PreferenceUtils;
import com.example.sqlite0.utils.ValidationUtils;

import java.util.Calendar;
import java.util.Locale;

public class EditItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Spinner spinnerCategory = findViewById(R.id.spinner_category);
        EditText etTitle = findViewById(R.id.et_title);
        EditText etPrice = findViewById(R.id.et_price);
        EditText etDate = findViewById(R.id.et_date);
        Button btnUpdate = findViewById(R.id.btn_update);
        Button btnRemove = findViewById(R.id.btn_remove);
        Button btnBack = findViewById(R.id.btn_back);

        ItemRepository itemRepository = new ItemRepository(this);
        PreferenceUtils preferenceUtils = new PreferenceUtils(this);

        int itemId = getIntent().getIntExtra("item_id", -1);
        String title = getIntent().getStringExtra("item_title");
        String category = getIntent().getStringExtra("item_category");
        String price = getIntent().getStringExtra("item_price");
        String date = getIntent().getStringExtra("item_date");

        // Kiểm tra null trước khi gọi replace
        if (price != null) {
            price = price.replace("K", "");
        } else {
            price = ""; // Giá trị mặc định nếu price là null
        }

        etTitle.setText(title);
        etPrice.setText(price);
        etDate.setText(date);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerCategory.setAdapter(adapter);

        // Kiểm tra null khi lấy item từ adapter
        for (int i = 0; i < adapter.getCount(); i++) {
            CharSequence item = adapter.getItem(i);
            if (item != null && item.toString().equals(category)) {
                spinnerCategory.setSelection(i);
                break;
            }
        }

        etDate.setKeyListener(null);

        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditItemActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String dateStr = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        etDate.setText(dateStr);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        btnUpdate.setOnClickListener(v -> {
            int userId = preferenceUtils.getUserId();
            if (userId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }

            String newTitle = etTitle.getText().toString().trim();
            String newCategory = spinnerCategory.getSelectedItem().toString();
            String newPriceStr = etPrice.getText().toString().trim();
            String newDate = etDate.getText().toString().trim();

            if (ValidationUtils.isEmpty(newTitle, newPriceStr, newDate)) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!ValidationUtils.isValidDate(newDate)) {
                Toast.makeText(this, "Ngày không hợp lệ, vui lòng chọn lại (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!ValidationUtils.isValidPrice(newPriceStr, 0)) {
                Toast.makeText(this, "Giá phải lớn hơn 0 và là số hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            String newPrice = newPriceStr + "K";
            Item updatedItem = new Item(itemId, userId, newTitle, newCategory, newPrice, newDate);
            itemRepository.updateItem(updatedItem);
            Toast.makeText(this, "Đã cập nhật mục chi tiêu", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnRemove.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa mục chi tiêu này không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    itemRepository.deleteItem(itemId);
                    Toast.makeText(this, "Đã xóa mục chi tiêu", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .show());

        btnBack.setOnClickListener(v -> finish());
    }
}
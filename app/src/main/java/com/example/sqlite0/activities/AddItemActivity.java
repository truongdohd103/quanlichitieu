package com.example.sqlite0.activities;

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

public class AddItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        Spinner spinnerCategory = findViewById(R.id.spinner_category);
        EditText etTitle = findViewById(R.id.et_title);
        EditText etPrice = findViewById(R.id.et_price);
        EditText etDate = findViewById(R.id.et_date);
        Button btnAdd = findViewById(R.id.btn_add);
        Button btnCancel = findViewById(R.id.btn_cancel);

        ItemRepository itemRepository = new ItemRepository(this);
        PreferenceUtils preferenceUtils = new PreferenceUtils(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerCategory.setAdapter(adapter);

        etDate.setKeyListener(null);

        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddItemActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        etDate.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        btnAdd.setOnClickListener(v -> {
            int userId = preferenceUtils.getUserId();
            if (userId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = etTitle.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String priceStr = etPrice.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            // Kiểm tra các trường có rỗng không
            if (ValidationUtils.isEmpty(title, priceStr, date)) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra định dạng ngày
            if (!ValidationUtils.isValidDate(date)) {
                Toast.makeText(this, "Ngày không hợp lệ, vui lòng chọn lại (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra giá trị giá
            if (!ValidationUtils.isValidPrice(priceStr, 0)) {
                Toast.makeText(this, "Giá phải lớn hơn 0 và là số hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            String price = priceStr + "K";
            Item item = new Item(userId, title, category, price, date);
            itemRepository.addItem(item, userId);
            Toast.makeText(this, "Đã thêm mục chi tiêu", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
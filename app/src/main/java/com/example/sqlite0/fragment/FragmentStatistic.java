package com.example.sqlite0.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sqlite0.data.DatabaseHelper;
import com.example.sqlite0.R;
import com.example.sqlite0.adapter.ItemAdapter;
import com.example.sqlite0.model.Item;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;

public class FragmentStatistic extends Fragment {

    private SearchView searchView;
    private Spinner spinnerCategories;
    private EditText etInput1, etInput2;
    private Button btnSearch;
    private TextView tvTotalExpense;
    private RecyclerView rvItems;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private List<Item> filteredList;
    private DatabaseHelper databaseHelper;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        // Khởi tạo các thành phần giao diện
        searchView = view.findViewById(R.id.search_view);
        spinnerCategories = view.findViewById(R.id.spinner_categories_with_all);
        etInput1 = view.findViewById(R.id.et_input_1);
        etInput2 = view.findViewById(R.id.et_input_2);
        btnSearch = view.findViewById(R.id.btn_search);
        tvTotalExpense = view.findViewById(R.id.tv_total_expense);
        rvItems = view.findViewById(R.id.rv_expenses);
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách và adapter
        itemList = new ArrayList<>();
        filteredList = new ArrayList<>();
        itemAdapter = new ItemAdapter(getContext(), filteredList);
        rvItems.setAdapter(itemAdapter);

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        // Thiết lập Spinner với categories_with_all
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.categories_with_all, // Sử dụng danh sách có "All"
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerCategories.setAdapter(adapter);

        // Xử lý sự kiện chọn ngày cho etInput1
        etInput1.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        etInput1.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Xử lý sự kiện chọn ngày cho etInput2
        etInput2.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        etInput2.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Lấy dữ liệu ban đầu
        loadData();

        // Xử lý sự kiện khi nhấn nút Search
        btnSearch.setOnClickListener(v -> {
            String selectedCategory = spinnerCategories.getSelectedItem().toString().trim();
            String startDate = etInput1.getText().toString().trim();
            String endDate = etInput2.getText().toString().trim();

            // Log để kiểm tra giá trị selectedCategory
            System.out.println("Selected Category: " + selectedCategory);

            // Kiểm tra định dạng ngày nếu có nhập
            if (!startDate.isEmpty() && !isValidDate(startDate)) {
                Toast.makeText(getContext(), "Ngày bắt đầu không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!endDate.isEmpty() && !isValidDate(endDate)) {
                Toast.makeText(getContext(), "Ngày kết thúc không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lọc theo ngày và danh mục
            filteredList.clear();
            Date start = null;
            Date end = null;

            try {
                // Xác định ngày bắt đầu và ngày kết thúc
                if (!startDate.isEmpty()) {
                    start = dateFormat.parse(startDate);
                }
                if (!endDate.isEmpty()) {
                    end = dateFormat.parse(endDate);
                }

                // Nếu không nhập ngày nào, lấy tất cả các mục phù hợp với danh mục
                if (start == null && end == null) {
                    System.out.println("No date range specified, filtering by category only.");
                    for (Item item : itemList) {
                        String itemCategory = item.getCategory().trim();
                        System.out.println("Item: " + item.getTitle() + ", Category: " + itemCategory);
                        if (selectedCategory.equals("All") || itemCategory.equals(selectedCategory)) {
                            filteredList.add(item);
                        }
                    }
                } else {
                    // Nếu chỉ nhập ngày bắt đầu, lấy từ ngày bắt đầu đến hiện tại
                    if (start != null && end == null) {
                        end = new Date(); // Ngày hiện tại
                    }
                    // Nếu chỉ nhập ngày kết thúc, lấy từ ngày đầu tiên đến ngày kết thúc
                    if (start == null && end != null) {
                        start = getEarliestDate();
                    }

                    // Lọc các mục chi tiêu trong khoảng thời gian
                    System.out.println("Filtering by date range: " + start + " to " + end);
                    for (Item item : itemList) {
                        Date itemDate = dateFormat.parse(item.getDate());
                        if (itemDate.compareTo(start) >= 0 && itemDate.compareTo(end) <= 0) {
                            String itemCategory = item.getCategory().trim();
                            System.out.println("Item: " + item.getTitle() + ", Category: " + itemCategory + ", Date: " + item.getDate());
                            if (selectedCategory.equals("All") || itemCategory.equals(selectedCategory)) {
                                filteredList.add(item);
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Log danh sách đã lọc
            System.out.println("Filtered List Size: " + filteredList.size());
            for (Item item : filteredList) {
                System.out.println("Filtered Item: " + item.getTitle() + ", Category: " + item.getCategory());
            }

            // Sắp xếp theo ngày (mới nhất đến cũ nhất)
            Collections.sort(filteredList, (item1, item2) -> {
                try {
                    Date date1 = dateFormat.parse(item1.getDate());
                    Date date2 = dateFormat.parse(item2.getDate());
                    return date2.compareTo(date1); // Sắp xếp giảm dần (mới nhất trước)
                } catch (ParseException e) {
                    return 0;
                }
            });

            itemAdapter.updateData(filteredList);
            updateTotalExpense();
        });

        // Xử lý tìm kiếm thời gian thực với SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItemsByTitle(newText);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // Làm mới dữ liệu khi fragment được hiển thị
    }

    private void loadData() {
        // Lấy dữ liệu từ SQLite
        itemList = databaseHelper.getAllItems();

        // Log để kiểm tra category của các mục
        for (Item item : itemList) {
            System.out.println("Item: " + item.getTitle() + ", Category: " + item.getCategory());
        }

        // Sắp xếp theo ngày (mới nhất đến cũ nhất)
        Collections.sort(itemList, (item1, item2) -> {
            try {
                Date date1 = dateFormat.parse(item1.getDate());
                Date date2 = dateFormat.parse(item2.getDate());
                return date2.compareTo(date1); // Sắp xếp giảm dần (mới nhất trước)
            } catch (ParseException e) {
                return 0;
            }
        });

        filteredList = new ArrayList<>(itemList);
        itemAdapter.updateData(filteredList);
        updateTotalExpense();
    }

    // Cập nhật tổng chi tiêu
    private void updateTotalExpense() {
        double total = 0;
        for (Item item : filteredList) {
            try {
                String priceStr = item.getPrice().replace("K", "").trim();
                total += Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        tvTotalExpense.setText("Tổng tiền: " + String.format("%.0fK", total));
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

    // Lọc các mục chi tiêu theo tiêu đề (thời gian thực)
    private void filterItemsByTitle(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(itemList);
        } else {
            for (Item item : itemList) {
                if (item.getTitle().toLowerCase().startsWith(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        itemAdapter.updateData(filteredList);
        updateTotalExpense();
    }

    // Lấy ngày sớm nhất từ danh sách các mục chi tiêu
    private Date getEarliestDate() {
        if (itemList.isEmpty()) {
            return new Date(); // Nếu không có dữ liệu, trả về ngày hiện tại
        }

        Date earliest = null;
        try {
            for (Item item : itemList) {
                Date itemDate = dateFormat.parse(item.getDate());
                if (earliest == null || itemDate.before(earliest)) {
                    earliest = itemDate;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return earliest != null ? earliest : new Date();
    }
}
package com.example.sqlite0.fragments;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sqlite0.R;
import com.example.sqlite0.adapters.ItemAdapter;
import com.example.sqlite0.models.Item;
import com.example.sqlite0.utils.PreferenceUtils;
import com.example.sqlite0.utils.ValidationUtils;
import com.example.sqlite0.viewmodels.ItemViewModel;
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
    private ItemViewModel itemViewModel;
    private PreferenceUtils preferenceUtils;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        searchView = view.findViewById(R.id.search_view);
        spinnerCategories = view.findViewById(R.id.spinner_categories_with_all);
        etInput1 = view.findViewById(R.id.et_input_1);
        etInput2 = view.findViewById(R.id.et_input_2);
        btnSearch = view.findViewById(R.id.btn_search);
        tvTotalExpense = view.findViewById(R.id.tv_total_expense);
        rvItems = view.findViewById(R.id.rv_expenses);
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();
        filteredList = new ArrayList<>();
        itemAdapter = new ItemAdapter(getContext(), filteredList);
        rvItems.setAdapter(itemAdapter);

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        preferenceUtils = new PreferenceUtils(getContext());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.categories_with_all,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerCategories.setAdapter(adapter);

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

        loadData();

        btnSearch.setOnClickListener(v -> {
            int userId = preferenceUtils.getUserId();
            if (userId == -1) return;

            String selectedCategory = spinnerCategories.getSelectedItem().toString().trim();
            String startDate = etInput1.getText().toString().trim();
            String endDate = etInput2.getText().toString().trim();

            if (!startDate.isEmpty() && !ValidationUtils.isValidDate(startDate)) {
                Toast.makeText(getContext(), "Ngày bắt đầu không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!endDate.isEmpty() && !ValidationUtils.isValidDate(endDate)) {
                Toast.makeText(getContext(), "Ngày kết thúc không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }

            filteredList.clear();
            Date start = null;
            Date end = null;

            try {
                if (!startDate.isEmpty()) {
                    start = dateFormat.parse(startDate);
                }
                if (!endDate.isEmpty()) {
                    end = dateFormat.parse(endDate);
                }

                if (start == null && end == null) {
                    filteredList.addAll(itemList);
                    filterByCategory(selectedCategory);
                } else {
                    if (start != null && end == null) {
                        end = new Date();
                    }
                    if (start == null && end != null) {
                        start = getEarliestDate();
                    }

                    List<Item> rangeItems = new ArrayList<>();
                    for (Item item : itemList) {
                        Date itemDate = dateFormat.parse(item.getDate());
                        if (itemDate.compareTo(start) >= 0 && itemDate.compareTo(end) <= 0) {
                            rangeItems.add(item);
                        }
                    }
                    filteredList.addAll(rangeItems);
                    filterByCategory(selectedCategory);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Collections.sort(filteredList, (item1, item2) -> {
                try {
                    Date date1 = dateFormat.parse(item1.getDate());
                    Date date2 = dateFormat.parse(item2.getDate());
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    return 0;
                }
            });

            itemAdapter.updateData(filteredList);
            updateTotalExpense();
        });

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
        loadData();
    }

    private void loadData() {
        int userId = preferenceUtils.getUserId();
        if (userId == -1) return;

        itemViewModel.getItems(userId).observe(getViewLifecycleOwner(), items -> {
            itemList.clear();
            itemList.addAll(items);

            Collections.sort(itemList, (item1, item2) -> {
                try {
                    Date date1 = dateFormat.parse(item1.getDate());
                    Date date2 = dateFormat.parse(item2.getDate());
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    return 0;
                }
            });

            filteredList.clear();
            filteredList.addAll(itemList);
            itemAdapter.updateData(filteredList);
            updateTotalExpense();
        });
    }

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

    private void filterByCategory(String selectedCategory) {
        if (!selectedCategory.equals("All")) {
            List<Item> tempList = new ArrayList<>(filteredList);
            filteredList.clear();
            for (Item item : tempList) {
                if (item.getCategory().trim().equals(selectedCategory)) {
                    filteredList.add(item);
                }
            }
        }
    }

    private Date getEarliestDate() {
        if (itemList.isEmpty()) {
            return new Date();
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
package com.example.sqlite0.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sqlite0.data.DatabaseHelper;
import com.example.sqlite0.R;
import com.example.sqlite0.adapter.ItemAdapter;
import com.example.sqlite0.model.Item;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentToday extends Fragment {

    private RecyclerView rvItems;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private DatabaseHelper databaseHelper;
    private TextView tv_total_expense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        tv_total_expense = view.findViewById(R.id.tv_total_expense);
        // Khởi tạo RecyclerView
        rvItems = view.findViewById(R.id.rv_expenses);
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách và adapter
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(getContext(), itemList);
        rvItems.setAdapter(itemAdapter);

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        // Lấy dữ liệu ban đầu
        loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // Làm mới dữ liệu khi fragment được hiển thị
    }

    private void loadData() {
        // Lấy ngày hiện tại
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Lấy dữ liệu từ SQLite
        itemList = databaseHelper.getItemsByDate(currentDate);
        itemAdapter.updateData(itemList);

        // Tính tổng tiền chi tiêu
        double sum = 0;
        for (Item i : itemList) {
            try {
                // Loại bỏ "K" và chuyển thành số
                String priceStr = i.getPrice().replace("K", "").trim();
                sum += Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Hiển thị tổng tiền trên TextView
        tv_total_expense.setText("Tổng tiền: " + String.format("%.0fK", sum));
    }
}
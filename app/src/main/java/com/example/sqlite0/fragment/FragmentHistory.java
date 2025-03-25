package com.example.sqlite0.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentHistory extends Fragment {

    private RecyclerView rvItems;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private DatabaseHelper databaseHelper;
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

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
        // Lấy dữ liệu từ SQLite
        itemList = databaseHelper.getAllItems();

        // Sắp xếp theo ngày (mới nhất đến cũ nhất)
        Collections.sort(itemList, (item1, item2) -> {
            try {
                Date date1 = displayDateFormat.parse(item1.getDate());
                Date date2 = displayDateFormat.parse(item2.getDate());
                return date2.compareTo(date1); // Sắp xếp giảm dần (mới nhất trước)
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });

        itemAdapter.updateData(itemList);
    }
}
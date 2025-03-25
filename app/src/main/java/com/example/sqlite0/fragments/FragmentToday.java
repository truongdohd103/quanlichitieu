package com.example.sqlite0.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sqlite0.R;
import com.example.sqlite0.adapters.ItemAdapter;
import com.example.sqlite0.models.Item;
import com.example.sqlite0.utils.PreferenceUtils;
import com.example.sqlite0.viewmodels.ItemViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentToday extends Fragment {

    private RecyclerView rvItems;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private TextView tvTotalExpense;
    private ItemViewModel itemViewModel;
    private PreferenceUtils preferenceUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        tvTotalExpense = view.findViewById(R.id.tv_total_expense);
        rvItems = view.findViewById(R.id.rv_expenses);
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(getContext(), itemList);
        rvItems.setAdapter(itemAdapter);

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        preferenceUtils = new PreferenceUtils(getContext());

        loadData();
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        itemViewModel.getItemsByDate(currentDate, userId).observe(getViewLifecycleOwner(), items -> {
            itemList.clear();
            itemList.addAll(items);
            itemAdapter.updateData(itemList);

            double sum = 0;
            for (Item i : itemList) {
                try {
                    String priceStr = i.getPrice().replace("K", "").trim();
                    sum += Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            tvTotalExpense.setText("Tổng tiền: " + String.format("%.0fK", sum));
        });
    }
}
package com.example.sqlite0.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sqlite0.R;
import com.example.sqlite0.adapters.ItemAdapter;
import com.example.sqlite0.models.Item;
import com.example.sqlite0.utils.PreferenceUtils;
import com.example.sqlite0.viewmodels.ItemViewModel;
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
    private ItemViewModel itemViewModel;
    private PreferenceUtils preferenceUtils;
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

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

        itemViewModel.getItems(userId).observe(getViewLifecycleOwner(), items -> {
            itemList.clear();
            itemList.addAll(items);

            Collections.sort(itemList, (item1, item2) -> {
                try {
                    Date date1 = displayDateFormat.parse(item1.getDate());
                    Date date2 = displayDateFormat.parse(item2.getDate());
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            });

            itemAdapter.updateData(itemList);
        });
    }
}
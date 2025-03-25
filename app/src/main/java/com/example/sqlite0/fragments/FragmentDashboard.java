package com.example.sqlite0.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.sqlite0.R;
import com.example.sqlite0.models.Item;
import com.example.sqlite0.utils.PreferenceUtils;
import com.example.sqlite0.viewmodels.ItemViewModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentDashboard extends Fragment {

    private TextView tvTotalMonth;
    private ItemViewModel itemViewModel;
    private PreferenceUtils preferenceUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvTotalMonth = view.findViewById(R.id.tv_total_month);
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = dateFormat.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = dateFormat.format(calendar.getTime());

        itemViewModel.getItems(userId).observe(getViewLifecycleOwner(), items -> {
            double total = 0;
            try {
                Date start = dateFormat.parse(startDate);
                Date end = dateFormat.parse(endDate);
                for (Item item : items) {
                    Date itemDate = dateFormat.parse(item.getDate());
                    if (itemDate.compareTo(start) >= 0 && itemDate.compareTo(end) <= 0) {
                        String priceStr = item.getPrice().replace("K", "").trim();
                        total += Double.parseDouble(priceStr);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvTotalMonth.setText("Tổng chi tiêu tháng này: " + String.format("%.0fK", total));
        });
    }
}
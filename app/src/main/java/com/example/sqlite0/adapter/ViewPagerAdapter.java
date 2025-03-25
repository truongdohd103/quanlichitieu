package com.example.sqlite0.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.sqlite0.fragment.FragmentHistory;
import com.example.sqlite0.fragment.FragmentStatistic;
import com.example.sqlite0.fragment.FragmentToday;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new FragmentToday();
            case 1: return new FragmentHistory();
            case 2: return new FragmentStatistic();
            default: return new FragmentToday();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}

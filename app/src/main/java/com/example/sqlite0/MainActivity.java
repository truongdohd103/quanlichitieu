package com.example.sqlite0;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.example.sqlite0.adapter.ViewPagerAdapter;
import com.example.sqlite0.data.DatabaseHelper;
import com.example.sqlite0.model.Item;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private FloatingActionButton floatingActionButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);
        floatingActionButton = findViewById(R.id.fab_add);

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Kiểm tra xem bảng có dữ liệu không, nếu không thì thêm dữ liệu mẫu (Phương Pháp 2)
        if (databaseHelper.isTableEmpty()) {
            databaseHelper.addItem(new Item("Quan bo", "Mua sắm", "500K", "25/03/2025"));
            databaseHelper.addItem(new Item("Tiền điện", "Tiền điện", "1200K", "24/03/2025"));
            databaseHelper.addItem(new Item("Tiền nhà", "Tiền nhà", "5000K", "23/03/2025"));
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.mToday).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.mHistory).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.mSearch).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.mToday) viewPager.setCurrentItem(0);
                if (item.getItemId() == R.id.mHistory) viewPager.setCurrentItem(1);
                if (item.getItemId() == R.id.mSearch) viewPager.setCurrentItem(2);
                return true;
            }
        });
    }
}
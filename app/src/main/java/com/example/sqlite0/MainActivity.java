package com.example.sqlite0;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.sqlite0.activities.AddItemActivity;
import com.example.sqlite0.activities.LoginActivity;
import com.example.sqlite0.adapters.ViewPagerAdapter;
import com.example.sqlite0.data.ItemRepository;
import com.example.sqlite0.fragments.FragmentDashboard;
import com.example.sqlite0.fragments.FragmentHistory;
import com.example.sqlite0.fragments.FragmentProfile;
import com.example.sqlite0.fragments.FragmentStatistic;
import com.example.sqlite0.fragments.FragmentToday;
import com.example.sqlite0.models.Item;
import com.example.sqlite0.utils.PreferenceUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private FloatingActionButton floatingActionButton;
    private PreferenceUtils preferenceUtils;
    private ItemRepository itemRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);
        floatingActionButton = findViewById(R.id.fab_add);

        preferenceUtils = new PreferenceUtils(this);
        itemRepository = new ItemRepository(this);

        // Kiểm tra trạng thái đăng nhập
        int userId = preferenceUtils.getUserId();
        if (userId == -1) {
            // Nếu chưa đăng nhập, chuyển hướng về LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Thêm dữ liệu mặc định nếu đây là lần đăng nhập đầu tiên
//        if (preferenceUtils.isFirstLogin() && itemRepository.isTableEmpty()) {
//            itemRepository.addItem(new Item(userId, "Quần bò", "Mua sắm", "500K", "25/03/2025"), userId);
//            itemRepository.addItem(new Item(userId, "Tiền điện", "Khác", "1200K", "24/03/2025"), userId);
//            itemRepository.addItem(new Item(userId, "Tiền nhà", "Khác", "5000K", "23/03/2025"), userId);
//            preferenceUtils.setFirstLogin(false); // Đánh dấu đã thêm dữ liệu mặc định
//        }

        // Thiết lập ViewPager và Adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), ViewPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new FragmentDashboard(), "Dashboard");
        adapter.addFragment(new FragmentToday(), "Today");
        adapter.addFragment(new FragmentHistory(), "History");
        adapter.addFragment(new FragmentStatistic(), "Statistic");
        adapter.addFragment(new FragmentProfile(), "Profile"); // Thêm FragmentProfile
        viewPager.setAdapter(adapter);

        // Liên kết ViewPager với BottomNavigationView
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.nav_today) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (itemId == R.id.nav_history) {
                viewPager.setCurrentItem(2);
                return true;
            } else if (itemId == R.id.nav_statistic) {
                viewPager.setCurrentItem(3);
                return true;
            } else if (itemId == R.id.nav_profile) {
                viewPager.setCurrentItem(4); // Chuyển đến FragmentProfile
                return true;
            }
            return false;
        });

        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            startActivity(intent);
        });
    }
}
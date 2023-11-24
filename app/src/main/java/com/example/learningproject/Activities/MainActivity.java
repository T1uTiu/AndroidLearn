package com.example.learningproject.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learningproject.Fragments.Reward.RewardFragment;
import com.example.learningproject.Fragments.Statistics.StatisticsFragment;
import com.example.learningproject.Fragments.Task.TaskFragment;
import com.example.learningproject.Manager.RewardManager;
import com.example.learningproject.Manager.ScoreManager;
import com.example.learningproject.Manager.TaskManager;
import com.example.learningproject.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final int activeColor = androidx.appcompat.R.attr.colorPrimary;
    private final int normalColor = Color.parseColor("#666666");
    TabLayout tabLayout;
    DrawerLayout drawerLayout;
    NavigationView drawerMenu;
    ActionBarDrawerToggle toggle;
    TabLayoutMediator mediator;
    ViewPager2 viewPager2;
    ActivityResultLauncher<Intent> taskManagerActivityLauncher;
    private final ViewPager2.OnPageChangeCallback changeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            System.out.println(position);
        }
    };
    final String[] tabLabels = {"任务", "奖励", "统计"};
    final int[] tabIcons = {R.drawable.ic_task_list, R.drawable.ic_reward, R.drawable.ic_statistics};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TaskManager.getInstance().loadFileData(this);
        TaskManager.getInstance().tryRefreshTask();

        RewardManager.getInstance().loadFileData(this);

        ScoreManager.getInstance().loadFileData(this);

        initDrawer();
        initTabLayout();


    }
    void initDrawer(){
        drawerLayout = findViewById(R.id.main_drawer);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        taskManagerActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {});

        drawerMenu = findViewById(R.id.drawer_view);
        drawerMenu.setNavigationItemSelectedListener(item -> {
            switch (item.getOrder()){
                case 0:
                    Intent intent = new Intent(this, TaskManagerActivity.class);
                    taskManagerActivityLauncher.launch(intent);
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
    void initTabLayout(){
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        viewPager2.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position){
                    case 0:
                        return TaskFragment.newInstance();
                    case 1:
                        return RewardFragment.newInstance();
                    default:
                        return StatisticsFragment.newInstance();
                }
            }

            @Override
            public int getItemCount() {
                return tabLabels.length;
            }
        });
        viewPager2.registerOnPageChangeCallback(changeCallback);
        viewPager2.setUserInputEnabled(false);

        mediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            View tabView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_tab_item, tabLayout, false);
            TextView tabLabel = tabView.findViewById(R.id.bottom_tab_label);
            ImageView tabIcon = tabView.findViewById(R.id.bottom_tab_icon);
            tabLabel.setText(tabLabels[position]);
            tabIcon.setImageResource(tabIcons[position]);

            int[][] states = new int[2][];
            states[0] = new int[]{android.R.attr.state_selected};
            states[1] = new int[]{};

            int[] colors = new int[]{activeColor, normalColor};
            ColorStateList colorStateList = new ColorStateList(states, colors);
            tabLabel.setTextColor(colorStateList);
            tabIcon.setImageTintList(colorStateList);

            tab.setCustomView(tabView);
        });
        mediator.attach();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
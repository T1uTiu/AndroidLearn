package com.example.learningproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learningproject.Fragments.MapFragment;
import com.example.learningproject.Fragments.Task.TaskFragment;
import com.example.learningproject.Fragments.WebViewFragment;
import com.example.learningproject.Manager.TaskManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    private final int activeColor = Color.parseColor("#ff678f");
    private final int normalColor = Color.parseColor("#666666");
    TabLayout tabLayout;
    TabLayoutMediator mediator;
    ViewPager2 viewPager2;
    private final ViewPager2.OnPageChangeCallback changeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            System.out.println(position);
        }
    };
    final String[] tabLabels = {"任务", "奖励", "统计", "我的"};
    final int[] tabIcons = {R.drawable.ic_task_list, R.drawable.ic_reward, R.drawable.ic_statistics, R.drawable.ic_me};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TaskManager.getInstance().loadFileData(this);
        TaskManager.getInstance().tryRefreshTask();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        viewPager2.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position){
                    case 0:
                        return TaskFragment.newInstance();
                    case 2:
                        return MapFragment.newInstance();
                    default:
                        return WebViewFragment.newInstance(position);
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

}
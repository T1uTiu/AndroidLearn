package com.example.learningproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.learningproject.Fragments.BookListFragment;
import com.example.learningproject.Fragments.MapFragment;
import com.example.learningproject.Fragments.WebViewFragment;
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
    final String[] tabs = {"图书", "新闻", "地图"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        viewPager2.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position){
                    case 0:
                        return BookListFragment.newInstance();
                    case 2:
                        return MapFragment.newInstance();
                    default:
                        return WebViewFragment.newInstance(position);
                }
            }

            @Override
            public int getItemCount() {
                return tabs.length;
            }
        });
        viewPager2.registerOnPageChangeCallback(changeCallback);

        mediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            //这里可以自定义TabView
            TextView tabView = new TextView(MainActivity.this);

            int[][] states = new int[2][];
            states[0] = new int[]{android.R.attr.state_selected};
            states[1] = new int[]{};

            int[] colors = new int[]{activeColor, normalColor};
            ColorStateList colorStateList = new ColorStateList(states, colors);
            tabView.setText(tabs[position]);
            tabView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tabView.setTextColor(colorStateList);

            tab.setCustomView(tabView);
        });
        mediator.attach();



    }

}
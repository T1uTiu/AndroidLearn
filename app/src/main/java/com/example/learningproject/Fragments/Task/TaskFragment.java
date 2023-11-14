package com.example.learningproject.Fragments.Task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.learningproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {
    ViewPager2 viewPager2;
    TabLayout tabLayout;
    FloatingActionButton fab;
    final String[] tabLabels = {"今日任务", "本周任务", "普通任务"};
    ActivityResultLauncher<Bundle> taskDetailLauncher;
    public TaskFragment() {
        // Required empty public constructor
    }

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);
        viewPager2 = rootView.findViewById(R.id.view_pager);
        tabLayout = rootView.findViewById(R.id.tab_layout);
        fab = rootView.findViewById(R.id.task_add_btn);

        viewPager2.setAdapter(new FragmentStateAdapter(getParentFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return TaskListFragment.newInstance(position);
            }

            @Override
            public int getItemCount() {
                return tabLabels.length;
            }
        });
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(tabLabels[position]));
        mediator.attach();

        taskDetailLauncher = registerForActivityResult(new TaskDetailResultContract(), result -> {
        });
        fab.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("method", 0);
            taskDetailLauncher.launch(bundle);
        });

        return rootView;
    }

}
class TaskDetailResultContract extends ActivityResultContract<Bundle, Integer>{
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Bundle input) {
        Intent intent = new Intent(context, TaskDetail.class);
        intent.putExtra("param", input);
        return intent;
    }

    @Override
    public Integer parseResult(int resultCode, @Nullable Intent intent) {
        return resultCode;
    }
}
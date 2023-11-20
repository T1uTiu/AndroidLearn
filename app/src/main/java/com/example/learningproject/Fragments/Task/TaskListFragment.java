package com.example.learningproject.Fragments.Task;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.learningproject.Interface.TaskChangeObserver;
import com.example.learningproject.Manager.ScoreManager;
import com.example.learningproject.R;
import com.example.learningproject.Model.Task.Task;
import com.example.learningproject.Manager.TaskManager;
import com.example.learningproject.Model.Task.TaskType;
import com.google.android.material.color.utilities.Score;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListFragment extends Fragment implements TaskChangeObserver {
    private TaskType taskType;
    View rootView;
    TaskAdapter adapter;
    public TaskListFragment() {
        // Required empty public constructor
    }


    public static TaskListFragment newInstance(int taskType) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt("taskType", taskType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskType = TaskType.valueOf(getArguments().getInt("taskType"));
            TaskManager.getInstance().attachTaskChangeObserver(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_task_list, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(),DividerItemDecoration.VERTICAL));
        // TaskManager.getInstance().loadTaskList(rootView.getContext(), taskType);

        adapter = new TaskAdapter(TaskManager.getInstance().getTaskList(taskType));
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onTaskChange(TaskType taskType) {
        if(taskType == this.taskType){
            adapter.notifyDataSetChanged();
        }
    }

    class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{

        class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
            private final TextView taskNameText;
            private final TextView taskScoreText;
            private final TextView taskTimesText;
            CheckBox taskCheckBox;

            public TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                this.taskNameText = itemView.findViewById(R.id.task_name_text);
                this.taskScoreText = itemView.findViewById(R.id.task_score_text);
                this.taskTimesText = itemView.findViewById(R.id.task_progress_text);
                this.taskCheckBox = itemView.findViewById(R.id.task_check);
                this.taskCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
                    int idx = getAdapterPosition();
                    Task task = tasks.get(idx);
                    if(b){
                        if(TaskManager.getInstance().finishTask(task)){
                            notifyItemRemoved(idx);
                            ScoreManager.getInstance().addScoreLog(task.getScore(), task.getName());
                        }else{
                            notifyItemChanged(idx);
                        }

                        this.taskCheckBox.setChecked(false);
                    }
                });
                itemView.setOnCreateContextMenuListener(this);
            }

            public TextView getTaskNameText() {
                return taskNameText;
            }

            public TextView getTaskScoreText() {
                return taskScoreText;
            }

            public TextView getTaskTimesText() {
                return taskTimesText;
            }

            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                int idx = getAdapterPosition();
                Task task = tasks.get(idx);
                AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
                builder.setMessage("确定删除任务吗？")
                        .setPositiveButton("确定", (dialogInterface, i) -> TaskManager.getInstance().deleteTask(task))
                        .setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss());
                builder.show();
                return true;
            }

            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                MenuItem delete = contextMenu.add(Menu.NONE, 1,1, "删除");
                delete.setOnMenuItemClickListener(this);
            }
        }
        private final List<Task> tasks;
        public TaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }
        @NonNull
        @Override
        public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_task,parent,false);
            return new TaskViewHolder(view);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
            Task task = tasks.get(position);
            holder.getTaskNameText().setText(task.getName());
            holder.getTaskScoreText().setText(String.format("+%d", task.getScore()));
            holder.getTaskTimesText().setText(String.format("%d/%d", task.getCurrentTimes(), task.getTimes()));

        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }
    }
}

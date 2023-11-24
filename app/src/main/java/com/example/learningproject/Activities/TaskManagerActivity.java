package com.example.learningproject.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.learningproject.Manager.TaskManager;
import com.example.learningproject.Model.Task.Task;
import com.example.learningproject.Model.Task.TaskType;
import com.example.learningproject.Navigation.TaskDetailResultContract;
import com.example.learningproject.R;

import java.util.List;
import java.util.Objects;

public class TaskManagerActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TaskManagerItemAdapter adapter;
    Spinner spinner;
    ActivityResultLauncher<Bundle> taskDetailLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("任务管理");

        spinner = findViewById(R.id.task_filter_spinner);
        taskDetailLauncher = registerForActivityResult(new TaskDetailResultContract(), result -> {
            if(result == Activity.RESULT_OK){
                adapter.notifyDataSetChanged();
            }
        });
        initRecyclerView();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<Task> taskList = TaskManager.getInstance().getRepeatTaskList(TaskType.valueOf(spinner.getSelectedItemPosition()));
                adapter.setTaskList(taskList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    void initRecyclerView(){
        recyclerView = findViewById(R.id.task_manager_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Task> taskList = TaskManager.getInstance().getRepeatTaskList(TaskType.valueOf(spinner.getSelectedItemPosition()));
        adapter = new TaskManagerItemAdapter(taskList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    class TaskManagerItemAdapter extends RecyclerView.Adapter<TaskManagerItemAdapter.TaskManagerItemViewHolder>{
        class TaskManagerItemViewHolder extends RecyclerView.ViewHolder{
            TextView taskNameText, taskTimesText, taskScoreText;
            Button taskEditButton, taskDeleteButton;
            public TaskManagerItemViewHolder(@NonNull View itemView) {
                super(itemView);
                taskNameText = itemView.findViewById(R.id.task_manager_item_name_text);
                taskTimesText = itemView.findViewById(R.id.task_manager_item_times_text);
                taskScoreText = itemView.findViewById(R.id.task_manager_item_score_text);
                taskEditButton = itemView.findViewById(R.id.task_manager_item_edit_btn);
                taskDeleteButton = itemView.findViewById(R.id.task_manager_item_delete_btn);

                taskDeleteButton.setOnClickListener(view -> {
                    int idx = getAdapterPosition();
                    Task task = taskList.get(idx);
                    AlertDialog.Builder builder = new AlertDialog.Builder(TaskManagerActivity.this);
                    builder.setMessage("确定要删除任务吗？")
                            .setPositiveButton("确定", (dialogInterface, i) -> {
                                TaskManager.getInstance().deleteRepeatTask(task.getType(), idx);
                                notifyItemRemoved(idx);
                            })
                            .setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss());
                    builder.create().show();
                });
                taskEditButton.setOnClickListener(view -> {
                    int idx = getAdapterPosition();
                    Task task = taskList.get(idx);
                    Bundle bundle = new Bundle();
                    bundle.putInt("method", 1);
                    bundle.putInt("idx", idx);
                    bundle.putInt("taskType", task.getType().value());
                    bundle.putInt("editType", 1);
                    taskDetailLauncher.launch(bundle);
                });
            }
        }
        List<Task> taskList;

        public void setTaskList(List<Task> taskList) {
            this.taskList = taskList;
        }

        public TaskManagerItemAdapter(List<Task> taskList){
            this.taskList = taskList;
        }
        @NonNull
        @Override
        public TaskManagerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.listitem_task_manager, parent, false);
            return new TaskManagerItemViewHolder(view);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull TaskManagerItemViewHolder holder, int position) {
            Task task = taskList.get(position);
            holder.taskNameText.setText(task.getName());
            holder.taskScoreText.setText(String.valueOf(task.getScore()));
            holder.taskTimesText.setText(String.format("?/%d", task.getTimes()));
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }



    }
}
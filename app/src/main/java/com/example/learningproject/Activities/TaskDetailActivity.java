package com.example.learningproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.learningproject.R;
import com.example.learningproject.Model.Task.Task;
import com.example.learningproject.Manager.TaskManager;
import com.example.learningproject.Model.Task.TaskType;

public class TaskDetailActivity extends AppCompatActivity {
    Spinner taskTypeSpinner;
    Button okBtn;
    EditText taskNameEdit;
    EditText taskScoreEdit;
    EditText taskTimesEdit;
    String[] taskTypes = {TaskType.EVERYDAY.name(), TaskType.EVERYWEEK.name(), TaskType.NORMAL.name()};
    String[] titles = {"添加任务", "编辑任务"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Intent intent = getIntent();
        Bundle param = intent.getBundleExtra("param");
        int method = param.getInt("method");

        getSupportActionBar().setTitle(titles[method]);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        taskTypeSpinner = findViewById(R.id.detail_task_type_spinner);
        taskNameEdit = findViewById(R.id.detail_task_name_edit);
        taskScoreEdit = findViewById(R.id.detail_task_score_edit);
        taskTimesEdit = findViewById(R.id.detail_task_times_edit);
        okBtn = findViewById(R.id.detail_task_ok_btn);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, taskTypes);
        taskTypeSpinner.setAdapter(adapter);

        if(method == 1){
            int idx = param.getInt("idx");
            TaskType taskType = TaskType.valueOf(param.getInt("taskType"));
            assert taskType != null;
            Task task = TaskManager.getInstance().getTaskList(taskType).get(idx);
            taskNameEdit.setText(task.getName());
            taskScoreEdit.setText(String.valueOf(task.getScore()));
            taskTimesEdit.setText(String.valueOf(task.getTimes()));
            taskTypeSpinner.setSelection(task.getType().value());
        }

        okBtn.setOnClickListener(view -> {
            String taskRawName = taskNameEdit.getText().toString();
            String taskRawScore = taskScoreEdit.getText().toString();
            String taskRawTimes = taskTimesEdit.getText().toString();
            if(taskRawName.isEmpty() || taskRawScore.isEmpty()){
                Toast.makeText(this, "任务名称和分数不能为空", Toast.LENGTH_SHORT).show();
            }else{
                int taskScore = Integer.parseInt(taskRawScore);
                int taskTimes = taskRawTimes.isEmpty() ? 1 : Integer.parseInt(taskRawTimes);
                int taskRawType = (int)taskTypeSpinner.getSelectedItemId();
                TaskType taskType = TaskType.valueOf(taskRawType);

                if(method == 0){
                    Task newTask = new Task(taskRawName, taskScore, taskTimes, taskType);
                    TaskManager.getInstance().addTask(newTask);
                }else{
                    int idx = param.getInt("idx");
                    TaskType originTaskType = TaskType.valueOf(param.getInt("taskType"));
                    assert originTaskType != null;
                    TaskManager.getInstance().editTask(originTaskType, idx, taskRawName, taskScore, taskTimes);
                    Task task = TaskManager.getInstance().getTaskList(originTaskType).get(idx);
                    if(taskType != originTaskType){
                        task.setType(taskType);
                        TaskManager.getInstance().deleteTask(originTaskType, idx);
                        TaskManager.getInstance().addTask(task);
                    }
                }
                setResult(RESULT_OK);
                finish();
            }



        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.example.learningproject.Fragments.Task;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.learningproject.R;
import com.example.learningproject.data.Task.TaskType;

public class TaskDetail extends AppCompatActivity {
    Spinner taskTypeSpinner;
    TaskType[] taskTypes = {TaskType.EVERYDAY, TaskType.EVERYWEEK, TaskType.NORMAL};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        taskTypeSpinner = findViewById(R.id.detail_task_type_spinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, taskTypes);
        taskTypeSpinner.setAdapter(adapter);
        taskTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.print(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
}
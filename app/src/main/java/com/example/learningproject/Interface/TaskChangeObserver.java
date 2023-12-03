package com.example.learningproject.Interface;

import com.example.learningproject.Model.Task.TaskType;

public interface TaskChangeObserver {
    int ADD = 0;
    int EDIT = 1;
    int DELETE = 2;
    void onTaskChange(TaskType taskType, int method, int idx);
}

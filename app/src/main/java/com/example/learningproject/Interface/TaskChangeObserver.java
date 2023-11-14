package com.example.learningproject.Interface;

import com.example.learningproject.Model.Task.TaskType;

public interface TaskChangeObserver {
    void onTaskChange(TaskType taskType);
}

package com.example.learningproject.Service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.learningproject.Manager.TaskManager;

public class WeekTaskRefreshService extends IntentService {

    public WeekTaskRefreshService() {
        super("RefreshEveryWeekTaskService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        TaskManager.getInstance().refreshWeekTask();
    }
}

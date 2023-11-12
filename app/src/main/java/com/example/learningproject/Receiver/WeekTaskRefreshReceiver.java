package com.example.learningproject.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.learningproject.Service.DayTaskRefreshService;

public class WeekTaskRefreshReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Intent serviceIntent = new Intent(context, DayTaskRefreshService.class);
        context.startService(serviceIntent);
    }
}

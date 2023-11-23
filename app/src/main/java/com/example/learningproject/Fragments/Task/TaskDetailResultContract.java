package com.example.learningproject.Fragments.Task;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TaskDetailResultContract extends ActivityResultContract<Bundle, Integer> {
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

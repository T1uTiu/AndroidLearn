package com.example.learningproject.Model.Reward;

import com.example.learningproject.Model.Task.TaskType;

public enum RewardType {
    ONESHOT(0), INFINITY(1);

    private final int value;
    private RewardType(int value){
        this.value = value;
    }
    public int value(){
        return this.value;
    }
    public static RewardType valueOf(int value){
        switch (value){
            case 0:
                return ONESHOT;
            case 1:
                return INFINITY;
            default:
                return null;
        }
    }
}

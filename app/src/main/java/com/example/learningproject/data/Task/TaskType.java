package com.example.learningproject.data.Task;

public enum TaskType {
    EVERYDAY(0),
    EVERYWEEK(1),
    NORMAL(2);

    private int value;
    private TaskType(int value){
        this.value = value;
    }
    public int value(){
        return this.value;
    }
    public static TaskType valueOf(int value){
        switch (value){
            case 0:
                return EVERYDAY;
            case 1:
                return EVERYWEEK;
            case 2:
                return NORMAL;
            default:
                return null;
        }
    }
}

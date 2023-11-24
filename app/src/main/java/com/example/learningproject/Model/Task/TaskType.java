package com.example.learningproject.Model.Task;

public enum TaskType {
    EVERYDAY(0),
    EVERYWEEK(1),
    NORMAL(2);

    private final int value;
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
            default:
                return NORMAL;
        }
    }


}

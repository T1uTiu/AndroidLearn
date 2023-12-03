package com.example.learningproject.Model.Task;

import androidx.annotation.NonNull;

public class Task implements java.io.Serializable, Cloneable {
    public static final int FINISHED = 1;
    public static final int UNFINISHED = 0;
    long id;
    String name;
    int score;
    int totalTimes,  currentTimes;
    TaskType type;
    boolean accumulative;

    public Task(String name, int score, int times, TaskType type) {
        this.name = name;
        this.score = score;
        this.totalTimes = times;
        this.currentTimes = 0;
        this.type = type;
    }
    public Task(String name, int score, int times, TaskType type, boolean accumulative) {
        this(name, score, times, type);
        this.accumulative = accumulative;
    }

    public long getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public int getTotalTimes() {
        return totalTimes;
    }

    public int getCurrentTimes() {
        return currentTimes;
    }

    public String getName() {
        return name;
    }
    public TaskType getType() {
        return type;
    }
    public boolean isAccumulative() {
        return accumulative;
    }
    public int getTaskState(){
        if (currentTimes >= totalTimes){
            return FINISHED;
        }
        return UNFINISHED;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTotalTimes(int totalTimes) {
        this.totalTimes = totalTimes;
    }

    public void setCurrentTimes(int currentTimes) {
        this.currentTimes = currentTimes;
    }

    public void setType(TaskType type) {
        this.type = type;
    }
    public void setAccumulative(boolean accumulative) {
        this.accumulative = accumulative;
    }

    @NonNull
    @Override
    public Task clone() {
        try {
            return (Task) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

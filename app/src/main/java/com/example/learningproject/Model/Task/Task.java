package com.example.learningproject.Model.Task;

import androidx.annotation.NonNull;

public class Task implements java.io.Serializable, Cloneable {
    int id;
    String name;
    int score;
    int times;
    int currentTimes;
    TaskType type;

    public Task(String name, int score, int times, TaskType type) {
        this.name = name;
        this.score = score;
        this.times = times;
        this.type = type;
        this.currentTimes = 0;
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public int getTimes() {
        return times;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public void setCurrentTimes(int currentTimes) {
        this.currentTimes = currentTimes;
    }

    public void setType(TaskType type) {
        this.type = type;
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

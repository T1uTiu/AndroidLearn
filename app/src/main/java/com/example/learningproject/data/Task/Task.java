package com.example.learningproject.data.Task;

public class Task implements java.io.Serializable{
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

    public void setName(String name) {
        this.name = name;
    }

    public void setPoint(int score) {
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
}

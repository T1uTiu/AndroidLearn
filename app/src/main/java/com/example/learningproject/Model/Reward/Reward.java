package com.example.learningproject.Model.Reward;

import androidx.annotation.NonNull;

public class Reward implements java.io.Serializable, Cloneable{
    long id;
    String name;
    int score;
    int currentTimes;
    RewardType type;
    public Reward(String name, int score, RewardType type) {
        this.name = name;
        this.score = score;
        this.type = type;
        this.currentTimes = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public long getId() {
        return id;
    }

    public int getCurrentTimes() {
        return currentTimes;
    }

    public RewardType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCurrentTimes(int currentTimes) {
        this.currentTimes = currentTimes;
    }

    public void setType(RewardType type) {
        this.type = type;
    }

    @NonNull
    @Override
    public Reward clone() {
        try {
            return (Reward) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

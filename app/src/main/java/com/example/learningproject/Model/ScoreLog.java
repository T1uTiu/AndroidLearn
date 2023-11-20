package com.example.learningproject.Model;

import java.io.Serializable;

public class ScoreLog implements Serializable {
    long time;
    int score;
    String name;
    public ScoreLog(long time, int score, String name){
        this.score = score; this.time = time; this.name = name;
    }

    public int getScore() {
        return score;
    }

    public long getTime() {
        return time;
    }

    public String getName() {
        return name;
    }
}

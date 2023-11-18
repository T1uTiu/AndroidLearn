package com.example.learningproject.Model;

public class ScoreLog {
    long time;
    int score;
    public ScoreLog(long time, int score){
        this.score = score; this.time = time;
    }

    public int getScore() {
        return score;
    }

    public long getTime() {
        return time;
    }
}

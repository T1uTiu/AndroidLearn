package com.example.learningproject.Manager;

import android.annotation.SuppressLint;
import android.content.Context;


import com.example.learningproject.Model.ScoreLog;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScoreManager {
    @SuppressLint("StaticFieldLeak")
    static ScoreManager instance;
    public static ScoreManager getInstance(){
        if(instance == null){
            instance = new ScoreManager();
        }
        return instance;
    }
    final String filename = "score_log";
    HashMap<Long, List<ScoreLog>> scoreLogMap = new HashMap<>();
    Context context;

    @SuppressWarnings("unchecked")
    public void loadFileData(Context context){
        this.context = context;
        try(FileInputStream fis = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis)){
            this.scoreLogMap = (HashMap<Long, List<ScoreLog>>) ois.readObject();
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    public void saveFileData() {
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(scoreLogMap);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public List<ScoreLog> getScoreLogList(){
        List<ScoreLog> scoreLogList = new ArrayList<>();
        for(List<ScoreLog> list : scoreLogMap.values()){
            scoreLogList.addAll(list);
        }
        return scoreLogList;
    }
}

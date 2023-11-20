package com.example.learningproject.Manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;


import com.example.learningproject.Interface.ScoreLogObserver;
import com.example.learningproject.Model.ScoreLog;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    final String[] filename = {"score_log_group", "score_log_group_key"};
    HashMap<Integer, List<ScoreLog>> scoreLogGroup = new HashMap<>(); // 以每日零点为键值，记录每一天的积分记录
    List<Integer> scoreLogGroupKeyList = new ArrayList<>(); // 记录所有的键值，方便按下标遍历
    Context context;
    List<ScoreLogObserver> scoreLogObservers;
    SimpleDateFormat dateFormat;
    @SuppressLint("SimpleDateFormat")
    ScoreManager(){
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        scoreLogObservers = new ArrayList<>();
    }
    public void attachScoreLogObserver(ScoreLogObserver observer){
        scoreLogObservers.add(observer);
    }

    @SuppressWarnings("unchecked")
    public void loadFileData(Context context){
        this.context = context;
        for(int i = 0; i < filename.length; i++){
            try(FileInputStream fis = context.openFileInput(filename[i]);
                ObjectInputStream ois = new ObjectInputStream(fis)){
                if(i == 0){
                    this.scoreLogGroup = (HashMap<Integer, List<ScoreLog>>) ois.readObject();
                } else if (i == 1) {
                    this.scoreLogGroupKeyList = (List<Integer>) ois.readObject();
                }

            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }
    public void saveFileData(){
        for(int i =0; i < filename.length; i++){
            try(FileOutputStream fos = context.openFileOutput(filename[i], Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                if(i == 0){
                    oos.writeObject(scoreLogGroup);
                }else if(i==1){
                    oos.writeObject(scoreLogGroupKeyList);
                }
                Log.d("ScoreManager", "保存成功");
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    public List<Integer> getScoreLogGroupKeyList() {
        return scoreLogGroupKeyList;
    }
    public HashMap<Integer, List<ScoreLog>> getScoreLogGroup(){
        return scoreLogGroup;
    }
    public void addScoreLog(int score, String name){
        String dateString = dateFormat.format(System.currentTimeMillis());
        int date = Integer.parseInt(dateString);
        ScoreLog scoreLog = new ScoreLog(System.currentTimeMillis(), score, name);
        int method = 0; // 0表示新增一天的记录，1表示今天的记录增加一条
        // 更新scoreLogMap
        if(scoreLogGroup.containsKey(date)){
            scoreLogGroup.get(date).add(scoreLog);
            method = 1;
        }else{
            List<ScoreLog> scoreLogList = new ArrayList<>();
            scoreLogList.add(scoreLog);
            scoreLogGroup.put(date, scoreLogList);
            scoreLogGroupKeyList.add(date);
        }
        for(ScoreLogObserver observer : scoreLogObservers){
            observer.onScoreLogChange(method, score);
        }
        saveFileData();
    }
}

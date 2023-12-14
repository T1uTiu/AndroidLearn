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
import java.util.Locale;

public class ScoreManager {
    @SuppressLint("StaticFieldLeak")
    static ScoreManager instance;
    public static ScoreManager getInstance(){
        if(instance == null){
            instance = new ScoreManager();
        }
        return instance;
    }
    final String[] filename = {"score_log_group", "score_log_group_key",
            "current_score", "current_income", "current_outcome",
            "daily_income", "daily_outcome"};
    HashMap<Integer, List<ScoreLog>> scoreLogGroup = new HashMap<>(); // 以每日零点为键值，记录每一天的积分记录
    HashMap<Integer, Integer> dailyIncome = new HashMap<>(); // 以每日零点为键值，记录每一天的收入
    HashMap<Integer, Integer> dailyOutcome = new HashMap<>(); // 以每日零点为键值，记录每一天的支出
    List<Integer> scoreLogGroupKeyList = new ArrayList<>(); // 记录所有的键值，方便按下标遍历
    Integer currentScore = 0, currentIncome = 0, currentOutcome = 0;
    Context context;
    List<ScoreLogObserver> scoreLogObservers;
    SimpleDateFormat dateFormat;

    ScoreManager(){
        dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        scoreLogObservers = new ArrayList<>();
    }
    public void attachScoreLogObserver(ScoreLogObserver observer){
        scoreLogObservers.add(observer);
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    @SuppressWarnings("unchecked")
    public void loadFileData(Context context){
        this.context = context;
        for(int i = 0; i < filename.length; i++){
            try(FileInputStream fis = context.openFileInput(filename[i]);
                ObjectInputStream ois = new ObjectInputStream(fis)){
                switch (i) {
                    case 0:
                        this.scoreLogGroup = (HashMap<Integer, List<ScoreLog>>) ois.readObject();
                        break;
                    case 1:
                        this.scoreLogGroupKeyList = (List<Integer>) ois.readObject();
                        break;
                    case 2:
                        this.currentScore = (Integer) ois.readObject();
                        break;
                    case 3:
                        this.currentIncome = (Integer) ois.readObject();
                        break;
                    case 4:
                        this.currentOutcome = (Integer) ois.readObject();
                        break;
                    case 5:
                        this.dailyIncome = (HashMap<Integer, Integer>) ois.readObject();
                        break;
                    case 6:
                        this.dailyOutcome = (HashMap<Integer, Integer>) ois.readObject();
                        break;
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
                switch (i) {
                    case 0:
                        oos.writeObject(scoreLogGroup);
                        break;
                    case 1:
                        oos.writeObject(scoreLogGroupKeyList);
                        break;
                    case 2:
                        oos.writeObject(currentScore);
                        break;
                    case 3:
                        oos.writeObject(currentIncome);
                        break;
                    case 4:
                        oos.writeObject(currentOutcome);
                        break;
                    case 5:
                        oos.writeObject(dailyIncome);
                        break;
                    case 6:
                        oos.writeObject(dailyOutcome);
                        break;
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
    public Integer getCurrentScore(){
        return currentScore;
    }

    public Integer getCurrentIncome() {
        return currentIncome;
    }

    public Integer getCurrentOutcome() {
        return currentOutcome;
    }

    public HashMap<Integer, Integer> getDailyIncome() {
        return dailyIncome;
    }

    public HashMap<Integer, Integer> getDailyOutcome() {
        return dailyOutcome;
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
        // 更新current, dailyIncome, dailyOutcome
        currentScore += score;
        if(score > 0){
            currentIncome += score;
            if(dailyIncome.containsValue(date)){
                dailyIncome.put(date, dailyIncome.get(date)+score);
            }else{
                dailyIncome.put(date, score);
            }
        }
        else{
            currentOutcome -= score;
            if(dailyOutcome.containsValue(date)){
                dailyOutcome.put(date, dailyOutcome.get(date)-score);
            }else{
                dailyOutcome.put(date, -score);
            }
        }
        for(ScoreLogObserver observer : scoreLogObservers){
            observer.onScoreLogChange(method, score);
        }
        saveFileData();
    }
}

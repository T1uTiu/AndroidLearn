package com.example.learningproject.Manager;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.learningproject.Interface.TaskChangeObserver;
import com.example.learningproject.Model.Task.Task;
import com.example.learningproject.Model.Task.TaskType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    @SuppressLint("StaticFieldLeak")
    private static TaskManager instance;
    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    List<Task> curDayTaskList = new ArrayList<>();
    List<Task> dayTaskList = new ArrayList<>();
    List<Task> curWeekTaskList = new ArrayList<>();
    List<Task> weekTaskList = new ArrayList<>();
    List<Task> onetimeTaskList = new ArrayList<>();
    HashMap<String, Long> info;
    Context context;
    List<TaskChangeObserver> taskChangeObservers = new ArrayList<>();


    final String[] fileName = {"cur_day_task_list", "cur_week_task_list", "onetime_task_list",
            "day_task_list", "week_task_list","onetime_task_list"};
    private TaskManager() {

    }
    public void attachTaskChangeObserver(TaskChangeObserver observer){
        taskChangeObservers.add(observer);
    }
    public List<Task> getTaskList(TaskType type){
        switch (type){
            case EVERYDAY:
                return curDayTaskList;
            case EVERYWEEK:
                return curWeekTaskList;
            case NORMAL:
                return onetimeTaskList;
            default:
                return null;
        }
    }
    public List<Task> getRepeatTaskList(TaskType type){
        switch (type){
            case EVERYDAY:
                return dayTaskList;
            case EVERYWEEK:
                return weekTaskList;
            default:
                return null;
        }
    }
    @SuppressWarnings("unchecked")
    public void loadFileData(Context context){
        this.context = context;
        for(int i = 0; i < 3; i++){
            try(FileInputStream fis = context.openFileInput(fileName[i]);
                FileInputStream fis2 = context.openFileInput(fileName[i + 3])){
                ObjectInputStream ois = new ObjectInputStream(fis);
                ObjectInputStream ois2 = new ObjectInputStream(fis2);
                switch (i){
                    case 0:
                        curDayTaskList = (ArrayList<Task>) ois.readObject();
                        dayTaskList = (ArrayList<Task>) ois2.readObject();
                        break;
                    case 1:
                        curWeekTaskList = (ArrayList<Task>) ois.readObject();
                        weekTaskList = (ArrayList<Task>) ois2.readObject();
                        break;
                    case 2:
                        onetimeTaskList = (ArrayList<Task>) ois.readObject();
                        break;
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        try(FileInputStream fis = context.openFileInput("info")){
            ObjectInputStream ois = new ObjectInputStream(fis);
            info = (HashMap<String, Long>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            info = new HashMap<>();
            info.put("task_id", 0L);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR, 0); calendar.set(Calendar.MINUTE, 0); calendar.set(Calendar.SECOND, 0);
            info.put("last_refresh_day", calendar.getTimeInMillis());
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            info.put("last_refresh_week", calendar.getTimeInMillis());
        }

    }
    public void saveFileData(TaskType type){
        try {
            FileOutputStream fos = context.openFileOutput(fileName[type.value()], Context.MODE_PRIVATE);
            FileOutputStream fos2 = context.openFileOutput(fileName[type.value() + 3], Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
            switch (type){
                case EVERYDAY:
                    oos.writeObject(curDayTaskList);
                    oos2.writeObject(dayTaskList);
                    break;
                case EVERYWEEK:
                    oos.writeObject(curWeekTaskList);
                    oos2.writeObject(weekTaskList);
                    break;
                case NORMAL:
                    oos.writeObject(onetimeTaskList);
                    break;
            }
            oos.close();
            oos2.close();
            fos.close();
            fos2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(FileOutputStream fos = context.openFileOutput("info", Context.MODE_PRIVATE)){
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(info);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addTask(Task task){
        task.setId(info.get("task_id"));
        info.put("task_id", info.get("task_id") + 1);
        TaskType type = task.getType();
        switch (type){
            case EVERYDAY:
                dayTaskList.add(task);
                curDayTaskList.add(task.clone());
                break;
            case EVERYWEEK:
                weekTaskList.add(task);
                curWeekTaskList.add(task.clone());
                break;
            case NORMAL:
                onetimeTaskList.add(task);
                break;
        }
        saveFileData(type);
        for(TaskChangeObserver observer : taskChangeObservers){
            observer.onTaskChange(type);
        }
    }
    public void deleteTask(TaskType taskType, int idx){
        long taskID;
        switch (taskType){
            case EVERYDAY:
                taskID = curDayTaskList.get(idx).getId();
                curDayTaskList.remove(idx);
                for(Task t : dayTaskList){
                    if(t.getId() == taskID){
                        dayTaskList.remove(t);
                        break;
                    }
                }
                break;
            case EVERYWEEK:
                taskID = curDayTaskList.get(idx).getId();
                curWeekTaskList.remove(idx);
                for(Task t : weekTaskList){
                    if(t.getId() == taskID){
                        weekTaskList.remove(t);
                        break;
                    }
                }
                break;
            case NORMAL:
                onetimeTaskList.remove(idx);
                break;
        }
        saveFileData(taskType);
        for(TaskChangeObserver observer : taskChangeObservers){
            observer.onTaskChange(taskType);
        }
    }
    public void editTask(TaskType taskType, int idx, String newName, int newScore, int newTimes){
        Task task;
        switch (taskType){
            case EVERYDAY:
                task = curDayTaskList.get(idx);
                break;
            case EVERYWEEK:
                task = curWeekTaskList.get(idx);
                break;
            default:
                task = onetimeTaskList.get(idx);
                break;
        }
        task.setName(newName);
        task.setScore(newScore);
        task.setTimes(newTimes);
        if(task.getCurrentTimes() >= newTimes){
            task.setCurrentTimes(newTimes-1);
        }
        saveFileData(taskType);
        for(TaskChangeObserver observer : taskChangeObservers){
            observer.onTaskChange(taskType);
        }
    }
    public boolean finishTask(Task task){
        TaskType type = task.getType();
        task.setCurrentTimes(task.getCurrentTimes() + 1);
        if(task.getCurrentTimes() == task.getTimes()){
            switch (type){
                case EVERYDAY:
                    curDayTaskList.remove(task);
                    break;
                case EVERYWEEK:
                    curWeekTaskList.remove(task);
                    break;
                case NORMAL:
                    onetimeTaskList.remove(task);
                    break;
            }
            saveFileData(type);
            return true;
        }
        saveFileData(type);
        return false;
    }
    public void tryRefreshTask(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Calendar lastRefreshDay = Calendar.getInstance();
        lastRefreshDay.setTimeInMillis(info.get("last_refresh_day"));
        Calendar lastRefreshWeek = Calendar.getInstance();
        lastRefreshWeek.setTimeInMillis(info.get("last_refresh_week"));
        if(calendar.get(Calendar.DAY_OF_YEAR) != lastRefreshDay.get(Calendar.DAY_OF_YEAR)){
            refreshDayTask();
        }
        if(calendar.get(Calendar.WEEK_OF_YEAR) != lastRefreshWeek.get(Calendar.WEEK_OF_YEAR)){
            refreshWeekTask();
        }
    }
    public void refreshDayTask(){
        for(Task task : dayTaskList) {
            curDayTaskList.add(task.clone());
        }
        info.put("last_refresh_day", System.currentTimeMillis());
        saveFileData(TaskType.EVERYDAY);
        for(TaskChangeObserver observer : taskChangeObservers){
            observer.onTaskChange(TaskType.EVERYDAY);
        }
    }
    public void refreshWeekTask(){
        for(Task task : weekTaskList) {
            curWeekTaskList.add(task.clone());
        }
        info.put("last_refresh_week", System.currentTimeMillis());
        saveFileData(TaskType.EVERYWEEK);
        for(TaskChangeObserver observer : taskChangeObservers){
            observer.onTaskChange(TaskType.EVERYWEEK);
        }
    }
}

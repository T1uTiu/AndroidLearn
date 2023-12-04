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

@SuppressWarnings("unused")
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

    public void attachTaskChangeObserver(TaskChangeObserver observer){
        taskChangeObservers.add(observer);
    }
    public List<Task> getTaskList(TaskType type){
        switch (type){
            case EVERYDAY:
                return curDayTaskList;
            case EVERYWEEK:
                return curWeekTaskList;
            default:
                return onetimeTaskList;
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
                FileInputStream fis2 = context.openFileInput(fileName[i + 3]);
                ObjectInputStream ois = new ObjectInputStream(fis);
                ObjectInputStream ois2 = new ObjectInputStream(fis2)){
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
            info.put("last_refresh_day", System.currentTimeMillis());
            info.put("last_refresh_week", System.currentTimeMillis());
        }

    }
    public void saveFileData(TaskType type){
        try (FileOutputStream fos = context.openFileOutput(fileName[type.value()], Context.MODE_PRIVATE);
             FileOutputStream fos2 = context.openFileOutput(fileName[type.value() + 3], Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos);
             ObjectOutputStream oos2 = new ObjectOutputStream(fos2)){

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(FileOutputStream fos = context.openFileOutput("info", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("ConstantConditions")
    public void addTask(Task task, boolean setID){
        if(setID){
            task.setId(info.get("task_id"));
            info.put("task_id", info.get("task_id") + 1);
        }
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
            observer.onTaskChange(type, TaskChangeObserver.ADD, -1);
        }
    }
    @SuppressWarnings("ConstantConditions")
    public void addRepeatTask(Task task, boolean setID){
        if(setID){
            task.setId(info.get("task_id"));
            info.put("task_id", info.get("task_id") + 1);
        }
        TaskType type = task.getType();
        switch (type){
            case EVERYDAY:
                dayTaskList.add(task);
                break;
            case EVERYWEEK:
                weekTaskList.add(task);
                break;
        }
        saveFileData(type);
    }
    public void deleteTask(TaskType taskType, int idx){
        switch (taskType){
            case EVERYDAY:
                curDayTaskList.remove(idx);
                break;
            case EVERYWEEK:
                curWeekTaskList.remove(idx);
                break;
            case NORMAL:
                onetimeTaskList.remove(idx);
                break;
        }
        saveFileData(taskType);
        for(TaskChangeObserver observer : taskChangeObservers){
            observer.onTaskChange(taskType, TaskChangeObserver.DELETE, idx);
        }
    }
    public void deleteFromCurTask(TaskType taskType, int idx){
        Task task;
        switch (taskType){
            case EVERYDAY:
                task = curDayTaskList.get(idx);
                for(int i = 0; i < dayTaskList.size(); i++){
                    if(dayTaskList.get(i).getId() == task.getId()){
                        dayTaskList.remove(i);
                        break;
                    }
                }
                curDayTaskList.remove(idx);
                break;
            case EVERYWEEK:
                task = curWeekTaskList.get(idx);
                for(int i = 0; i < weekTaskList.size(); i++){
                    if(weekTaskList.get(i).getId() == task.getId()){
                        weekTaskList.remove(i);
                        break;
                    }
                }
                curWeekTaskList.remove(idx);
                break;
            default:
                onetimeTaskList.remove(idx);
        }
        for(TaskChangeObserver observer : taskChangeObservers){
            observer.onTaskChange(taskType, TaskChangeObserver.DELETE, idx);
        }
        saveFileData(taskType);
    }
    public void deleteFromRepeatTask(TaskType taskType, int idx){
        Task task;
        int targetIdx = -1;
        switch (taskType){
            case EVERYDAY:
                task = dayTaskList.get(idx);
                for(int i = 0; i < curDayTaskList.size(); i++){
                    if(curDayTaskList.get(i).getId() == task.getId()){
                        targetIdx = i;
                        break;
                    }
                }
                if(targetIdx != -1) curDayTaskList.remove(targetIdx);
                dayTaskList.remove(idx);
                break;
            case EVERYWEEK:
                task = weekTaskList.get(idx);
                for(int i = 0; i < curWeekTaskList.size(); i++){
                    if(curWeekTaskList.get(i).getId() == task.getId()){
                        targetIdx = i;
                        break;
                    }
                }
                if(targetIdx != -1) curDayTaskList.remove(targetIdx);
                weekTaskList.remove(idx);
                break;
            default:
                return;
        }
        if(targetIdx != -1){
            for(TaskChangeObserver observer : taskChangeObservers){
                observer.onTaskChange(taskType, TaskChangeObserver.DELETE, targetIdx);
            }
        }
        saveFileData(taskType);
    }
    public void deleteRepeatTask(TaskType taskType, int idx){
        switch (taskType){
            case EVERYDAY:
                dayTaskList.remove(idx);
                break;
            case EVERYWEEK:
                weekTaskList.remove(idx);
                break;
        }
        saveFileData(taskType);
    }
    public void editTask(TaskType taskType, int idx, String newName, int newScore, int newTimes, boolean accumulative){
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
        task.setTotalTimes(newTimes);
        if(task.getCurrentTimes() >= newTimes){
            task.setCurrentTimes(newTimes-1);
        }
        task.setAccumulative(accumulative);
        saveFileData(taskType);
        for(TaskChangeObserver observer : taskChangeObservers){
            observer.onTaskChange(taskType, TaskChangeObserver.EDIT, idx);
        }
    }

    public void editRepeatTask(TaskType taskType, int idx, String newName, int newScore, int newTimes){
        Task task;
        switch (taskType){
            case EVERYDAY:
                task = dayTaskList.get(idx);
                break;
            case EVERYWEEK:
                task = weekTaskList.get(idx);
                break;
            default:
                return;
        }
        task.setName(newName);
        task.setScore(newScore);
        task.setTotalTimes(newTimes);
        saveFileData(taskType);
    }
    public boolean finishTask(Task task){
        TaskType type = task.getType();
        task.setCurrentTimes(Math.min(task.getCurrentTimes() + 1, task.getTotalTimes()));
        boolean res = task.getCurrentTimes() >= task.getTotalTimes();
        saveFileData(type);
        return res;
    }
    @SuppressWarnings("ConstantConditions")
    public void tryRefreshTask(){
        Calendar calendar = Calendar.getInstance();
        int curDay = calendar.get(Calendar.DAY_OF_YEAR);
        int curWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTimeInMillis(info.get("last_refresh_day"));
        int lastRefreshDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTimeInMillis(info.get("last_refresh_week"));
        int lastRefreshWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if(curDay != lastRefreshDay){
            refreshDayTask();
        }
        if(curWeek != lastRefreshWeek){
            refreshWeekTask();
        }
    }
    @SuppressWarnings("ConstantConditions")
    public void refreshDayTask(){
        HashMap<Long, Integer> unfinishedTask = new HashMap<>();
        for(Task task : curDayTaskList) {
            if(task.isAccumulative()){
                unfinishedTask.put(task.getId(), task.getTotalTimes() - task.getCurrentTimes());
            }
        }
        curDayTaskList.clear();
        for(Task task : dayTaskList) {
            Task cloneTask = task.clone();
            if(unfinishedTask.containsKey(cloneTask.getId())){
                cloneTask.setTotalTimes(cloneTask.getTotalTimes() + unfinishedTask.get(cloneTask.getId()));
            }
            curDayTaskList.add(cloneTask);
        }
        info.put("last_refresh_day", System.currentTimeMillis());
        saveFileData(TaskType.EVERYDAY);
    }
    @SuppressWarnings("ConstantConditions")
    public void refreshWeekTask(){
        HashMap<Long, Integer> unfinishedTask = new HashMap<>();
        for(Task task : curWeekTaskList) {
            if(task.isAccumulative()){
                unfinishedTask.put(task.getId(), task.getTotalTimes() - task.getCurrentTimes());
            }
        }
        curWeekTaskList.clear();
        for(Task task : weekTaskList) {
            Task cloneTask = task.clone();
            if(unfinishedTask.containsKey(cloneTask.getId())){
                cloneTask.setTotalTimes(cloneTask.getTotalTimes() + unfinishedTask.get(cloneTask.getId()));
            }
            curWeekTaskList.add(cloneTask);
        }
        info.put("last_refresh_week", System.currentTimeMillis());
        saveFileData(TaskType.EVERYWEEK);
    }
}

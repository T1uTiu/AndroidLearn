package com.example.learningproject.Manager;

import android.content.Context;

import com.example.learningproject.Model.Task.Task;
import com.example.learningproject.Model.Task.TaskType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private static TaskManager instance;
    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }
    int n;
    List<Task> curDayTaskList = new ArrayList<>();
    List<Task> dayTaskList = new ArrayList<>();
    List<Task> curWeekTaskList = new ArrayList<>();
    List<Task> weekTaskList = new ArrayList<>();
    List<Task> onetimeTaskList = new ArrayList<>();

    final String[] fileName = {"cur_day_task_list", "cur_week_task_list", "onetime_task_list",
            "day_task_list", "week_task_list","onetime_task_list"};
    private TaskManager() {

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
    /*
    public void loadTaskList(Context context, TaskType type) {
        try {
            FileInputStream fis = context.openFileInput(fileName[type.value()]);
            ObjectInputStream ois = new ObjectInputStream(fis);
            FileInputStream fis2 = context.openFileInput(fileName[type.value() + 3]);
            ObjectInputStream ois2 = new ObjectInputStream(fis2);
            switch (type){
                case EVERYDAY:
                    curDayTaskList = (ArrayList<Task>) ois.readObject();
                    dayTaskList = (ArrayList<Task>) ois2.readObject();
                    break;
                case EVERYWEEK:
                    curWeekTaskList = (ArrayList<Task>) ois.readObject();
                    weekTaskList = (ArrayList<Task>) ois2.readObject();
                    break;
                case NORMAL:
                    onetimeTaskList = (ArrayList<Task>) ois.readObject();
                    break;
            }
            n = dayTaskList.size() + weekTaskList.size() + onetimeTaskList.size();
            ois.close();
            ois2.close();
            fis.close();
            fis2.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    */

    public void loadFileData(Context context){
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
    }
    public void saveFileData(Context context, TaskType type){
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
    }
    public void addTask(Task task){
        task.setId(n++);
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
    }
    public void deleteTask(Task task){
        TaskType type = task.getType();
        switch (type){
            case EVERYDAY:
                curDayTaskList.remove(task);
                for(Task t : dayTaskList){
                    if(t.getId() == task.getId()){
                        dayTaskList.remove(t);
                        break;
                    }
                }
                break;
            case EVERYWEEK:
                curWeekTaskList.remove(task);
                for(Task t : weekTaskList){
                    if(t.getId() == task.getId()){
                        weekTaskList.remove(t);
                        break;
                    }
                }
                break;
            case NORMAL:
                onetimeTaskList.remove(task);
                break;
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
            return true;
        }
        return false;
    }
    public void refreshDayTask(){
        for(Task task : dayTaskList) {
            curDayTaskList.add(task.clone());
        }

    }
    public void refreshWeekTask(){
        for(Task task : weekTaskList) {
            curWeekTaskList.add(task.clone());
        }
    }
}

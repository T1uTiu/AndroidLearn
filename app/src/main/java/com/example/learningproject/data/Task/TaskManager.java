package com.example.learningproject.data.Task;

import android.content.Context;

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
    List<Task> dayTaskList = new ArrayList<>();
    List<Task> weekTaskList = new ArrayList<>();
    List<Task> onetimeTaskList = new ArrayList<>();

    final String[] fileName = {"day_task_list", "week_task_list", "onetime_task_list"};
    private TaskManager() {

    }
    public List<Task> getTaskList(TaskType type){
        switch (type){
            case EVERYDAY:
                return dayTaskList;
            case EVERYWEEK:
                return weekTaskList;
            case NORMAL:
                return onetimeTaskList;
            default:
                return null;
        }
    }

    public void loadTaskList(Context context, TaskType type) {
        try {
            FileInputStream fis = context.openFileInput(fileName[type.value()]);
            ObjectInputStream ois = new ObjectInputStream(fis);
            switch (type){
                case EVERYDAY:
                    dayTaskList = (ArrayList<Task>) ois.readObject();
                    break;
                case EVERYWEEK:
                    weekTaskList = (ArrayList<Task>) ois.readObject();
                    break;
                case NORMAL:
                    onetimeTaskList = (ArrayList<Task>) ois.readObject();
                    break;
            }
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void saveFileData(Context context, TaskType type){
        try {
            FileOutputStream fos = context.openFileOutput(fileName[type.value()], Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            switch (type){
                case EVERYDAY:
                    oos.writeObject(dayTaskList);
                    break;
                case EVERYWEEK:
                    oos.writeObject(weekTaskList);
                    break;
                case NORMAL:
                    oos.writeObject(onetimeTaskList);
                    break;
            }
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.example.learningproject.Manager;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.learningproject.Model.Reward.Reward;
import com.example.learningproject.Model.Reward.RewardType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RewardManager {
    @SuppressLint("StaticFieldLeak")
    static RewardManager instance;
    public static RewardManager getInstance(){
        if(instance == null){
            instance = new RewardManager();
        }
        return instance;
    }
    List<Reward> rewardList = new ArrayList<>();
    HashMap<String, Long> info;
    Context context;
    final String[] fileName = {"reward_list", "reward_info"};
    public List<Reward> getRewardList(){
        return rewardList;
    }
    @SuppressWarnings("unchecked")
    public void loadFileData(Context context){
        this.context = context;
        try(FileInputStream fis = context.openFileInput(fileName[0]);
            ObjectInputStream ois = new ObjectInputStream(fis)){
            rewardList = (List<Reward>) ois.readObject();
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        try(FileInputStream fis = context.openFileInput(fileName[1]);
            ObjectInputStream ois = new ObjectInputStream(fis)){
            info = (HashMap<String, Long>) ois.readObject();
        }catch (IOException | ClassNotFoundException e){
            info = new HashMap<>();
            info.put("reward_id", 0L);
        }
    }
    public void saveFileData(){
        try(FileOutputStream fos = context.openFileOutput(fileName[0], Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(rewardList);
        }catch (IOException e){
            e.printStackTrace();
        }
        try(FileOutputStream fos = context.openFileOutput(fileName[1], Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(info);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void addReward(Reward reward){
        reward.setId(info.get("reward_id"));
        info.put("reward_id", info.get("reward_id") + 1);
        rewardList.add(reward);
        saveFileData();
    }
    public boolean finishReward(Reward reward, int idx){
        reward.setCurrentTimes(reward.getCurrentTimes() + 1);
        if(reward.getType() == RewardType.ONESHOT){
            rewardList.remove(idx);
        }
        saveFileData();
        return reward.getType() == RewardType.ONESHOT;
    }

}

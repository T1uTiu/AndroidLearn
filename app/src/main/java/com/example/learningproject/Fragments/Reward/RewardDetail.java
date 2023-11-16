package com.example.learningproject.Fragments.Reward;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.learningproject.Manager.RewardManager;
import com.example.learningproject.Manager.TaskManager;
import com.example.learningproject.Model.Reward.Reward;
import com.example.learningproject.Model.Reward.RewardType;
import com.example.learningproject.Model.Task.Task;
import com.example.learningproject.Model.Task.TaskType;
import com.example.learningproject.R;

public class RewardDetail extends AppCompatActivity {
    Spinner rewardTypeSpinner;
    EditText rewardNameEdit;
    EditText rewardScoreEdit;
    Button okBtn;
    String[] rewardTypes = {"单次奖励", "不限次奖励"};
    String[] titles = {"添加奖励", "编辑奖励"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_detail);
        Intent intent = getIntent();
        Bundle param = intent.getBundleExtra("param");
        int method = param.getInt("method");

        getSupportActionBar().setTitle(titles[method]);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rewardTypeSpinner = findViewById(R.id.detail_reward_type_spinner);
        rewardNameEdit = findViewById(R.id.detail_reward_name_edit);
        rewardScoreEdit = findViewById(R.id.detail_reward_score_edit);
        okBtn = findViewById(R.id.detail_reward_ok_btn);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rewardTypes);
        rewardTypeSpinner.setAdapter(adapter);

        okBtn.setOnClickListener(view -> {
            String rewardRawName = rewardNameEdit.getText().toString();
            String rewardRawScore = rewardScoreEdit.getText().toString();
            if(rewardRawName.isEmpty() || rewardRawScore.isEmpty()){
                Toast.makeText(this, "奖励名称和分数不能为空", Toast.LENGTH_SHORT).show();
            }else{
                int rewardScore = Integer.parseInt(rewardRawScore);
                int rewardRawType = (int)rewardTypeSpinner.getSelectedItemId();
                RewardType rewardType = RewardType.valueOf(rewardRawType);
                Reward reward = new Reward(rewardRawName, rewardScore, rewardType);
                RewardManager.getInstance().addReward(reward);
                finish();
            }



        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
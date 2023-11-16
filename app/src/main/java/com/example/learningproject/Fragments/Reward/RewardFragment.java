package com.example.learningproject.Fragments.Reward;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.learningproject.Fragments.Task.TaskDetail;
import com.example.learningproject.Manager.RewardManager;
import com.example.learningproject.Model.Reward.Reward;
import com.example.learningproject.Model.Reward.RewardType;
import com.example.learningproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RewardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RewardFragment extends Fragment {
    View rootView;
    RewardAdapter adapter;
    FloatingActionButton rewardAddBtn;
    ActivityResultLauncher<Bundle> rewardDetailLauncher;
    public RewardFragment() {
        // Required empty public constructor
    }

    public static RewardFragment newInstance() {
        RewardFragment fragment = new RewardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_reward, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.reward_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(),DividerItemDecoration.VERTICAL));
        adapter = new RewardAdapter(RewardManager.getInstance().getRewardList());
        recyclerView.setAdapter(adapter);

        rewardDetailLauncher = registerForActivityResult(new RewardDetailResultContract(), result -> {
            if(result == 1){
                adapter.notifyItemInserted(adapter.getItemCount() - 1);
            }
        });
        rewardAddBtn = rootView.findViewById(R.id.reward_add_btn);
        rewardAddBtn.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putInt("method", 0);
            rewardDetailLauncher.launch(bundle);
        });

        return rootView;
    }
    static class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder>{

        class RewardViewHolder extends RecyclerView.ViewHolder{
            final TextView rewardNameText;
            final TextView rewardScoreText;
            final TextView rewardTimesText;
            final CheckBox rewardCheck;

            public RewardViewHolder(@NonNull View itemView) {
                super(itemView);
                this.rewardNameText = itemView.findViewById(R.id.reward_name_text);
                this.rewardScoreText = itemView.findViewById(R.id.reward_score_text);
                this.rewardTimesText = itemView.findViewById(R.id.reward_progress_text);
                this.rewardCheck = itemView.findViewById(R.id.reward_check);
                this.rewardCheck.setOnCheckedChangeListener((compoundButton, b) -> {
                    int idx = getAdapterPosition();
                    Reward reward = rewards.get(idx);
                    if(b){
                        if(RewardManager.getInstance().finishReward(reward, idx)){
                            notifyItemRemoved(idx);
                        }else{
                            notifyItemChanged(idx);
                        }
                        this.rewardCheck.setChecked(false);
                    }
                });
            }

            public TextView getRewardNameText() {
                return rewardNameText;
            }

            public TextView getRewardScoreText() {
                return rewardScoreText;
            }

            public TextView getRewardTimesText() {
                return rewardTimesText;
            }
        }
        List<Reward> rewards;
        public RewardAdapter(List<Reward> rewards){
            this.rewards = rewards;
        }
        @NonNull
        @Override
        public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_reward, parent, false);
            return new RewardViewHolder(viewHolder);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
            Reward reward = rewards.get(position);
            holder.getRewardNameText().setText(reward.getName());
            holder.getRewardScoreText().setText(String.format("-%d", reward.getScore()));
            if(reward.getType() == RewardType.ONESHOT){
                holder.getRewardTimesText().setText(String.format("%d/%d", reward.getCurrentTimes(), 1));
            }else{
                holder.getRewardTimesText().setText(String.format("%d/∞", reward.getCurrentTimes()));
            }
        }

        @Override
        public int getItemCount() {
            return rewards.size();
        }


    }

    static class RewardDetailResultContract extends ActivityResultContract<Bundle, Integer> {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Bundle input) {
            Intent intent = new Intent(context, RewardDetail.class);
            intent.putExtra("param", input);
            return intent;
        }

        @Override
        public Integer parseResult(int resultCode, @Nullable Intent intent) {
            return resultCode;
        }
    }
}
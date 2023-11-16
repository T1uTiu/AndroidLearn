package com.example.learningproject.Fragments.Reward;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learningproject.Model.Reward.Reward;
import com.example.learningproject.Model.Reward.RewardType;
import com.example.learningproject.R;

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
        List<Reward> testRewards = new ArrayList<>();
        testRewards.add(new Reward("test1", 10, RewardType.ONESHOT));
        testRewards.add(new Reward("test1", 10, RewardType.INFINITY));
        adapter = new RewardAdapter(testRewards);
        recyclerView.setAdapter(adapter);

        return rootView;
    }
    class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder>{

        class RewardViewHolder extends RecyclerView.ViewHolder{
            final TextView rewardNameText;
            final TextView rewardScoreText;
            final TextView rewardTimesText;

            public RewardViewHolder(@NonNull View itemView) {
                super(itemView);
                this.rewardNameText = itemView.findViewById(R.id.reward_name_text);
                this.rewardScoreText = itemView.findViewById(R.id.reward_score_text);
                this.rewardTimesText = itemView.findViewById(R.id.reward_progress_text);
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
}
package com.example.learningproject.Fragments.Statistics;

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

import com.example.learningproject.Model.ScoreLog;
import com.example.learningproject.R;

import java.util.List;

public class StatisticsFragment extends Fragment {

    View rootView;
    public StatisticsFragment() {
        // Required empty public constructor
    }


    public static StatisticsFragment newInstance() {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_statistics, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.stats_bill_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(),DividerItemDecoration.VERTICAL));
        return rootView;
    }

    class ScoreBillAdapter extends RecyclerView.Adapter<ScoreBillAdapter.ScoreBillViewHolder>{

        class ScoreBillViewHolder extends RecyclerView.ViewHolder{
            TextView timeText;
            TextView outcomeText;
            TextView incomeText;
            TextView surplusText;
            public ScoreBillViewHolder(View itemView){
                super(itemView);
                timeText = itemView.findViewById(R.id.score_bill_time_text);
                outcomeText = itemView.findViewById(R.id.score_bill_outcome_text);
                incomeText = itemView.findViewById(R.id.score_bill_income_text);
                surplusText = itemView.findViewById(R.id.score_bill_surplus_text);
            }

            public TextView getIncomeText() {
                return incomeText;
            }

            public TextView getOutcomeText() {
                return outcomeText;
            }

            public TextView getSurplusText() {
                return surplusText;
            }

            public TextView getTimeText() {
                return timeText;
            }
        }
        List<ScoreLog> scoreLogList;
        public ScoreBillAdapter(List<ScoreLog> scoreLogList){
            this.scoreLogList = scoreLogList;
        }
        @NonNull
        @Override
        public ScoreBillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_scorebill,parent,false);
            return new ScoreBillViewHolder(holder);
        }

        @Override
        public void onBindViewHolder(@NonNull ScoreBillViewHolder holder, int position) {
            ScoreLog scoreLog = scoreLogList.get(position);
            holder.getTimeText().setText((int) scoreLog.getTime());
            int score = scoreLog.getScore();
            if(score > 0){
                holder.getIncomeText().setText(String.valueOf(score));
                holder.getOutcomeText().setText("");
            }else{
                holder.getIncomeText().setText("");
                holder.getOutcomeText().setText(String.valueOf(score));
            }
            holder.getSurplusText().setText("");
        }

        @Override
        public int getItemCount() {
            return scoreLogList.size();
        }


    }
}
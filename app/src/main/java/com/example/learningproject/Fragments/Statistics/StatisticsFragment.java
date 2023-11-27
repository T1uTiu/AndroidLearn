package com.example.learningproject.Fragments.Statistics;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learningproject.Interface.ScoreLogObserver;
import com.example.learningproject.Manager.ScoreManager;
import com.example.learningproject.Model.ScoreLog;
import com.example.learningproject.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatisticsFragment extends Fragment implements ScoreLogObserver {

    View rootView;
    TextView sumOfIncomeText;
    TextView sumOfOutcomeText;
    LineDataSet scoreLogDataSet;
    LineChart lineChart;
    ScoreBillAdapter adapter;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ScoreManager.getInstance().attachScoreLogObserver(this);
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.statistics_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView =  inflater.inflate(R.layout.fragment_statistics, container, false);
        HashMap<Integer, List<ScoreLog>> scoreLogGroup = ScoreManager.getInstance().getScoreLogGroup();
        List<Integer> scoreLogGroupKeyList = ScoreManager.getInstance().getScoreLogGroupKeyList();
        //region 收支总览
        sumOfIncomeText = rootView.findViewById(R.id.stats_sum_of_income_text);
        sumOfOutcomeText = rootView.findViewById(R.id.stats_sum_of_outcome_text);
        int sumOfIncome = 0;
        int sumOfOutcome = 0;
        //region 收支列表
        RecyclerView recyclerView = rootView.findViewById(R.id.stats_bill_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        adapter = new ScoreBillAdapter();
        recyclerView.setAdapter(adapter);
        //endregion
        //region 收支折线图
        lineChart = rootView.findViewById(R.id.stats_line_chart);
        lineChart.getAxisLeft().setDrawZeroLine(true);
        lineChart.setNoDataText("暂无记录");
        List<Entry> entries = new ArrayList<>();
        for(int i = 0; i < scoreLogGroupKeyList.size(); i++){
            int surplus = 0;
            List<ScoreLog> scoreLogList = scoreLogGroup.get(scoreLogGroupKeyList.get(i));
            for(ScoreLog scoreLog : scoreLogList){
                if(scoreLog.getScore() > 0){
                    sumOfIncome += scoreLog.getScore();
                }else{
                    sumOfOutcome -= scoreLog.getScore();
                }
                surplus += scoreLog.getScore();
            }
            Entry e = new Entry(i, surplus);
            entries.add(e);
        }
        scoreLogDataSet = new LineDataSet(entries, "所有记录");
        LineData lineData = new LineData(scoreLogDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();

        sumOfIncomeText.setText(String.valueOf(sumOfIncome));
        sumOfOutcomeText.setText(String.valueOf(sumOfOutcome));
        //endregion
        return rootView;
    }

    @Override
    public void onScoreLogChange(int method, int score) {
        if(method == 0){
            adapter.notifyItemInserted(0);
            scoreLogDataSet.addEntry(new Entry(scoreLogDataSet.getEntryCount(), score));
        }else{
            adapter.notifyItemChanged(0);
            Entry e = scoreLogDataSet.getValues().get(scoreLogDataSet.getEntryCount()-1);
            e.setY(e.getY()+score);

        }
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();

        if(score > 0){
            sumOfIncomeText.setText(String.valueOf(Integer.parseInt(sumOfIncomeText.getText().toString())+score));
        }else{
            sumOfOutcomeText.setText(String.valueOf(Integer.parseInt(sumOfOutcomeText.getText().toString())-score));
        }
    }


    class ScoreBillAdapter extends RecyclerView.Adapter<ScoreBillAdapter.ScoreBillViewHolder>{
        class ScoreBillViewHolder extends RecyclerView.ViewHolder{
            TextView timeText;
            TextView surplusText;
            RecyclerView scoreLogRecyclerView;
            public ScoreBillViewHolder(View itemView){
                super(itemView);
                timeText = itemView.findViewById(R.id.score_bill_time_text);
                surplusText = itemView.findViewById(R.id.score_bill_surplus_text);
                scoreLogRecyclerView = itemView.findViewById(R.id.score_log_recycler_view);
            }

            public TextView getSurplusText() {
                return surplusText;
            }

            public TextView getTimeText() {
                return timeText;
            }

            public RecyclerView getScoreLogRecyclerView() {
                return scoreLogRecyclerView;
            }
        }
        SimpleDateFormat formatter;
        @SuppressLint("SimpleDateFormat")
        ScoreBillAdapter(){
            formatter = new SimpleDateFormat("MM-dd");
        }
        @NonNull
        @Override
        public ScoreBillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_scorebill,parent,false);
            return new ScoreBillViewHolder(holder);
        }

        @Override
        public void onBindViewHolder(@NonNull ScoreBillViewHolder holder, int position) {
            List<Integer> scoreLogGroupKeyList = ScoreManager.getInstance().getScoreLogGroupKeyList();
            int date = scoreLogGroupKeyList.get(scoreLogGroupKeyList.size()-1-position);
            List<ScoreLog> scoreLogList = ScoreManager.getInstance().getScoreLogGroup().get(date);

            String dateString = formatter.format(scoreLogList.get(0).getTime());
            holder.getTimeText().setText(dateString);

            // 统计结余
            int surplus = 0;
            for(ScoreLog scoreLog : scoreLogList){
                surplus += scoreLog.getScore();
            }
            holder.getSurplusText().setText(String.valueOf(surplus));
            int surplusColor = surplus <=0 ? R.color.outcome:R.color.income;
            holder.getSurplusText().setTextColor(ContextCompat.getColor(rootView.getContext(), surplusColor));

            holder.getScoreLogRecyclerView().setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            holder.getScoreLogRecyclerView().setAdapter(new ScoreLogAdapter(date));
            holder.getScoreLogRecyclerView().addItemDecoration(new DividerItemDecoration(rootView.getContext(),DividerItemDecoration.VERTICAL));
        }

        @Override
        public int getItemCount() {
            return ScoreManager.getInstance().getScoreLogGroupKeyList().size();
        }


    }
    class ScoreLogAdapter extends RecyclerView.Adapter<ScoreLogAdapter.ScoreLogViewHolder>{
        class ScoreLogViewHolder extends RecyclerView.ViewHolder{
            TextView scoreLogNameText;
            TextView scoreLogTimeText;
            TextView scoreLogScoreText;
            ImageView scoreLogIcon;
            public ScoreLogViewHolder(@NonNull View itemView) {
                super(itemView);
                scoreLogNameText = itemView.findViewById(R.id.score_log_name_text);
                scoreLogTimeText = itemView.findViewById(R.id.score_log_time_text);
                scoreLogScoreText = itemView.findViewById(R.id.score_log_score_text);
                scoreLogIcon = itemView.findViewById(R.id.score_log_icon);
            }
        }
        int date;
        List<ScoreLog> scoreLogList;
        SimpleDateFormat formatter;

        @SuppressLint("SimpleDateFormat")
        public ScoreLogAdapter(int date){
            this.date = date;
            scoreLogList = ScoreManager.getInstance().getScoreLogGroup().get(date);
            formatter = new SimpleDateFormat("HH:mm");
        }
        @NonNull
        @Override
        public ScoreLogAdapter.ScoreLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_score_log, parent, false);
            return new ScoreLogViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ScoreLogAdapter.ScoreLogViewHolder holder, int position) {
            int idx = scoreLogList.size()-1-position;
            String timeString = formatter.format(scoreLogList.get(idx).getTime());
            holder.scoreLogTimeText.setText(timeString);
            holder.scoreLogNameText.setText(scoreLogList.get(idx).getName());
            int score = scoreLogList.get(idx).getScore();
            holder.scoreLogScoreText.setText(String.valueOf(score));
            if(score < 0){
                holder.scoreLogScoreText.setTextColor(ContextCompat.getColor(rootView.getContext(), R.color.outcome));
                holder.scoreLogIcon.setImageResource(R.drawable.ic_outcome);
            }else{
                holder.scoreLogScoreText.setTextColor(ContextCompat.getColor(rootView.getContext(), R.color.income));
                holder.scoreLogIcon.setImageResource(R.drawable.ic_income);
            }
        }

        @Override
        public int getItemCount() {
            return scoreLogList.size();
        }
    }
}
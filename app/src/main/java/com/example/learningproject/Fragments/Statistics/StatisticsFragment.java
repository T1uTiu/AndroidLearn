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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learningproject.Interface.ScoreLogObserver;
import com.example.learningproject.Manager.ScoreManager;
import com.example.learningproject.Model.ScoreLog;
import com.example.learningproject.R;
import com.example.learningproject.Utils.ScoreLogXAxisValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class StatisticsFragment extends Fragment implements ScoreLogObserver {

    View rootView;
    TextView currentIncomeText, currentOutcomeText, currentScoreText;
    BarDataSet scoreLogDataSet;
    BarChart lineChart;
    ScoreLogXAxisValueFormatter xAxisValueFormatter;
    ScoreBillAdapter adapter;
    int chartTotalDays = 15;
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
        ScoreManager.getInstance().attachScoreLogObserver(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView =  inflater.inflate(R.layout.fragment_statistics, container, false);
        initOverviewCard();
        initScoreBill();
        initChart();
        return rootView;
    }
    void initScoreBill(){
        RecyclerView recyclerView = rootView.findViewById(R.id.stats_bill_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        adapter = new ScoreBillAdapter();
        recyclerView.setAdapter(adapter);
    }
    void initChart(){
        HashMap<Integer, List<ScoreLog>> scoreLogGroup = ScoreManager.getInstance().getScoreLogGroup();
        lineChart = rootView.findViewById(R.id.stats_line_chart);
        lineChart.getAxisLeft().setDrawZeroLine(true);
        lineChart.setNoDataText("近15天无记录");
        List<BarEntry> entries = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = ScoreManager.getInstance().getDateFormat();
        for(int i = chartTotalDays-1; i >= 0; i--){
            int surplus = 0;
            int key = Integer.parseInt(dateFormat.format(calendar.getTime()));
            if(scoreLogGroup.containsKey(key)){
                List<ScoreLog> scoreLogList = scoreLogGroup.get(key);
                assert scoreLogList != null;
                for(ScoreLog scoreLog : scoreLogList){
                    surplus += scoreLog.getScore();
                }
            }
            entries.add(new BarEntry(i, surplus));
            // 前一天
            calendar.add(Calendar.DATE, -1);
        }
        scoreLogDataSet = new BarDataSet(entries, "结余");
        scoreLogDataSet.setColor(ContextCompat.getColor(rootView.getContext(), R.color.purple_500));
        BarData lineData = new BarData(scoreLogDataSet);
        xAxisValueFormatter = new ScoreLogXAxisValueFormatter(chartTotalDays);
        lineChart.getXAxis().setValueFormatter(xAxisValueFormatter);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setLabelRotationAngle(-60);
        lineChart.setDescription(null);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    private void initOverviewCard() {
        currentIncomeText = rootView.findViewById(R.id.stats_sum_of_income_text);
        currentOutcomeText = rootView.findViewById(R.id.stats_sum_of_outcome_text);
        currentScoreText = rootView.findViewById(R.id.stats_overview_text);

        currentIncomeText.setText(String.valueOf(ScoreManager.getInstance().getCurrentIncome()));
        currentOutcomeText.setText(String.valueOf(ScoreManager.getInstance().getCurrentOutcome()));
        currentScoreText.setText(String.valueOf(ScoreManager.getInstance().getCurrentScore()));
    }

    @Override
    public void onScoreLogChange(int method, int score) {
        if(method == 1){
            adapter.notifyItemChanged(0);
        }else{
            adapter.notifyItemInserted(0);
        }
        BarEntry e = scoreLogDataSet.getValues().get(0);
        e.setY(e.getY()+score);



        lineChart.notifyDataSetChanged();
        lineChart.invalidate();

        if(score > 0){
            currentIncomeText.setText(String.valueOf(ScoreManager.getInstance().getCurrentIncome()));
        }else{
            currentOutcomeText.setText(String.valueOf(ScoreManager.getInstance().getCurrentOutcome()));
        }
        currentScoreText.setText(String.valueOf(ScoreManager.getInstance().getCurrentScore()));
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

        ScoreBillAdapter(){
            formatter = new SimpleDateFormat("MM-dd", Locale.getDefault());
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
            assert scoreLogList != null;
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
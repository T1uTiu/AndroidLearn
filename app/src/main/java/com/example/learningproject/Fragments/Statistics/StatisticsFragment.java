package com.example.learningproject.Fragments.Statistics;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learningproject.Activities.CalendarStatisticsActivity;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class StatisticsFragment extends Fragment implements ScoreLogObserver {

    View rootView;
    TextView currentIncomeText, currentOutcomeText, currentScoreText;
    BarDataSet scoreLogDataSet;
    FloatingActionButton toCalendarStatsBtn;
    BarChart surplusChart;
    ScoreLogXAxisValueFormatter xAxisValueFormatter;
    ActivityResultLauncher<Intent> activityResultLauncher;
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
        initChart();
        initFab();
        return rootView;
    }
    void initFab(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {});
        toCalendarStatsBtn = rootView.findViewById(R.id.to_calendar_stats_fab);
        toCalendarStatsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(rootView.getContext(), CalendarStatisticsActivity.class);
            activityResultLauncher.launch(intent);
        });
    }
    void initChart(){
        HashMap<Integer, List<ScoreLog>> scoreLogGroup = ScoreManager.getInstance().getScoreLogGroup();
        surplusChart = rootView.findViewById(R.id.stats_line_chart);
        List<BarEntry> entries = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        // 统计的第一天
        calendar.add(Calendar.DATE, -chartTotalDays+1);
        xAxisValueFormatter = new ScoreLogXAxisValueFormatter(calendar.getTimeInMillis());
        SimpleDateFormat dateFormat = ScoreManager.getInstance().getDateFormat();
        for(int i = 0; i < chartTotalDays; i++){

            int key = Integer.parseInt(dateFormat.format(calendar.getTimeInMillis()));
            int income = ScoreManager.getInstance().getDailyIncome().getOrDefault(key, 0);
            int outcome = ScoreManager.getInstance().getDailyOutcome().getOrDefault(key, 0);
            int surplus = income-outcome;
            entries.add(new BarEntry(i, surplus));
            // 前一天
            calendar.add(Calendar.DATE, 1);
        }
        scoreLogDataSet = new BarDataSet(entries, "结余");
        scoreLogDataSet.setColor(ContextCompat.getColor(rootView.getContext(), R.color.purple_500));
        BarData lineData = new BarData(scoreLogDataSet);
        surplusChart.getXAxis().setValueFormatter(xAxisValueFormatter);
        surplusChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        surplusChart.getXAxis().setLabelRotationAngle(-60);
        surplusChart.setDescription(null);
        surplusChart.setData(lineData);
        surplusChart.invalidate();
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
        BarEntry e = scoreLogDataSet.getValues().get(chartTotalDays-1);
        e.setY(e.getY()+score);

        surplusChart.notifyDataSetChanged();
        surplusChart.invalidate();

        if(score > 0){
            currentIncomeText.setText(String.valueOf(ScoreManager.getInstance().getCurrentIncome()));
        }else{
            currentOutcomeText.setText(String.valueOf(ScoreManager.getInstance().getCurrentOutcome()));
        }
        currentScoreText.setText(String.valueOf(ScoreManager.getInstance().getCurrentScore()));
    }



}
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
    List<Entry> entries;
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
        //region 收支总览
        sumOfIncomeText = rootView.findViewById(R.id.stats_sum_of_income_text);
        sumOfOutcomeText = rootView.findViewById(R.id.stats_sum_of_outcome_text);
        //region 收支列表
        RecyclerView recyclerView = rootView.findViewById(R.id.stats_bill_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        adapter = new ScoreBillAdapter();
        recyclerView.setAdapter(adapter);
        //endregion
        //region 收支折线图
        lineChart = rootView.findViewById(R.id.stats_line_chart);
        lineChart.setNoDataText("暂无记录");
        entries = new ArrayList<Entry>();
        for(int i = 0; i < ScoreManager.getInstance().getScoreLogGroupKeyList().size(); i++){
            int surplus = 0;
            HashMap<Integer, List<ScoreLog>> scoreLogGroup = ScoreManager.getInstance().getScoreLogGroup();
            List<Integer> scoreLogGroupKeyList = ScoreManager.getInstance().getScoreLogGroupKeyList();
            List<ScoreLog> scoreLogList = scoreLogGroup.get(scoreLogGroupKeyList.get(i));
            for(ScoreLog scoreLog : scoreLogList){
                surplus += scoreLog.getScore();
            }
            entries.add(new Entry(i, surplus));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
        //endregion
        return rootView;
    }

    @Override
    public void onScoreLogChange(int method, int score) {
        if(method == 0){
            adapter.notifyItemInserted(adapter.getItemCount()-1);
            entries.add(new Entry(entries.size(), score));
        }else{
            adapter.notifyItemChanged(adapter.getItemCount()-1);
            entries.get(entries.size()-1).setY(entries.get(entries.size()-1).getY()+score);
        }

        lineChart.invalidate();
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
            public ScoreLogViewHolder(@NonNull View itemView) {
                super(itemView);
                scoreLogNameText = itemView.findViewById(R.id.score_log_name_text);
                scoreLogTimeText = itemView.findViewById(R.id.score_log_time_text);
                scoreLogScoreText = itemView.findViewById(R.id.score_log_score_text);
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
            String timeString = formatter.format(scoreLogList.get(position).getTime());
            holder.scoreLogTimeText.setText(timeString);
            holder.scoreLogNameText.setText(scoreLogList.get(position).getName());
            int score = scoreLogList.get(position).getScore();
            holder.scoreLogScoreText.setText(String.valueOf(score));
            if(score < 0){
                holder.scoreLogScoreText.setTextColor(ContextCompat.getColor(rootView.getContext(), R.color.outcome));
            }else{
                holder.scoreLogScoreText.setTextColor(ContextCompat.getColor(rootView.getContext(), R.color.income));
            }
        }

        @Override
        public int getItemCount() {
            return scoreLogList.size();
        }
    }
}
package com.example.learningproject.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningproject.Manager.ScoreManager
import com.example.learningproject.Model.ScoreLog
import com.example.learningproject.R
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import java.text.SimpleDateFormat
import java.util.*

class CalendarStatisticsActivity : AppCompatActivity()
        , CalendarView.OnCalendarSelectListener {
    private lateinit var adapter : ScoreLogAdapter
    private lateinit var statsCalendar: CalendarView
    private var toolbarDateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    private var selectDate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_statistics)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        statsCalendar = findViewById(R.id.statistics_calendar)

        initBill()
    }

    override fun onStart() {
        super.onStart()
        statsCalendar.setOnCalendarSelectListener(this)
    }
    private fun initBill(){
        val recyclerView: RecyclerView = findViewById(R.id.calendar_stats_bill_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val formatter = ScoreManager.getInstance().dateFormat
        selectDate = statsCalendar.selectedCalendar.timeInMillis
        supportActionBar?.title = toolbarDateFormat.format(selectDate)
        val date = formatter.format(selectDate).toInt()
        adapter = ScoreLogAdapter(date, this)
        recyclerView.adapter = adapter
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
        val formatter = ScoreManager.getInstance().dateFormat
        selectDate = calendar!!.timeInMillis
        supportActionBar?.title = toolbarDateFormat.format(selectDate)
        val date = formatter.format(selectDate).toInt()
        adapter.date = date
        adapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
class ScoreLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var scoreLogNameText: TextView
    var scoreLogTimeText: TextView
    var scoreLogScoreText: TextView
    var scoreLogIcon: ImageView

    init {
        scoreLogNameText = itemView.findViewById(R.id.score_log_name_text)
        scoreLogTimeText = itemView.findViewById(R.id.score_log_time_text)
        scoreLogScoreText = itemView.findViewById(R.id.score_log_score_text)
        scoreLogIcon = itemView.findViewById(R.id.score_log_icon)
    }
}
class ScoreLogAdapter(date: Int, val context: Context) : RecyclerView.Adapter<ScoreLogViewHolder>() {
    private var scoreLogList: List<ScoreLog> = ScoreManager.getInstance().scoreLogGroup[date] ?: mutableListOf()
    private var formatter: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    var date = date
        set(value) {
            field = value
            scoreLogList = ScoreManager.getInstance().scoreLogGroup[date] ?: mutableListOf()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreLogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_score_log, parent, false)
        return ScoreLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreLogViewHolder, position: Int) {
        val idx = scoreLogList.size - 1 - position
        val timeString = formatter.format(scoreLogList[idx].time)
        holder.scoreLogTimeText.text = timeString
        holder.scoreLogNameText.text = scoreLogList[idx].name
        val score = scoreLogList[idx].score
        holder.scoreLogScoreText.text = score.toString()
        if (score < 0) {
            holder.scoreLogScoreText.setTextColor(ContextCompat.getColor(context, R.color.outcome))
            holder.scoreLogIcon.setImageResource(R.drawable.ic_outcome)
        } else {
            holder.scoreLogScoreText.setTextColor(ContextCompat.getColor(context, R.color.income))
            holder.scoreLogIcon.setImageResource(R.drawable.ic_income)
        }
    }

    override fun getItemCount(): Int {
        return scoreLogList.size
    }
}
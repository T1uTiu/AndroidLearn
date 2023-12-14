package com.example.learningproject.Utils

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class ScoreLogXAxisValueFormatter(private val firstDay: Long): ValueFormatter() {
    private val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
    override fun getFormattedValue(value: Float): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = firstDay
        calendar.add(Calendar.DATE, value.toInt())
        return dateFormat.format(calendar.timeInMillis)
    }
}
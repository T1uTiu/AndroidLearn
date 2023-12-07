package com.example.learningproject.Utils

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class ScoreLogXAxisValueFormatter(var total: Int): ValueFormatter() {
    private val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
    override fun getFormattedValue(value: Float): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, value.toInt()-total+1)
        return dateFormat.format(calendar.time)
    }
}
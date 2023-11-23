package com.example.learningproject.Utils

import com.example.learningproject.Manager.ScoreManager
import com.github.mikephil.charting.formatter.ValueFormatter

class ScoreLogXAxisValueFormatter: ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return ScoreManager.getInstance().scoreLogGroupKeyList[value.toInt()].toString()
    }
}
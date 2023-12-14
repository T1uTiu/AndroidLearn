package com.example.learningproject.Utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat
import com.example.learningproject.Manager.ScoreManager
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.MonthView
import com.example.learningproject.R
import kotlin.Boolean
import kotlin.Int


class StatisticsMonthView(context: Context?) : MonthView(context) {
    private val incomePaint = Paint()
    private val outcomePaint = Paint()
    init {
        incomePaint.color = ResourcesCompat.getColor(resources, R.color.income, null)
        incomePaint.style = Paint.Style.FILL
        incomePaint.textSize = 35f
        incomePaint.textAlign = Paint.Align.CENTER
        outcomePaint.color = ResourcesCompat.getColor(resources, R.color.outcome, null)
        outcomePaint.style = Paint.Style.FILL
        outcomePaint.textSize = 35f
        outcomePaint.textAlign = Paint.Align.CENTER
    }
    override fun onDrawSelected(canvas: Canvas?, calendar: Calendar?, x: Int, y: Int, hasScheme: Boolean): Boolean {
        val right = x + mItemWidth
        val bottom = y + mItemHeight
        canvas!!.drawRect(x.toFloat(), y.toFloat(), right.toFloat(), bottom.toFloat(), mSelectedPaint)
        return false
    }

    override fun onDrawScheme(canvas: Canvas?, calendar: Calendar?, x: Int, y: Int) {
        val right = x + mItemWidth
        val bottom = y + mItemHeight
        canvas!!.drawRect(x.toFloat(), y.toFloat(), right.toFloat(), bottom.toFloat(), mSchemePaint)
    }

    override fun onDrawText(canvas: Canvas?, calendar: Calendar?, x: Int, y: Int, hasScheme: Boolean, isSelected: Boolean) {
        val baselineY = y + mItemHeight.toFloat() / 2
        val cx = x + mItemWidth / 2


        canvas!!.drawText(calendar!!.day.toString(), cx.toFloat(), baselineY,
            if (calendar.isCurrentMonth) mCurMonthTextPaint else mOtherMonthTextPaint)
        val dateFormat = ScoreManager.getInstance().dateFormat
        val date = dateFormat.format(calendar.timeInMillis).toInt()
        val income = ScoreManager.getInstance().dailyIncome.getOrDefault(date, 0)
        val outcome = ScoreManager.getInstance().dailyOutcome.getOrDefault(date, 0)
        val surplus = income - outcome
        if(surplus > 0){
            canvas.drawText((income-outcome).toString(), cx.toFloat(), baselineY + mItemHeight / 4, incomePaint)
        }else{
            canvas.drawText((income-outcome).toString(), cx.toFloat(), baselineY + mItemHeight / 4, outcomePaint)
        }

    }
}
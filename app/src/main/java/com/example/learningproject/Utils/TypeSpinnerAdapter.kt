package com.example.learningproject.Utils
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.learningproject.R

class TypeSpinnerAdapter(private val context: Context, private val types: Array<String>): BaseAdapter() {
    override fun getCount(): Int {
        return types.size
    }

    override fun getItem(p0: Int): Any {
        return types[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.listitem_type_spinner, p2, false)
        val textView: TextView = itemView.findViewById(R.id.type_spinner_text)
        textView.text = types[p0]
        return itemView
    }
}
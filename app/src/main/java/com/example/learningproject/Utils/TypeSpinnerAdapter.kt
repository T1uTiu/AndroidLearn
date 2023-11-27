package com.example.learningproject.Utils
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.learningproject.R

class TypeSpinnerAdapter(private val context: Context, private val types: Array<String>): BaseAdapter() {
    inner class ViewHolder(val itemView: View){
        val textView: TextView = itemView.findViewById(R.id.type_spinner_text)
    }
    override fun getCount(): Int {
        return types.size
    }

    override fun getItem(p0: Int): Any {
        return types[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val holder: ViewHolder
        if(p1 == null) {
            val itemView = inflater.inflate(R.layout.listitem_type_spinner, p2, false)
            holder = ViewHolder(itemView)
            itemView.tag = holder
        }else{
            holder = p1.tag as ViewHolder
        }
        holder.textView.text = types[p0]
        return holder.itemView;
    }
}
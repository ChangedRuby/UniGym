package com.example.unigym2.Fragments.Calendar.Recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore

class CalendarPersonalAdapter(private val scheduleList: ArrayList<CalendarPersonalItem>) : RecyclerView.Adapter<CalendarPersonalAdapter.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.calendario_user_recycler_layout,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val currentItem = scheduleList[position]

        holder.nameView.text = currentItem.nomePersonal

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var nameView : TextView = itemView.findViewById(R.id.calendarioUserNameView)
    }
}
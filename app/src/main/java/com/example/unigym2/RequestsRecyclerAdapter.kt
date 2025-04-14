package com.example.unigym2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RequestsRecyclerAdapter(private val requestsList: ArrayList<RequestsData>) : RecyclerView.Adapter<RequestsRecyclerAdapter.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_layout,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return requestsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = requestsList[position]
        holder.nameView.text = currentItem.name

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val nameView : TextView = itemView.findViewById(R.id.userName)
    }
}
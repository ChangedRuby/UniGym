package com.example.unigym2.Fragments.Calendar.Recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore

class CalendarUserAdapter(private val scheduleList: ArrayList<CalendarUserItem>) : RecyclerView.Adapter<CalendarUserAdapter.MyViewHolder>(){



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

        holder.nomePersonal.text = currentItem.nomePersonal
        holder.horario.text = currentItem.nomePersonal
        holder.servico.text = currentItem.servico


    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var nomePersonal: TextView = itemView.findViewById(R.id.nomePersonalView)
        var horario: TextView = itemView.findViewById(R.id.horarioUserView)
        var servico: TextView = itemView.findViewById(R.id.tipoDeServicoView)
    }
}
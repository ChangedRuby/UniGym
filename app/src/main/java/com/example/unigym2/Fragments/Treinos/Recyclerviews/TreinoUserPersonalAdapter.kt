package com.example.unigym2.Fragments.Treinos.Recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.R

class TreinoUserPersonalAdapter(private val dataList: ArrayList<TreinoUserPersonalItem>) : RecyclerView.Adapter<TreinoUserPersonalAdapter.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.treinos_usuario_personal_recycler_layout,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.repeticoesView.text = currentItem.repeticoes.toString()
        holder.deleteView.setOnClickListener {
            dataList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, dataList.size)

        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val deleteView: ImageView = itemView.findViewById(R.id.removeExercicioButton)
        val repeticoesView : TextView = itemView.findViewById(R.id.repeticoesText)
    }
}
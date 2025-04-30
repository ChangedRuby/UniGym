package com.example.unigym2.Fragments.Chat.Recyclerviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Chat.ChatMain
import com.example.unigym2.Fragments.Profile.VisualizarPerfilPersonal
import com.example.unigym2.Fragments.Profile.VisualizarPerfilUser
import com.example.unigym2.Fragments.Treinos.Recyclerviews.ListaTreinosItem
import com.example.unigym2.Fragments.Treinos.Recyclerviews.ListaUsuariosClickListener
import com.example.unigym2.R

class ListaUsuariosAdapter(private val dataList: MutableList<ListaPersonaisItem>, var communicator: Communicator) : RecyclerView.Adapter<ListaUsuariosAdapter.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.usuarios_chat_recycler_layout,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.nameView.text = currentItem.name
        holder.itemView.setOnClickListener {
            communicator.replaceFragment(ChatMain())

        }

        holder.visualizarPerfilBtn.setOnClickListener {
//            fragmentManager.setFragmentResult("user_info_key", Bundle().apply {
//                putString("user_name", holder.nameView.text.toString())
//            })
            communicator.replaceFragment(VisualizarPerfilUser())
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val visualizarPerfilBtn : Button = itemView.findViewById(R.id.visualizarPerfilBtn)
        val nameView : TextView = itemView.findViewById(R.id.textViewUsername)
    }
}
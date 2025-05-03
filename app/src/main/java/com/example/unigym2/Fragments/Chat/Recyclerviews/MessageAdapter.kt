package com.example.unigym2.Fragments.Chat.Recyclerviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Chat.ChatMain
import com.example.unigym2.Fragments.Profile.VisualizarPerfilPersonal
import com.example.unigym2.Fragments.Profile.VisualizarPerfilUser
import com.example.unigym2.Fragments.Treinos.Recyclerviews.ListaTreinosItem
import com.example.unigym2.Fragments.Treinos.Recyclerviews.ListaUsuariosClickListener
import com.example.unigym2.R

class MessageAdapter(private val messageList: MutableList<MessageItem>) : RecyclerView.Adapter<MessageAdapter.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.message_chat_recycler_layout,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = messageList[position]


    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val messageView : TextView = itemView.findViewById(R.id.messageRecvTextview)
        val messageRecvLayout: LinearLayout = itemView.findViewById(R.id.messageRecvLayout)
        val messageSentLayout: LinearLayout = itemView.findViewById(R.id.messageSentLayout)

    }
}
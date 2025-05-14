package com.example.unigym2.Fragments.Chat.Recyclerviews

import android.os.Message
import android.service.carrier.CarrierMessagingService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.runtime.currentComposer
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.R
import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList:
ArrayList<com.example.unigym2.Fragments.Chat.Recyclerviews.Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        val ITEM_RECEIVE = 1
        val ITEM_SENT = 2

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int,): RecyclerView.ViewHolder {

            if(viewType == 1){
                //Inflate receive
                val view: View = LayoutInflater.from(context).inflate(R.layout.message_received, parent, false)
                return ReceiveViewHolder(view)
            } else{
                //inflate sent
                val view: View = LayoutInflater.from(context).inflate(R.layout.message_sent, parent, false)
                return SentViewHolder(view)
            }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder,position: Int,) {

        val currentMessage = messageList[position]


        if(holder.javaClass == SentViewHolder::class.java){
            //Paradas para o SentViewHolder

            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message

        } else{
            //Paradas para o ReceiveViewHolder
            val viewHolder = holder as ReceiveViewHolder
            holder.receivedMessage.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        } else {
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receivedMessage = itemView.findViewById<TextView>(R.id.txt_received_message)
    }
}
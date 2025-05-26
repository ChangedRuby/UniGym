package com.example.unigym2.Fragments.Chat.Recyclerviews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Managers.AvatarManager
import com.example.unigym2.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2
    val ITEM_RECEIVE_IMAGE = 3
    val ITEM_SENT_IMAGE = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            ITEM_RECEIVE -> ReceiveViewHolder(inflater.inflate(R.layout.message_received, parent, false))
            ITEM_SENT -> SentViewHolder(inflater.inflate(R.layout.message_sent, parent, false))
            ITEM_RECEIVE_IMAGE -> ReceiveImageViewHolder(inflater.inflate(R.layout.message_received_image, parent, false))
            ITEM_SENT_IMAGE -> SentImageViewHolder(inflater.inflate(R.layout.message_sent_image, parent, false))
            else -> ReceiveViewHolder(inflater.inflate(R.layout.message_received, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        val timeFormatted = currentMessage.timestamp?.let { formatTimestamp(it) } ?: "--:--"

        when (holder) {
            is SentViewHolder -> {
                holder.sentMessage.text = currentMessage.message
                holder.sentTime.text = timeFormatted
            }
            is ReceiveViewHolder -> {
                holder.receivedMessage.text = currentMessage.message
                holder.receivedTime.text = timeFormatted
            }
            is SentImageViewHolder -> {
                holder.sentImageMessage.setImageBitmap(AvatarManager.base64ToBitmap(currentMessage.message ?: ""))
            }
            is ReceiveImageViewHolder -> {
                holder.receivedImageMessage.setImageBitmap(AvatarManager.base64ToBitmap(currentMessage.message ?: ""))
            }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("GMT-3")
        return format.format(date)
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid == currentMessage.senderId) {
            if (AvatarManager.isJpeg(currentMessage.message ?: "")) ITEM_SENT_IMAGE else ITEM_SENT
        } else {
            if (AvatarManager.isJpeg(currentMessage.message ?: "")) ITEM_RECEIVE_IMAGE else ITEM_RECEIVE
        }
    }

    override fun getItemCount() = messageList.size

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage: TextView = itemView.findViewById(R.id.txt_sent_message)
        val sentTime: TextView = itemView.findViewById(R.id.txt_timestamp_sent)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receivedMessage: TextView = itemView.findViewById(R.id.txt_received_message)
        val receivedTime: TextView = itemView.findViewById(R.id.txt_timestamp_received)
    }

    class SentImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentImageMessage: ShapeableImageView = itemView.findViewById(R.id.txt_sent_image)
    }

    class ReceiveImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receivedImageMessage: ShapeableImageView = itemView.findViewById(R.id.txt_received_image)
    }
}

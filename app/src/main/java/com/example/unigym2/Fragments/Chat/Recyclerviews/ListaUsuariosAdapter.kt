package com.example.unigym2.Fragments.Chat.Recyclerviews

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Chat.ChatMain
import com.example.unigym2.R
import com.google.android.material.imageview.ShapeableImageView

class ListaUsuariosAdapter(
    private val itemArray: MutableList<ListaPersonaisItem>,
    private val communicator: Communicator,
    private val parentFragmentManager: FragmentManager
) : RecyclerView.Adapter<ListaUsuariosAdapter.ListaUsuariosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaUsuariosViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.usuarios_chat_recycler_layout, parent, false)
        return ListaUsuariosViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListaUsuariosViewHolder, position: Int) {
        val currentItem = itemArray[position]

        holder.nameView.text = currentItem.name
        holder.userImage.setImageBitmap(currentItem.image)

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("name", currentItem.name)
                putString("receiverID", currentItem.userId)
            }

            parentFragmentManager.setFragmentResult("chat_name_key", bundle)

            communicator.replaceFragment(ChatMain())
        }
    }

    override fun getItemCount(): Int = itemArray.size

    class ListaUsuariosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.textViewUsername)
        val userImage: ShapeableImageView = itemView.findViewById(R.id.profileChatListaImage)
    }
}

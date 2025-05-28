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
import com.example.unigym2.Fragments.Profile.VisualizarPerfilPersonal
import com.example.unigym2.Fragments.Profile.VisualizarPerfilUser
import com.example.unigym2.Managers.AvatarManager
import com.example.unigym2.R
import com.google.android.material.imageview.ShapeableImageView

class ListaUsuariosAdapter(
    private val itemArray: MutableList<ListaUsuariosItem>,
    private val communicator: Communicator,
    private val fragmentManager: FragmentManager,
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
                putString("imageBase64", AvatarManager.bitmapToBase64(currentItem.image!!, 100))
                putString("receiverID", currentItem.userId)
            }

            fragmentManager.setFragmentResult("chat_name_key", bundle)
            communicator.replaceFragment(ChatMain())
        }

        if (currentItem.userId == "BROK_AI_AGENT") {
            holder.visualizarPerfilBtn.visibility = View.GONE
            holder.nameView.text = "Brok"
            holder.descriptionView.text = "Converse com um agente de IA."
        } else {
            holder.visualizarPerfilBtn.visibility = View.VISIBLE
            holder.descriptionView.text = "Treino com personal"
        }

        holder.visualizarPerfilBtn.setOnClickListener {
            fragmentManager.setFragmentResult("user_info_key", Bundle().apply {
                putString("user_name", currentItem.name)
                putString("user_id", currentItem.userId)
            })
            fragmentManager.setFragmentResult("user_monitoring_key", Bundle().apply {
                putString("user_name", currentItem.name)
                putString("user_id", currentItem.userId)
            })
            communicator.replaceFragment(VisualizarPerfilUser())

        }
    }

    override fun getItemCount(): Int = itemArray.size

    class ListaUsuariosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.textViewUsername)
        val userImage: ShapeableImageView = itemView.findViewById(R.id.profileChatListaImage)
        val visualizarPerfilBtn: ImageView = itemView.findViewById(R.id.visualizarPerfilBtn)
        val descriptionView: TextView = itemView.findViewById(R.id.userDescription)
    }
}

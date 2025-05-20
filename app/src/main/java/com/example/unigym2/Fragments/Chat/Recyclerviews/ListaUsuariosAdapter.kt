package com.example.unigym2.Fragments.Chat.Recyclerviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Chat.ChatMain
import com.example.unigym2.Fragments.Profile.VisualizarPerfilPersonal
import com.example.unigym2.Fragments.Profile.VisualizarPerfilUser
import com.example.unigym2.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore

class ListaUsuariosAdapter(private val dataList: MutableList<ListaPersonaisItem>, var communicator: Communicator, val fragmentManager : FragmentManager) : RecyclerView.Adapter<ListaUsuariosAdapter.MyViewHolder>(){

    lateinit var db: FirebaseFirestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.usuarios_chat_recycler_layout,
            parent, false)
        db = FirebaseFirestore.getInstance()
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.nameView.text = currentItem.name
        holder.usuarioImage.setImageBitmap(currentItem.image)
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("name", currentItem.name)
                putString("recieverID", currentItem.userId)
            }

            fragmentManager.setFragmentResult("chat_name_key", bundle)
            communicator.replaceFragment(ChatMain())
        }

        Guif (currentItem.userId == "BROK_AI_AGENT") {
            holder.visualizarPerfilBtn.visibility = View.GONE
            holder.nameView.text = "Brok"
            holder.descriptionView.text = "Converse com um agente de IA"
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

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val visualizarPerfilBtn : AppCompatImageView = itemView.findViewById(R.id.visualizarPerfilBtn)
        val nameView : TextView = itemView.findViewById(R.id.textViewUsername)
        val descriptionView: TextView = itemView.findViewById(R.id.userDescription)
        val usuarioImage: ShapeableImageView = itemView.findViewById(R.id.profileChatListaImage)
    }
}
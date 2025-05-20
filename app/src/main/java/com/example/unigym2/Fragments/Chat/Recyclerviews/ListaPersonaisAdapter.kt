package com.example.unigym2.Fragments.Chat.Recyclerviews

import android.os.Bundle
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
import com.example.unigym2.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore

class ListaPersonaisAdapter(private val dataList : MutableList<ListaPersonaisItem>, var communicator : Communicator, val fragmentManager : FragmentManager) : RecyclerView.Adapter<ListaPersonaisAdapter.MyViewHolder>(){

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
        holder.personalImage.setImageBitmap(currentItem.image)

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("name", currentItem.name)
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
            fragmentManager.setFragmentResult("personal_info_key", Bundle().apply {
                putString("personal_name", currentItem.name)
                putString("personal_id", currentItem.userId)
            })
            fragmentManager.setFragmentResult("personal_monitoring_key", Bundle().apply {
                putString("personal_name", currentItem.name)
                putString("personal_id", currentItem.userId)
            })
            communicator.replaceFragment(VisualizarPerfilPersonal())

        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val visualizarPerfilBtn : ImageView = itemView.findViewById(R.id.visualizarPerfilBtn)
        val nameView : TextView = itemView.findViewById(R.id.textViewUsername)
        val descriptionView: TextView = itemView.findViewById(R.id.userDescription)
        val personalImage: ShapeableImageView = itemView.findViewById(R.id.profileChatListaImage)
    }
}
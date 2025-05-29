package com.example.unigym2.Fragments.Treinos.Recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.R
import com.google.android.material.imageview.ShapeableImageView

class ListaTreinosAdapter(private val dataList: ArrayList<ListaTreinosItem>, private val usuarioClickListener: ListaUsuariosClickListener) : RecyclerView.Adapter<ListaTreinosAdapter.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.usuarios_recycler_layout,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.nameView.text = currentItem.name
        holder.profileImageView.setImageBitmap(currentItem.image)
        holder.visualizarTreinoButton.setOnClickListener {
            usuarioClickListener.onItemClick(currentItem)
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val nameView : TextView = itemView.findViewById(R.id.textViewUsername)
        val visualizarTreinoButton: Button = itemView.findViewById(R.id.visualizarTreinoBtn)
        val profileImageView: ShapeableImageView = itemView.findViewById(R.id.profileListaImage)
    }
}
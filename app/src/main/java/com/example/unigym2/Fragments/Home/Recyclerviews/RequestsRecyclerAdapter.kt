package com.example.unigym2.Fragments.Home.Recyclerviews

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore

class RequestsRecyclerAdapter(private val requestsList: ArrayList<RequestsData>) : RecyclerView.Adapter<RequestsRecyclerAdapter.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.requests_recycler_layout,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return requestsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dataBase = FirebaseFirestore.getInstance()
        val currentItem = requestsList[position]

        currentItem.clienteID?.let { clienteID ->  dataBase.collection("Usuarios").document(clienteID)
            .get()
            .addOnSuccessListener { document ->
                if(document.exists()){
                    val nomeCliente = document.getString("name")
                    holder.nameView.text = nomeCliente
                }
            }
            .addOnFailureListener{
                holder.nameView.text = "Erro de acesso ao nome"
            }
        }
        holder.dataView.text = currentItem.data
        holder.serviceView.text = "Quer agendar um(a) \n${currentItem.servico.toString()}"
        holder.hourView.text = currentItem.hora


        holder.deleteView.setOnClickListener {

            currentItem.agendamentoID?.let { it1 -> dataBase.collection("Agendamentos").document(it1)
                .delete()
                .addOnSuccessListener {
                    requestsList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, requestsList.size)
                }.addOnFailureListener{
                    Log.e("DELETE", "Erro ao deletar ${it.message}")
                } }


        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var nameView : TextView = itemView.findViewById(R.id.userName)
        var hourView: TextView = itemView.findViewById(R.id.horarioValue)
        var dataView : TextView = itemView.findViewById(R.id.diaValue)
        var serviceView: TextView = itemView.findViewById(R.id.serviceView)
        val deleteView: Button = itemView.findViewById(R.id.removeRequestButton)
        val accept: Button = itemView.findViewById(R.id.acceptRequestBtn)
    }
}
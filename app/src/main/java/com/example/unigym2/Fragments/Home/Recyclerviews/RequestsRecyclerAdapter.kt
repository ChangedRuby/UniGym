package com.example.unigym2.Fragments.Home.Recyclerviews

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore

class RequestsRecyclerAdapter(private val requestsList: ArrayList<RequestsData>, val communicator: Communicator) : RecyclerView.Adapter<RequestsRecyclerAdapter.MyViewHolder>(){



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
        holder.profileView.setImageBitmap(currentItem.image)


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

        holder.accept.setOnClickListener{
            val agendamentoID =currentItem.agendamentoID
            if(agendamentoID!==null){
                dataBase.collection("Agendamentos")
                    .document(agendamentoID)
                    .update("status", "aceito")
                    .addOnSuccessListener {
                        showNotification("Solicitação aceita", "A sua solicitação foi aceita por ${communicator.getAuthUserName()}", holder.itemView.context)
                        requestsList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, requestsList.size)
                    }
                    .addOnFailureListener { e ->
                        Log.e("ACEITAR", "Erro ao aceitar solicitação")
                    }

            } else{
                Log.e("ACEITAR", "ID é nulo")
            }
        }

    }

    fun showNotification(title: String, message: String, applicationContext: Context) {
        val mNotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("210",
            "Solicitations",
            NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "Solicitações de agendamento"
        mNotificationManager.createNotificationChannel(channel)
        val mBuilder = NotificationCompat.Builder(applicationContext, "210")
            .setSmallIcon(R.drawable.app_icon) // notification icon
            .setContentTitle(title) // title for notification
            .setContentText(message)// message for notification
            .setAutoCancel(true) // clear notification after click
        mNotificationManager.notify(0, mBuilder.build())
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var nameView : TextView = itemView.findViewById(R.id.userName)
        var hourView: TextView = itemView.findViewById(R.id.horarioValue)
        var dataView : TextView = itemView.findViewById(R.id.diaValue)
        var serviceView: TextView = itemView.findViewById(R.id.serviceView)
        val deleteView: Button = itemView.findViewById(R.id.removeRequestButton)
        val profileView: ShapeableImageView = itemView.findViewById(R.id.imageProfileSolicitation)
        val accept: Button = itemView.findViewById(R.id.acceptRequestBtn)
    }
}
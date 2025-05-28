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
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext

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
            val agendamentoID = currentItem.agendamentoID
            if(agendamentoID !== null){
                dataBase.collection("Agendamentos")
                    .document(agendamentoID)
                    .update("status", "aceito")
                    .addOnSuccessListener {
                        handleRequestAcceptance(currentItem)
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

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var nameView : TextView = itemView.findViewById(R.id.userName)
        var hourView: TextView = itemView.findViewById(R.id.horarioValue)
        var dataView : TextView = itemView.findViewById(R.id.diaValue)
        var serviceView: TextView = itemView.findViewById(R.id.serviceView)
        val deleteView: Button = itemView.findViewById(R.id.removeRequestButton)
        val profileView: ShapeableImageView = itemView.findViewById(R.id.imageProfileSolicitation)
        val accept: Button = itemView.findViewById(R.id.acceptRequestBtn)
    }


    private fun handleRequestAcceptance(requestToAccept: RequestsData) {
        // Show some loading indicator if needed
        Log.d("ActivityLogic", "Handling acceptance for: ${requestToAccept.nomeCliente}")

        RequestActions.acceptRequestInDatabase(requestToAccept,
            onSuccess = {
                Log.i("ActivityLogic", "Request accepted in DB for ${requestToAccept.nomeCliente}. Notification should be triggered by backend.")
                // If you were NOT using database triggers and had a separate backend call for notifications:
                // RequestActions.triggerAcceptanceNotification(requestToAccept,
                //    onSuccess = { Log.i("ActivityLogic", "Notification triggered successfully.") },
                //    onFailure = { e -> Log.e("ActivityLogic", "Failed to trigger notification.", e) }
                // )

            },
            onFailure = { exception ->
                Log.e("ActivityLogic", "Failed to accept request for ${requestToAccept.nomeCliente}", exception)
            }
        )
    }
}

object RequestActions {

    private val db = FirebaseFirestore.getInstance()

    fun acceptRequestInDatabase(
        request: RequestsData, // Using your data class
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (request.agendamentoID == null) {
            Log.e("RequestActions", "Cannot accept request without an agendamentoID (request ID).")
            onFailure(IllegalArgumentException("Request ID is missing"))
            return
        }

        Log.d("RequestActions", "Attempting to accept request: ${request.agendamentoID}")
        db.collection("Agendamentos") // Assuming "Agendamentos" is your collection name
            .document(request.agendamentoID)
            .update("status", "aceito") // Or whatever field and value signifies acceptance
            .addOnSuccessListener {
                Log.i("RequestActions", "Request ${request.agendamentoID} successfully marked as accepted in Firestore.")
                // The backend (e.g., Cloud Function listening to this update)
                // is now responsible for sending the FCM notification.
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("RequestActions", "Error accepting request ${request.agendamentoID} in Firestore", e)
                onFailure(e)
            }
    }
}
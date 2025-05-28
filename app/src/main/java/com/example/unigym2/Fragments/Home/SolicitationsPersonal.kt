package com.example.unigym2.Fragments.Home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Home.Recyclerviews.RequestsData
import com.example.unigym2.Fragments.Home.Recyclerviews.RequestsRecyclerAdapter
import com.example.unigym2.Managers.AvatarManager
import com.example.unigym2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SolicitationsPersonal : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var db: FirebaseFirestore

    lateinit var schedulesBtn: Button
    lateinit var exitBtn : ImageView
    lateinit var semSolitacoeView: TextView

    private lateinit var communicator : Communicator

    private lateinit var adapter : RequestsRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var requestsArrayList: ArrayList<RequestsData>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var v = inflater.inflate(R.layout.fragment_solicitations_personal, container, false)
        semSolitacoeView = v.findViewById(R.id.semSolicitaçõesView)
        communicator = activity as Communicator
        semSolitacoeView.visibility = View.INVISIBLE

        dataInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = v.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = RequestsRecyclerAdapter(requestsArrayList, communicator)
        recyclerView.adapter = adapter
        exitBtn = v.findViewById(R.id.closeScreen)

        exitBtn.setOnClickListener {
            communicator.replaceFragment(HomePersonalTrainer())
        }


        return v
    }

    companion object {
        @JvmStatic fun newInstance(param1: String, param2: String) =
                SolicitationsPersonal().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    private fun dataInitialize(){

        requestsArrayList = arrayListOf<RequestsData>()

        db = FirebaseFirestore.getInstance()
        val personalIdAtual = FirebaseAuth.getInstance().currentUser?.uid
        var contador = 0
        db.collection("Agendamentos")
            .whereEqualTo("personalID", personalIdAtual)
            .whereEqualTo("status", "pendente")
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {


                    if(error != null){
                        Log.e("Firestore error", error.message.toString())
                        return
                    }



                    for(dc : DocumentChange in value?.documentChanges!!){
                        val document = dc.document

                        var clienteName: String ?= null

                        contador++


                        if(dc.type == DocumentChange.Type.ADDED){
                            db.collection("Usuarios").document(document.getString("clienteID").toString()).get().addOnSuccessListener { userDoc ->
                                clienteName = userDoc.get("name").toString()

                                AvatarManager.getUserAvatar(document.get("clienteID").toString(), document.get("clienteEmail").toString(), document.get("clienteNome").toString(), 40, lifecycleScope) { bitmap ->
                                    val agendamento = RequestsData(nomeCliente = clienteName, data = document.getString("data"), hora = document.getString("hora"),
                                        servico = document.getString("servico"), clienteID = document.getString("clienteID"), personalID = document.getString("personalID"),
                                        agendamentoID = document.id, image = bitmap)
                                    Log.d("schedules", "${agendamento.nomeCliente}")
                                    requestsArrayList.add(agendamento)
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                    if(contador<=0){
                        semSolitacoeView.visibility = View.VISIBLE
                    }

                }

            })


    }
}
package com.example.unigym2.Fragments.Home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [SolicitationsPersonal.newInstance] factory method to
 * create an instance of this fragment.
 */
class SolicitationsPersonal : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var db: FirebaseFirestore

    lateinit var schedulesBtn: Button
    lateinit var exitBtn : ImageView
    private lateinit var communicator : Communicator

    private lateinit var adapter : RequestsRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var requestsArrayList: ArrayList<RequestsData>

    lateinit var names: Array<String>
    lateinit var requests: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_solicitations_personal, container, false)


        dataInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = v.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = RequestsRecyclerAdapter(requestsArrayList)
        recyclerView.adapter = adapter
        communicator = activity as Communicator
        exitBtn = v.findViewById(R.id.closeScreen)

        exitBtn.setOnClickListener {
            communicator.replaceFragment(HomePersonalTrainer())
        }

        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SolicitationsPersonal.
         */
        // TODO: Rename and change types and number of parameters
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

                        if(dc.type == DocumentChange.Type.ADDED){
                            AvatarManager.getUserAvatar(document.get("clienteID").toString(), document.get("clienteEmail").toString(), document.get("clienteNome").toString(), 40, lifecycleScope) { bitmap ->
                                val agendamento = RequestsData(nomeCliente = document.getString("nomeCliente"), data = document.getString("data"), hora = document.getString("hora"),
                                    servico = document.getString("servico"), clienteID = document.getString("clienteID"), personalID = document.getString("personalID"),
                                    agendamentoID = document.id, image = bitmap)
                                requestsArrayList.add(agendamento)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }

                }

            })


    }
}
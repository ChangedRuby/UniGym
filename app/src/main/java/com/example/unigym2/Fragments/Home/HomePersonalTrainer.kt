package com.example.unigym2.Fragments.Home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Calendar.MonitoringSchedules
import com.example.unigym2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [HomePersonalTrainer.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomePersonalTrainer : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



    lateinit var schedulesBtn: Button
    lateinit var titleView: TextView
    lateinit var solicitationsView: TextView

    lateinit var db: FirebaseFirestore
    private lateinit var communicator: Communicator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_home_personal_trainer, container, false)
        solicitationsView = v.findViewById(R.id.solicitationsView)
        db = FirebaseFirestore.getInstance()

        communicator = activity as Communicator
        titleView = v.findViewById(R.id.nameTitle)
        schedulesBtn = v.findViewById(R.id.schedulesBtn)

        communicator.showLoadingOverlay()

        val auth = FirebaseAuth.getInstance()
        val personalID = auth.currentUser?.uid

        schedulesBtn.setOnClickListener{
            communicator.replaceFragment(SolicitationsPersonal())
            Log.d("personalLog", "Clicked")
            // this.parentFragmentManager.beginTransaction().replace(R.id.frame_layout, HomeUser()).commit()
        }
        db.collection("Agendamentos")
            .whereEqualTo("personalID", personalID )
            .whereEqualTo("status", "pendente")
            .get()
            .addOnSuccessListener { documents ->
                var solicitacoesPendentes = 0
                for(document in documents){
                    solicitacoesPendentes++
                }

                solicitationsView.text = "$solicitacoesPendentes solicitações pendentes!"

            }
            .addOnFailureListener {
                Log.e("Erro de valor", "Nao foi calculado a qtd")
            }

        db.collection("Usuarios").document(communicator.getAuthUser())
            .get()
            .addOnSuccessListener { result ->
                val userName = result.data?.get("name").toString()
                titleView.text = userName
                communicator.setAuthUserName(userName)
                communicator.hideLoadingOverlay()
                Log.d("firestore", "${result.id} => ${result.data}")
            }.addOnFailureListener { exception ->
                Log.w("firestore", "Error getting document.", exception)
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
         * @return A new instance of fragment fragment_home_personalTrainer.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomePersonalTrainer().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
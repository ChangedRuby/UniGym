package com.example.unigym2.Fragments.Profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Calendar.MonitoringSchedules
import com.example.unigym2.Fragments.Chat.ChatPersonal
import com.example.unigym2.Fragments.Chat.ChatUser
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VisualizarPerfilPersonal() : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var communicator : Communicator
    private lateinit var agendamentoTreinoBtn : TextView
    private lateinit var backBtn : ImageView
    private lateinit var nameTextView : TextView
    private lateinit var emailTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_visualizar_profile_personal, container, false)
        nameTextView = v.findViewById(R.id.UserProfileName)
        emailTextView = v.findViewById(R.id.userProfileEmail)
        communicator = activity as Communicator
        db = FirebaseFirestore.getInstance()
        agendamentoTreinoBtn = v.findViewById(R.id.agendarTreino)
        backBtn = v.findViewById(R.id.SairPersonal)

        agendamentoTreinoBtn.setOnClickListener {
            communicator.replaceFragment(MonitoringSchedules())
            Log.d("personalLog", "Clicked")
        }

        backBtn.setOnClickListener {
            communicator.replaceFragment(if (communicator.getMode()) ChatPersonal() else ChatUser())
        }

        parentFragmentManager.setFragmentResultListener("personal_info_key", viewLifecycleOwner) { _, bundle ->
            val personalID = bundle.getString("personal_id").toString()
            db.collection("Usuarios").document(personalID)
                .get()
                .addOnSuccessListener { result ->
                    nameTextView.text = result.data?.get("name").toString()
                    emailTextView.text = result.data?.get("email").toString()
                }.addOnFailureListener { exception ->
                    Log.d("firestore", "Error getting document.", exception)
                }

        }


        return v
    }

}
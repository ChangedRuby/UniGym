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
import com.example.unigym2.Fragments.Chat.ChatPersonal
import com.example.unigym2.Fragments.Chat.ChatUser
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VisualizarPerfilUser : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var nameTextView : TextView
    private lateinit var emailTextView: TextView
    private lateinit var communicator : Communicator
    private lateinit var backBtn : ImageView
    private lateinit var userID : String
    private lateinit var objetivo1 : TextView
    private lateinit var objetivo2 : TextView
    private lateinit var objetivo3 : TextView
    private lateinit var objetivo4 : TextView

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
        var v = inflater.inflate(R.layout.fragment_visualizar_profile_user, container, false)
        db = FirebaseFirestore.getInstance()
        communicator = activity as Communicator
        backBtn = v.findViewById(R.id.backButton)
        nameTextView = v.findViewById(R.id.UserProfileName)
        emailTextView = v.findViewById(R.id.userCREF)
        objetivo1 = v.findViewById(R.id.especialidade1)
        objetivo2 = v.findViewById(R.id.especialidade2)
        objetivo3 = v.findViewById(R.id.objetivo3)
        objetivo4 = v.findViewById(R.id.objetivo4)

        backBtn.setOnClickListener {
            communicator.replaceFragment(if (communicator.getMode()) ChatPersonal() else ChatUser())
        }

        parentFragmentManager.setFragmentResultListener("user_info_key", viewLifecycleOwner) { _, bundle ->
            userID = bundle.getString("user_id").toString()
            db.collection("Usuarios").document(userID)
                .get()
                .addOnSuccessListener { result ->
                    nameTextView.text = result.data?.get("name").toString()
                    emailTextView.text = result.data?.get("email").toString()
                    objetivo1.text = result.data?.get("objetivo1").toString()
                    objetivo2.text = result.data?.get("objetivo2").toString()
                    objetivo3.text = result.data?.get("objetivo3").toString()
                    objetivo4.text = result.data?.get("objetivo4").toString()
                }.addOnFailureListener { exception ->
                    Log.d("firestore", "Error getting document.", exception)
                }
        }


        return v
    }
}
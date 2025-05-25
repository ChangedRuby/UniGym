package com.example.unigym2.Fragments.Profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Chat.ChatPersonal
import com.example.unigym2.Fragments.Chat.ChatUser
import com.example.unigym2.Managers.AvatarManager
import com.example.unigym2.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VisualizarPerfilUser : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var db: FirebaseFirestore

    private lateinit var nameTextView : TextView
    private lateinit var emailTextView: TextView
    private lateinit var backBtn : ImageView
    private lateinit var userImage: ShapeableImageView

    private lateinit var communicator : Communicator

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
        userImage = v.findViewById(R.id.profileVisualizarUserImage)

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
                    val objetivos = result.data?.get("objetivos") as List<*>

                    for (i in 0 until objetivos.size) {
                        when (i) {
                            0 -> objetivo1.text = objetivos[i].toString()
                            1 -> objetivo2.text = objetivos[i].toString()
                            2 -> objetivo3.text = objetivos[i].toString()
                            3 -> objetivo4.text = objetivos[i].toString()
                        }
                    }
                    AvatarManager.getUserAvatar(userID, result.data?.get("email").toString(), result.data?.get("name").toString(), 40, lifecycleScope) { bitmap ->
                        userImage.setImageBitmap(bitmap)
                    }
                }.addOnFailureListener { exception ->
                    Log.d("firestore", "Error getting document.", exception)
                }
        }


        return v
    }
}
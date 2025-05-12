package com.example.unigym2.Fragments.Profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfilePersonal : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var db: FirebaseFirestore
    lateinit var nameTextView : TextView
    lateinit var emailTextView: TextView
    lateinit var crefTextView: TextView
    lateinit var editBtn : ImageView
    lateinit var exitBtn : ImageView
    lateinit var accessibilityBtn: TextView
    private lateinit var communicator : Communicator
    lateinit var especialidade1 : TextView
    lateinit var especialidade2 : TextView
    lateinit var especialidade3 : TextView
    lateinit var especialidade4 : TextView
    lateinit var servico1 : TextView
    lateinit var servico2 : TextView
    lateinit var servico3 : TextView
    lateinit var servico4 : TextView
    lateinit var preco1 : TextView
    lateinit var preco2 : TextView
    lateinit var preco3 : TextView
    lateinit var preco4 : TextView

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
        var v = inflater.inflate(R.layout.fragment_profile_personal, container, false)
        db = FirebaseFirestore.getInstance()
        communicator = activity as Communicator
        editBtn = v.findViewById(R.id.EditProfilePersonal)
        exitBtn = v.findViewById(R.id.SairPersonal)
        accessibilityBtn = v.findViewById(R.id.AcessibilidadePersonal)
        nameTextView = v.findViewById(R.id.UserProfileName)
        emailTextView = v.findViewById(R.id.userProfileEmail)
        crefTextView = v.findViewById(R.id.userCREF)
        especialidade1 = v.findViewById(R.id.especialidade1)
        especialidade2 = v.findViewById(R.id.especialidade2)
        especialidade3 = v.findViewById(R.id.especialidade3)
        especialidade4 = v.findViewById(R.id.especialidade4)
        servico1 = v.findViewById(R.id.servico1)
        servico2 = v.findViewById(R.id.servico2)
        servico3 = v.findViewById(R.id.servico3)
        servico4 = v.findViewById(R.id.servico4)
        preco1 = v.findViewById(R.id.precoServico1)
        preco2 = v.findViewById(R.id.precoServico2)
        preco3 = v.findViewById(R.id.precoServico3)
        preco4 = v.findViewById(R.id.precoServico4)
        db.collection("Usuarios").document(communicator.getAuthUser())
            .get()
            .addOnSuccessListener { result ->
                nameTextView.text = result.data?.get("name").toString()
                emailTextView.text = communicator.getAuthUserEmail()
                crefTextView.text = result.data?.get("CREF").toString()
                especialidade1.text = result.data?.get("specialty1").toString()
                especialidade2.text = result.data?.get("specialty2").toString()
                especialidade3.text = result.data?.get("specialty3").toString()
                especialidade4.text = result.data?.get("specialty4").toString()
                servico1.text = result.data?.get("service1").toString()
                servico2.text = result.data?.get("service2").toString()
                servico3.text = result.data?.get("service3").toString()
                servico4.text = result.data?.get("service4").toString()
                preco1.text = result.data?.get("servicePrice1").toString()
                preco2.text = result.data?.get("servicePrice2").toString()
                preco3.text = result.data?.get("servicePrice3").toString()
                preco4.text = result.data?.get("servicePrice4").toString()
                Log.d("firestore", "${result.id} => ${result.data}")
            }.addOnFailureListener { exception ->
                Log.w("firestore", "Error getting document.", exception)
            }
        editBtn.setOnClickListener {
            communicator.replaceFragment(EditProfilePersonal())
            Log.d("personalLog", "Clicked")
        }

        accessibilityBtn.setOnClickListener {
            val intent = Intent("android.settings.ACCESSIBILITY_SETTINGS")
            startActivity(intent)
            Log.d("userLog", "Opening Accessibility Settings")
        }

        exitBtn.setOnClickListener {
            Log.d("personalLog", "Clicked")
            requireActivity().supportFragmentManager.beginTransaction()
                .add(android.R.id.content, ProfileLogout())
                .addToBackStack(null)
                .commit()
        }

        return v
    }
}
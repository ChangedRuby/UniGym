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
    private lateinit var crefTextView: TextView
    private lateinit var specialty1 : TextView
    private lateinit var specialty2 : TextView
    private lateinit var specialty3 : TextView
    private lateinit var specialty4 : TextView
    private lateinit var service1 : TextView
    private lateinit var service2 : TextView
    private lateinit var service3 : TextView
    private lateinit var service4 : TextView
    private lateinit var price1 : TextView
    private lateinit var price2 : TextView
    private lateinit var price3 : TextView
    private lateinit var price4 : TextView

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
        crefTextView = v.findViewById(R.id.userCREF)
        specialty1 = v.findViewById(R.id.especialidade1)
        specialty2 = v.findViewById(R.id.especialidade2)
        specialty3 = v.findViewById(R.id.especialidade3)
        specialty4 = v.findViewById(R.id.especialidade4)
        service1 = v.findViewById(R.id.servico1)
        service2 = v.findViewById(R.id.servico2)
        service3 = v.findViewById(R.id.servico3)
        service4 = v.findViewById(R.id.servico4)
        price1 = v.findViewById(R.id.precoServico1)
        price2 = v.findViewById(R.id.precoServico2)
        price3 = v.findViewById(R.id.precoServico3)
        price4 = v.findViewById(R.id.precoServico4)
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
                    crefTextView.text = result.data?.get("CREF").toString()
                    specialty1.text = result.data?.get("specialty1").toString()
                    specialty2.text = result.data?.get("specialty2").toString()
                    specialty3.text = result.data?.get("specialty3").toString()
                    specialty4.text = result.data?.get("specialty4").toString()
                    service1.text = result.data?.get("service1").toString()
                    service2.text = result.data?.get("service2").toString()
                    service3.text = result.data?.get("service3").toString()
                    service4.text = result.data?.get("service4").toString()
                    price1.text = result.data?.get("servicePrice1").toString()
                    price2.text = result.data?.get("servicePrice2").toString()
                    price3.text = result.data?.get("servicePrice3").toString()
                    price4.text = result.data?.get("servicePrice4").toString()
                }.addOnFailureListener { exception ->
                    Log.d("firestore", "Error getting document.", exception)
                }

        }


        return v
    }

}
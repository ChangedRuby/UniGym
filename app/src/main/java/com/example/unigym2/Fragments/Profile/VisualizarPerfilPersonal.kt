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
import com.example.unigym2.Fragments.Calendar.MonitoringSchedules
import com.example.unigym2.Fragments.Chat.ChatPersonal
import com.example.unigym2.Fragments.Chat.ChatUser
import com.example.unigym2.Managers.AvatarManager
import com.example.unigym2.R
import com.google.android.material.imageview.ShapeableImageView
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
    private lateinit var personalImage: ShapeableImageView
    private lateinit var personalID: String
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
        personalImage = v.findViewById(R.id.profileVisualizarPersonalImage)

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
            /*val bundle = Bundle().apply {
                putString("personal_id", personalID)
            }
            parentFragmentManager.setFragmentResult("personal_info_key", bundle)*/
            communicator.replaceFragment(MonitoringSchedules())
            Log.d("personalLog", "Clicked")
        }

        backBtn.setOnClickListener {
            communicator.replaceFragment(if (communicator.getMode()) ChatPersonal() else ChatUser())
        }

        parentFragmentManager.setFragmentResultListener("personal_info_key", viewLifecycleOwner) { _, bundle ->
            personalID = bundle.getString("personal_id").toString()
            db.collection("Usuarios").document(personalID)
                .get()
                .addOnSuccessListener { result ->
                    nameTextView.text = result.data?.get("name").toString()
                    emailTextView.text = result.data?.get("email").toString()
                    crefTextView.text = result.data?.get("CREF").toString()
                    val specialties = result.data?.get("specialties") as List<*>
                    val services = result.data?.get("services") as List<*>
                    val prices = result.data?.get("servicePrices") as List<*>
                    for (i in 0 until specialties.size) {
                        when (i) {
                            0 -> specialty1.text = specialties[i].toString()
                            1 -> specialty2.text = specialties[i].toString()
                            2 -> specialty3.text = specialties[i].toString()
                            3 -> specialty4.text = specialties[i].toString()
                        }
                    }

                    for (i in 0 until services.size) {
                        when (i) {
                            0 -> service1.text = services[i].toString()
                            1 -> service2.text = services[i].toString()
                            2 -> service3.text = services[i].toString()
                            3 -> service4.text = services[i].toString()
                        }
                    }

                    for (i in 0 until prices.size) {
                        when (i) {
                            0 -> price1.text = prices[i].toString()
                            1 -> price2.text = prices[i].toString()
                            2 -> price3.text = prices[i].toString()
                            3 -> price4.text = prices[i].toString()
                        }
                    }

                    AvatarManager.getUserAvatar(personalID, result.data?.get("email").toString(), result.data?.get("name").toString(), 40, lifecycleScope) { bitmap ->
                        personalImage.setImageBitmap(bitmap)
                    }
                }.addOnFailureListener { exception ->
                    Log.d("firestore", "Error getting document.", exception)
                }

        }


        return v
    }

}
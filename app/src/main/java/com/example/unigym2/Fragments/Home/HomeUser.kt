package com.example.unigym2.Fragments.Home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Chat.Recyclerviews.ListaPersonaisItem
import com.example.unigym2.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [HomeUser.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeUser : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var titleView: TextView
    lateinit var communicator: Communicator
    lateinit var button: TextView

    lateinit var sessionTime : TextView
    lateinit var sessionPersonal : TextView

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        communicator.showLoadingOverlay()
        db.collection("Usuarios").document(communicator.getAuthUser())
            .get()
            .addOnSuccessListener { result ->
                val userName = result.data?.get("name").toString()
                titleView.text = userName
                communicator.setAuthUserName(userName)
                Log.d("firestore", "${result.id} => ${result.data}")
            }.addOnFailureListener { exception ->
                Log.w("firestore", "Error getting document.", exception)
            }

        db.collection("Agendamentos")
            .whereEqualTo("clienteID", communicator.getAuthUser())
            .whereEqualTo("status", "aceito")
            .get()
            .addOnSuccessListener { results ->
                val currentDate = java.util.Calendar.getInstance()
                val currentYear = currentDate.get(java.util.Calendar.YEAR)
                val currentMonth = currentDate.get(java.util.Calendar.MONTH) + 1 // Months are 0-based in Calendar
                val currentDay = currentDate.get(java.util.Calendar.DAY_OF_MONTH)
                val currentHour = currentDate.get(java.util.Calendar.HOUR_OF_DAY)
                val currentMinute = currentDate.get(java.util.Calendar.MINUTE)

                var closestSession : Map<String, Any>? = null
                var minTimeDifference = Long.MAX_VALUE

                for (result in results) {
                    val sessionData = result.data
                    val dateString = sessionData["data"] as? String
                    val timeString = sessionData["hora"] as? String

                    val dateParts = dateString?.split("/")
                    if (dateParts?.size != 3) continue

                    val day = dateParts[0].toIntOrNull() ?: continue
                    val month = dateParts[1].toIntOrNull() ?: continue
                    val year = dateParts[2].toIntOrNull() ?: continue

                    val timeParts = timeString?.split(":")
                    if (timeParts?.size != 2) continue

                    val hour = timeParts[0].toIntOrNull() ?: continue
                    val minute = timeParts[1].toIntOrNull() ?: continue
                    val sessionCalendar = java.util.Calendar.getInstance()
                    sessionCalendar.set(year, month - 1, day, hour, minute, 0)
                    if (sessionCalendar.timeInMillis <= currentDate.timeInMillis) continue

                    val timeDifference = sessionCalendar.timeInMillis - currentDate.timeInMillis

                    if (timeDifference < minTimeDifference) {
                        minTimeDifference = timeDifference
                        closestSession = sessionData
                    }

                    val personalId = closestSession?.get("personalID") as? String ?: ""
                    sessionTime.text = "${closestSession?.get("hora")}"

                    db.collection("Usuarios").document(personalId)
                        .get()
                        .addOnSuccessListener { personalResult ->
                            sessionPersonal.text = personalResult.data?.get("name").toString()
                        }.addOnFailureListener { exception ->
                            Log.w("firestore", "Error getting document.", exception)
                        }
                }

            }.addOnFailureListener { exception ->
                Log.w("firestore", "Error getting document.", exception)
            }
        communicator.hideLoadingOverlay()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_home_user, container, false)

        titleView = v.findViewById(R.id.nameTitle)
        sessionTime = v.findViewById(R.id.nextSessionTime)
        sessionPersonal = v.findViewById(R.id.nextSessionCostumer)
        button = v.findViewById(R.id.treinoFeitoButton)
        communicator = activity as Communicator

        communicator.showLoadingOverlay()

            button.setOnClickListener {
                db.collection("Usuarios").document(communicator.getAuthUser())
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val totalAtual = snapshot.getLong("totalTreinos")?:0
                        db.collection("Usuarios").document(communicator.getAuthUser())
                            .update("totalTreinos", totalAtual + 1)
                            .addOnSuccessListener {
                                button.visibility = View.GONE
                                Log.d("firestore", "Treino registrado com sucesso")
                            }
                            .addOnFailureListener {
                                Log.e( "firestore ", "Erro ao registrar treino", it)
                            }
                    }
                    .addOnFailureListener {
                        Log.e("firestore", "Erro ao obter total de treinos", it)
                    }
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
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeUser().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
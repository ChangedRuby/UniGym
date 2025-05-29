package com.example.unigym2.Fragments.Home

import android.icu.util.Calendar
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
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

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
    lateinit var sessionTime: TextView
    lateinit var sessionCostumer: TextView

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
        sessionTime = v.findViewById(R.id.nextSessionTime)
        sessionCostumer = v.findViewById(R.id.nextSessionCostumer)

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
                if(solicitacoesPendentes==0){
                    solicitationsView.text = "Nenhuma solicitação!"
                } else {
                    solicitationsView.text = "$solicitacoesPendentes solicitações pendentes!"
                }

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
                Log.d("firestore", "Collected data")
            }.addOnFailureListener { exception ->
                Log.w("firestore", "Error getting document.", exception)
            }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        communicator.showLoadingOverlay()

        // --- MODIFICAÇÃO PRINCIPAL PARA PRÓXIMO TREINO DO DIA ---
        // 3. Buscar o primeiro agendamento ACEITO do DIA ATUAL que ainda NÃO PASSOU

        val nowCalendar = Calendar.getInstance() // Hora atual

        val startOfTodayCalendar = Calendar.getInstance()
        startOfTodayCalendar.set(Calendar.HOUR_OF_DAY, 0)
        startOfTodayCalendar.set(Calendar.MINUTE, 0)
        startOfTodayCalendar.set(Calendar.SECOND, 0)
        startOfTodayCalendar.set(Calendar.MILLISECOND, 0)
        val startOfTodayMillis = startOfTodayCalendar.timeInMillis

        val endOfTodayCalendar = Calendar.getInstance()
        endOfTodayCalendar.set(Calendar.HOUR_OF_DAY, 23)
        endOfTodayCalendar.set(Calendar.MINUTE, 59)
        endOfTodayCalendar.set(Calendar.SECOND, 59)
        endOfTodayCalendar.set(Calendar.MILLISECOND, 999)
        val endOfTodayMillis = endOfTodayCalendar.timeInMillis

        db.collection("Agendamentos")
            .whereEqualTo("personalID", communicator.getAuthUser()) // Filtrar pelo ID do personal
            .whereEqualTo("status", "aceito")
            .whereGreaterThanOrEqualTo("timestamp", nowCalendar.timeInMillis) // Agendamentos futuros ou atuais no dia de hoje
            .whereLessThanOrEqualTo("timestamp", endOfTodayMillis) // Agendamentos até o fim do dia de hoje
            .orderBy("timestamp", Query.Direction.ASCENDING) // O mais próximo primeiro
            .limit(1) // Apenas o primeiro
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val firstSessionDocument = querySnapshot.documents[0]
                    val sessionTimestamp = firstSessionDocument.getLong("timestamp")

                    if (sessionTimestamp != null) {
                        val sessionCal = Calendar.getInstance()
                        sessionCal.timeInMillis = sessionTimestamp
                        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                        sessionTime.text = timeFormatter.format(sessionCal.time)
                    } else {
                        // Fallback se 'timestamp' não existir, mas houver 'hora'
                        sessionTime.text = firstSessionDocument.getString("hora") ?: "N/A"
                    }

                    val costumerID = firstSessionDocument.getString("clienteID") // Obter ID do cliente
                    if (!costumerID.isNullOrEmpty()) {
                        db.collection("Usuarios").document(costumerID)
                            .get()
                            .addOnSuccessListener { costumerResult ->
                                if (costumerResult.exists()) {
                                    sessionCostumer.text = costumerResult.getString("name") ?: "Cliente"
                                } else {
                                    sessionCostumer.text = "Cliente não encontrado"
                                }
                                sessionTime.visibility = View.VISIBLE
                                sessionCostumer.visibility = View.VISIBLE
                                communicator.hideLoadingOverlay() // Esconder overlay após TODAS as buscas principais
                            }.addOnFailureListener { exception ->
                                Log.w("firestore", "Erro ao buscar nome do cliente.", exception)
                                sessionCostumer.text = "Erro Cliente"
                                sessionTime.visibility = View.VISIBLE
                                sessionCostumer.visibility = View.VISIBLE
                                communicator.hideLoadingOverlay() // Esconder overlay
                            }
                    } else {
                        sessionCostumer.text = "Cliente não informado"
                        sessionTime.visibility = View.VISIBLE
                        sessionCostumer.visibility = View.VISIBLE
                        communicator.hideLoadingOverlay() // Esconder overlay
                    }
                } else {
                    // Nenhum agendamento encontrado para hoje
                    sessionTime.text = "Nenhum treino hoje"
                    sessionCostumer.text = ""
                    sessionTime.visibility = View.VISIBLE // Ou GONE, dependendo da preferência
                    sessionCostumer.visibility = View.VISIBLE // Ou GONE
                    communicator.hideLoadingOverlay() // Esconder overlay
                }
            }.addOnFailureListener { exception ->
                Log.w("firestore", "Erro ao buscar agendamentos do dia para o personal.", exception)
                sessionTime.text = "Erro ao carregar treinos"
                sessionCostumer.text = ""
                communicator.hideLoadingOverlay() // Esconder overlay
            }
        communicator.hideLoadingOverlay() // Esconder overlay APÓS a busca do agendamento
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
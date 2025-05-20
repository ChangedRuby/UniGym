package com.example.unigym2.Fragments.Calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Fragments.Calendar.Recyclerviews.CalendarPersonalAdapter
import com.example.unigym2.Fragments.Calendar.Recyclerviews.CalendarPersonalItem
import com.example.unigym2.Fragments.Calendar.Recyclerviews.CalendarUserAdapter
import com.example.unigym2.Fragments.Calendar.Recyclerviews.CalendarUserItem
import com.example.unigym2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarPersonal.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalendarPersonal : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: CalendarPersonalAdapter
    lateinit var schedulesArrayList: ArrayList<CalendarPersonalItem>

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
        val v = inflater.inflate(R.layout.fragment_calendar_personal, container, false)

        dataInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = v.findViewById(R.id.calendarPersonalRecyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = CalendarPersonalAdapter(schedulesArrayList)
        recyclerView.adapter = adapter

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarView = view.findViewById<CalendarView>(R.id.calendarView4)
        val programacoesContainer = view.findViewById<LinearLayout>(R.id.programacoes_container)

        val auth = FirebaseAuth.getInstance()
        var personalID = auth.currentUser?.uid
        val dataBase = FirebaseFirestore.getInstance()

        calendarView.setOnDateChangeListener{ _,year, month, dayOfMonth ->
            val dataSelecionada = "$dayOfMonth/${month + 1}/$year"

        programacoesContainer.visibility = View.VISIBLE
//            schedulesArrayList.clear()
//
//            dataBase.collection("Agendamentos")
//                .whereEqualTo("personalID", personalID)
//                .whereEqualTo("data", dataSelecionada)
//                .whereEqualTo("status", "aceito")
//                .get()
//                .addOnSuccessListener{ documents ->
//                    for(document in documents){
//                        val clienteID = document.getString("clienteID")
//                        val horario = document.getString("horario")
//                        val servico = document.getString("servico")
//
//                        dataBase.collection("Usuarios")
//                            .document(clienteID!!)
//                            .get()
//                            .addOnSuccessListener { result ->
//                                val nomeCliente = result.getString("name")
//
//                                val programacao = CalendarPersonalItem(
//                                    nomeCliente = nomeCliente,
//                                    horario = horario,
//                                    servico = servico
//                                )
//                                schedulesArrayList.add(programacao)
//                                adapter.notifyDataSetChanged()
//                            }
//                    }
//
//
//                }
//                .addOnFailureListener {  }


        /*programacoesConteudo.text = "Carregando Programação..."
            dataBase.collection("Agendamentos")
                .whereEqualTo("personalID", personalID)
                .whereEqualTo("data", dataSelecionada)
                .whereEqualTo("status", "aceito")
                .get()
                .addOnSuccessListener { documents ->
                    //val agendamento
                }*/
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CalendarPersonal.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalendarPersonal().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun dataInitialize(){
        schedulesArrayList = arrayListOf()
    }
}
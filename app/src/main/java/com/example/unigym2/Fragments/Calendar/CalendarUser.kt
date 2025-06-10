package com.example.unigym2.Fragments.Calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Calendar.Recyclerviews.CalendarPersonalItem
import com.example.unigym2.Fragments.Calendar.Recyclerviews.CalendarUserAdapter
import com.example.unigym2.Fragments.Calendar.Recyclerviews.CalendarUserItem
import com.example.unigym2.Fragments.Home.Recyclerviews.RequestsRecyclerAdapter
import com.example.unigym2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query;

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarUser.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalendarUser : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: CalendarUserAdapter
    lateinit var communicator: Communicator
    lateinit var schedulesArrayList: ArrayList<CalendarUserItem>


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
        val v = inflater.inflate(R.layout.fragment_calendar_user, container, false)

        schedulesArrayList = arrayListOf()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = v.findViewById(R.id.calendarUserRecyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = CalendarUserAdapter(schedulesArrayList)
        recyclerView.adapter = adapter

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        communicator = activity as Communicator
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val programacoesContainer = view.findViewById<LinearLayout>(R.id.programacoes_container)
        programacoesContainer.visibility = View.GONE
        var dataBase = FirebaseFirestore.getInstance()
        var auth = FirebaseAuth.getInstance()
        var userId = communicator.getAuthUser()

        calendarView.setOnDateChangeListener{_, year, month, dayOfMonth ->
            val dataSelecionada = String.format("%02d/%02d/%04d", dayOfMonth, month+1, year)
            Log.d("data_selecionada", dataSelecionada)

            programacoesContainer.visibility = View.VISIBLE
//dateTime
            dataBase.collection("Agendamentos")
                .whereEqualTo("clienteID", userId)
                .whereEqualTo("data", dataSelecionada)
                .whereEqualTo("status", "aceito")
                .get()
                .addOnSuccessListener{ documents ->
                    var cont =0
                    for (doc in documents){
                        cont = cont+1
                    }
                    if(cont<=0) {
                        val programacao = CalendarUserItem(
                            nomePersonal = "Sem agendamentos",
                            horario = "",
                            servico = ""
                        )
                        schedulesArrayList.clear()
                        schedulesArrayList.add(programacao)
                        adapter.notifyDataSetChanged()
                    } else {
                        schedulesArrayList.clear()
                        for (document in documents) {
                            val clienteID = document.getString("clienteID")
                            val personalID = document.getString("personalID")
                            val horario = document.getString("hora")
                            val timestamp = document.getLong("timestamp")
                            val servico = document.getString("servico")
                            Log.d("calendar_user", document.data.toString())

                            dataBase.collection("Usuarios")
                                .document(personalID!!)
                                .get()
                                .addOnSuccessListener { result ->
                                    val nomePersonal = result.getString("name")
                                    Log.d("calendar_user", timestamp.toString())

                                    val programacao = CalendarUserItem(
                                        nomePersonal = nomePersonal,
                                        horario = horario,
                                        timestamp = timestamp,
                                        servico = servico
                                    )
                                    schedulesArrayList.add(programacao)
                                    schedulesArrayList.sortWith( compareBy({ it.timestamp }) )
                                    Log.d("calendar_user", schedulesArrayList.toString())
                                    adapter.notifyDataSetChanged()
                                }
                        }
                        // schedulesArrayList.sortWith( compareBy({ it.timestamp }) )
                        adapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("data_selecionada", e.toString())
                }

            /*programacoesConteudo.text = when (dataSelecionada){
                "11/8/2025" -> "Reunião com cliente às 10h"
                else -> "Sem agendamentos"
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
         * @return A new instance of fragment fragment_calendar_user.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalendarUser().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun dataInitialize(){
        schedulesArrayList = arrayListOf()
    }

    fun bubbleSort(array: ArrayList<CalendarUserItem>): ArrayList<CalendarUserItem>{
        for (i in 0 until array.size - 1){
            for (j in 0 until array.size - i - 1){
                if (array[j].timestamp!! > array[j+1].timestamp!!){
                    val temp = array[j]
                    array[j] = array[j+1]
                    array[j+1] = temp
                }
            }
        }
        Log.d("calendar_user", array.toString())
        return array
    }
}
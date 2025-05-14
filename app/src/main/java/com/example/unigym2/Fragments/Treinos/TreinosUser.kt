package com.example.unigym2.Fragments.Treinos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Treinos.Recyclerviews.TreinoUserAdapter
import com.example.unigym2.Fragments.Treinos.Recyclerviews.TreinoUserItem
import com.example.unigym2.Fragments.Treinos.Recyclerviews.TreinoUserPersonalItem
import com.example.unigym2.R
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TreinosUser.newInstance] factory method to
 * create an instance of this fragment.
 */
class TreinosUser : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var treinosTab: TabLayout
    lateinit var userId: String
    lateinit var communicator: Communicator
    lateinit var db: FirebaseFirestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemsArrayA: ArrayList<TreinoUserItem>
    private lateinit var itemsArrayB: ArrayList<TreinoUserItem>

    private lateinit var repeticoesA: Array<String>
    private lateinit var repeticoesB: Array<String>



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
        var v = inflater.inflate(R.layout.fragment_treinos_user, container, false)

        treinosTab = v.findViewById(R.id.treinosUserTabLayout)
        communicator = activity as Communicator
        db = FirebaseFirestore.getInstance()
        userId = communicator.getAuthUser()

        treinosTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.text) {
                    "Treino A" -> {
                        changeAdapter(itemsArrayA)
                    }

                    "Treino B" -> {
                        changeAdapter(itemsArrayB)
                    }
                }

                Log.d("tab_selected", tab?.text.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.d("tab_selected", "Tab unselected: ${tab?.text.toString()}")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("tab_selected", "Tab reselected: ${tab?.text.toString()}")
            }

        })

        itemsArrayA = arrayListOf()
        itemsArrayB = arrayListOf()

        val layoutManager = LinearLayoutManager(context)
        recyclerView = v.findViewById(R.id.treinoUserRecyclerview)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        changeAdapter(itemsArrayB)
        createItems()

        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_treinos_user.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TreinosUser().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun changeAdapter(userItemArray: ArrayList<TreinoUserItem>){
        val adapter = TreinoUserAdapter(userItemArray)
        recyclerView.adapter = adapter
    }

    private fun createItems(){

        val userDoc = db.collection("Usuarios").document(userId)
        val treinoDoc = userDoc.collection("Treinos")

        treinoDoc.addSnapshotListener(object: EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {

                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return
                }

                itemsArrayA = arrayListOf()
                itemsArrayB = arrayListOf()

                for (dc: DocumentChange in value?.documentChanges!!) {
                    val document = dc.document
                    val docId = document.id

                    var exercicios = document.get("exercicios") as List<Map<String, Any>>
                    Log.d("treino_usuario_personal", exercicios.toString())

                    for (exercicio in exercicios) {
                        if (document.get("name").toString() == "A") {
                            itemsArrayA.add(
                                TreinoUserItem(
                                    series = exercicio.get("series").toString().toInt(),
                                    repeticoes = exercicio.get("repeticoes").toString().toInt(),
                                    maquina = exercicio.get("maquina").toString(),
                                    exercicio = exercicio.get("exercicio").toString(),
                                    userId = userId,
                                    treinoId = docId,
                                ))
                        } else {
                            itemsArrayB.add(
                                TreinoUserItem(
                                    series = exercicio.get("series").toString().toInt(),
                                    repeticoes = exercicio.get("repeticoes").toString().toInt(),
                                    maquina = exercicio.get("maquina").toString(),
                                    exercicio = exercicio.get("exercicio").toString(),
                                    userId = userId,
                                    treinoId = docId,
                                ))
                        }
                    }

                    if (dc.type == DocumentChange.Type.ADDED) {
                        Log.d("treino_usuario_personal", "Adicionado")

                    }
                }

                changeAdapter(itemsArrayA)
            }
        })

        /*repeticoesA = arrayOf(
            "1 x 5",
            "2 x 4",
            "3 x 3",
            "4 x 2",
            "5 x 1",
        )

        repeticoesB = arrayOf(
            "5 x 1",
            "4 x 2",
            "3 x 3",
            "2 x 4",
            "1 x 5",
        )


        for(i in repeticoesA.indices){

            val exercicios = TreinoUserItem(repeticoesA[i])
            repeticoesArrayA.add(exercicios)
        }

        for(i in repeticoesB.indices){

            val exercicios = TreinoUserItem(repeticoesB[i])
            repeticoesArrayB.add(exercicios)
        }*/
    }
}
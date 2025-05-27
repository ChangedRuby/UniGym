package com.example.unigym2.Fragments.Treinos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Treinos.Recyclerviews.MaquinaInnerItem
import com.example.unigym2.Fragments.Treinos.Recyclerviews.MaquinaOuterAdapter
import com.example.unigym2.Fragments.Treinos.Recyclerviews.MaquinaOuterItem
import com.example.unigym2.R
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.Locale // For Locale.ROOT

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TreinosMaquinas : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var addButton: Button
    private lateinit var communicator: Communicator

    private lateinit var adapter: MaquinaOuterAdapter
    private lateinit var outerRecyclerView: RecyclerView
    private lateinit var outerItemsArray: MutableList<MaquinaOuterItem>
    private lateinit var displayedOuterItemsArray: MutableList<MaquinaOuterItem>
    private lateinit var viewPool: RecyclerView.RecycledViewPool
    private lateinit var searchView: SearchView

    private lateinit var db: FirebaseFirestore


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
        val v = inflater.inflate(R.layout.fragment_treinos_maquinas, container, false)

        communicator = activity as Communicator
        db = FirebaseFirestore.getInstance()

        addButton = v.findViewById(R.id.AddMaquinaButton)
        addButton.setOnClickListener {
            communicator.replaceFragment(AdicionarMaquina())
        }

        searchView = v.findViewById(R.id.MaquinasSearchView)
        setupSearchView()

        parentFragmentManager.setFragmentResultListener(
            "maquina_adicionada_key",
            viewLifecycleOwner
        ) { _, bundle ->
            val foiAdicionada = bundle.getBoolean("maquina_adicionada", false)
            if (foiAdicionada) {
                val maquinaName = bundle.getString("maquina_name")
                val maquina = hashMapOf("title" to maquinaName)
                db.collection("Maquinas")
                    .add(maquina)
                    .addOnSuccessListener { documentReference ->
                        Log.d("treinosMaquinas", "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("treinosMaquinas", "Error adding document", e)
                    }
                Toast.makeText(requireContext(), "Máquina $maquinaName adicionada!", Toast.LENGTH_SHORT).show()
            }
        }

        parentFragmentManager.setFragmentResultListener(
            "exercicio_adicionado_key",
            viewLifecycleOwner
        ) { _, bundle ->
            val foiAdicionada = bundle.getBoolean("exercicio_adicionado", false)
            val exercicioName = bundle.getString("exercicio_name")
            if (foiAdicionada) {
                val documentId = bundle.getString("maquina_id")
                if (documentId != null && exercicioName != null) {
                    val document = db.collection("Maquinas").document(documentId)
                    document.update("exercises", FieldValue.arrayUnion(exercicioName))
                        .addOnSuccessListener {
                            Log.d("treinosMaquinas", "Exercise $exercicioName added to $documentId.")
                        }
                        .addOnFailureListener { e ->
                            Log.w("treinosMaquinas", "Error adding exercise to $documentId.", e)
                        }
                    Toast.makeText(requireContext(), "Exercício $exercicioName adicionado!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w("treinosMaquinas", "maquina_id or exercicio_name is null.")
                }
            }
        }

        val layoutManager = LinearLayoutManager(context)
        outerItemsArray = mutableListOf()
        displayedOuterItemsArray = mutableListOf()
        createOuterItems()

        viewPool = RecycledViewPool()

        outerRecyclerView = v.findViewById(R.id.maquinasOuterRecyclerview)
        outerRecyclerView.layoutManager = layoutManager
        outerRecyclerView.setRecycledViewPool(viewPool)
        adapter = MaquinaOuterAdapter(displayedOuterItemsArray, viewPool, communicator, parentFragmentManager)
        outerRecyclerView.adapter = adapter

        return v
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText.orEmpty())
                return true
            }
        })
        searchView.queryHint = "Buscar máquinas..."
    }

    private fun filter(query: String) {
        displayedOuterItemsArray.clear()
        val lowerCaseQuery = (query as java.lang.String).toLowerCase(Locale.ROOT)

        if (lowerCaseQuery.isEmpty()) {
            displayedOuterItemsArray.addAll(outerItemsArray)
        } else {
            for (item in outerItemsArray) {
                val itemTitleLowerCase = (item.title as java.lang.String?)?.toLowerCase(Locale.ROOT) ?: ""
                if (itemTitleLowerCase.contains(lowerCaseQuery)) {
                    displayedOuterItemsArray.add(item)
                }
            }
        }
        displayedOuterItemsArray.sortBy { (it.title as java.lang.String?)?.toLowerCase(Locale.ROOT) }
        adapter.notifyDataSetChanged()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TreinosMaquinas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun createOuterItems() {
        db.collection("Maquinas").orderBy("title", Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("Firestore error", error.message.toString())
                        return
                    }

                    if (value == null) {
                        Log.w("Firestore warning", "Snapshot listener returned null value.")
                        return
                    }


                    for (dc: DocumentChange in value.documentChanges) {
                        val document = dc.document
                        val docId = document.id
                        val title = document.getString("title") ?: "Título Indisponível"

                        val exercisesFromFirestore = document.get("exercises")
                        val currentInnerItems = mutableListOf<MaquinaInnerItem>()

                        if (exercisesFromFirestore is List<*>) {
                            exercisesFromFirestore.forEach { item ->
                                if (item is String) {
                                    currentInnerItems.add(MaquinaInnerItem(text = item, maquinaId = docId))
                                } else {
                                    Log.w("maquinas_data", "Exercise item is not a String: $item for document $docId")
                                }
                            }
                        } else if (exercisesFromFirestore != null) {
                            Log.w("maquinas_data", "'exercises' field is not a List for document $docId. Type: ${exercisesFromFirestore.javaClass.name}")
                        }

                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                val outerItem = MaquinaOuterItem(
                                    innerItems = currentInnerItems,
                                    title = title,
                                    id = docId,
                                )
                                if (outerItemsArray.none { it.id == docId }) {
                                    outerItemsArray.add(outerItem)
                                }
                            }
                            DocumentChange.Type.MODIFIED -> {
                                val index = outerItemsArray.indexOfFirst { it.id == docId }
                                if (index != -1) {
                                    outerItemsArray[index] = MaquinaOuterItem(
                                        innerItems = currentInnerItems,
                                        title = title,
                                        id = docId,
                                    )
                                    Log.d("maquinas_data", "Modified ${outerItemsArray[index].title}")
                                } else {
                                    val outerItem = MaquinaOuterItem(
                                        innerItems = currentInnerItems,
                                        title = title,
                                        id = docId,
                                    )
                                    outerItemsArray.add(outerItem)
                                    Log.w("maquinas_data", "Modified item with ID $docId not found, added instead.")
                                }
                            }
                            DocumentChange.Type.REMOVED -> {
                                outerItemsArray.removeAll { it.id == docId }
                            }
                        }
                    }
                    filter(searchView.query.toString())
                }
            })
    }
}
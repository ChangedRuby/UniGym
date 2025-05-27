package com.example.unigym2.Fragments.Treinos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Treinos.Recyclerviews.ListaTreinosAdapter
import com.example.unigym2.Fragments.Treinos.Recyclerviews.ListaTreinosItem
import com.example.unigym2.Fragments.Treinos.Recyclerviews.ListaUsuariosClickListener
import com.example.unigym2.Managers.AvatarManager
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ListaTreinosPersonal : Fragment(), ListaUsuariosClickListener {
    private var param1: String? = null
    private var param2: String? = null
    // lateinit var verTreinoBtn: Button
    private lateinit var adapter: ListaTreinosAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var communicator: Communicator

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView

    private lateinit var originalItemArray: ArrayList<ListaTreinosItem>
    private lateinit var displayedItemArray: ArrayList<ListaTreinosItem>

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
        val v = inflater.inflate(R.layout.fragment_treinos_lista_personal, container, false)

        recyclerView = v.findViewById(R.id.TreinosRecyclerview)
        searchView = v.findViewById(R.id.conversasSearchView)
        db = FirebaseFirestore.getInstance()
        communicator = activity as Communicator

        originalItemArray = arrayListOf()
        displayedItemArray = arrayListOf()

        setupSearchView()
        createItems()

        val layoutManager = LinearLayoutManager(context)
        adapter = ListaTreinosAdapter(displayedItemArray, this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

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
        searchView.queryHint = "Buscar alunos..."
    }

    private fun filter(query: String) {
        displayedItemArray.clear()
        val lowerCaseQuery = (query as java.lang.String).toLowerCase(Locale.ROOT)

        if (lowerCaseQuery.isEmpty()) {
            displayedItemArray.addAll(originalItemArray)
        } else {
            for (item in originalItemArray) {
                val itemNameLowerCase = (item.name as java.lang.String?)?.toLowerCase(Locale.ROOT) ?: ""
                if (itemNameLowerCase.contains(lowerCaseQuery)) {
                    displayedItemArray.add(item)
                }
            }
        }
        displayedItemArray.sortBy { (it.name as java.lang.String?)?.toLowerCase(Locale.ROOT) }
        adapter.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListaTreinosPersonal().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun createItems() {
        val userCollection = db.collection("Usuarios")
        communicator.showLoadingOverlay()

        userCollection.whereEqualTo("isPersonal", false).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("ListaTreinosPersonal", "No users found with isPersonal == false.")
                    originalItemArray.clear()
                    filter(searchView.query.toString())
                    communicator.hideLoadingOverlay()
                    return@addOnSuccessListener
                }

                val tempItemsList = ArrayList<ListaTreinosItem>()
                var itemsToProcess = documents.size()
                var itemsProcessedCount = 0

                if (itemsToProcess == 0) {
                    originalItemArray.clear()
                    filter(searchView.query.toString())
                    communicator.hideLoadingOverlay()
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    val userName = document.getString("name")
                    val userDocId = document.id
                    val userEmail = document.getString("email")

                    if (userName.isNullOrEmpty() || userEmail.isNullOrEmpty()) {
                        Log.w("ListaTreinosPersonal", "User document ${document.id} has null or empty name/email. Skipping.")
                        itemsProcessedCount++
                        if (itemsProcessedCount == itemsToProcess) {
                            originalItemArray.clear()
                            originalItemArray.addAll(tempItemsList)
                            originalItemArray.sortBy { (it.name as java.lang.String?)?.toLowerCase(Locale.ROOT) }
                            filter(searchView.query.toString())
                            communicator.hideLoadingOverlay()
                        }
                        continue
                    }

                    AvatarManager.getUserAvatar(userDocId, userEmail, userName, 40, lifecycleScope) { bitmap ->
                        val newItem = ListaTreinosItem(name = userName, userId = userDocId, image = bitmap)
                        tempItemsList.add(newItem)
                        itemsProcessedCount++

                        if (itemsProcessedCount == itemsToProcess) {
                            originalItemArray.clear()
                            originalItemArray.addAll(tempItemsList)
                            originalItemArray.sortBy { (it.name as java.lang.String?)?.toLowerCase(Locale.ROOT) }
                            Log.d("ListaTreinosPersonal", "All users processed. OriginalItemArray size: ${originalItemArray.size}")
                            filter(searchView.query.toString())
                            communicator.hideLoadingOverlay()
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("ListaTreinosPersonal", "Error fetching users: ", e)
                communicator.hideLoadingOverlay()
            }
    }

    override fun onItemClick(listaTreinosItem: ListaTreinosItem) {
        Log.d("listaTreinosPersonal", "Recyclerview ${listaTreinosItem.name} clicked")
        parentFragmentManager.setFragmentResult("user_info_key", Bundle().apply {
            putString("name_user", listaTreinosItem.name)
            putString("id_user", listaTreinosItem.userId)
        })
        communicator.replaceFragment(TreinoUsuarioPersonal())
    }
}
package com.example.unigym2.Fragments.Chat

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Chat.Recyclerviews.ListaPersonaisAdapter
import com.example.unigym2.Fragments.Chat.Recyclerviews.ListaPersonaisItem
import com.example.unigym2.Managers.AvatarManager
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChatUser : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var adapter: ListaPersonaisAdapter
    lateinit var communicator: Communicator
    private lateinit var recyclerView: RecyclerView
    lateinit var db: FirebaseFirestore
    private lateinit var searchView: SearchView

    private lateinit var originalItemArray: MutableList<ListaPersonaisItem>
    private lateinit var displayedItemArray: MutableList<ListaPersonaisItem>

    // private lateinit var itemArray: MutableList<ListaPersonaisItem>

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
        val v = inflater.inflate(R.layout.fragment_chat_user, container, false)
        db = FirebaseFirestore.getInstance()
        recyclerView = v.findViewById(R.id.listaUsuariosRecyclerview)
        communicator = activity as Communicator
        searchView = v.findViewById(R.id.conversasSearchView)

        originalItemArray = mutableListOf()
        displayedItemArray = mutableListOf()

        setupSearchView()
        createItems()

        val layoutManager = LinearLayoutManager(context)
        adapter = ListaPersonaisAdapter(displayedItemArray, communicator, parentFragmentManager)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        return v
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText.orEmpty())
                return true
            }
        })
    }

    private fun filter(query: String) {
        displayedItemArray.clear()
        val lowerCaseQuery = (query as java.lang.String).toLowerCase(Locale.ROOT)

        if (query.isEmpty()) {
            val brokItem = originalItemArray.find { it.userId == "BROK_AI_AGENT" }
            if (brokItem != null) {
                displayedItemArray.add(brokItem)
            } else {
                Log.w("ChatUser", "Brok item with ID 'BROK_AI_AGENT' not found in originalItemArray.")
            }

            for (item in originalItemArray) {
                if (item.userId != "BROK_AI_AGENT") {
                    displayedItemArray.add(item)
                }
            }
        } else {
            for (item in originalItemArray) {
                val itemNameLowerCase = (item.name as java.lang.String?)?.toLowerCase(Locale.ROOT) ?: ""
                if (itemNameLowerCase.contains(lowerCaseQuery)) {
                    displayedItemArray.add(item)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatUser().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun createItems() {
        communicator.showLoadingOverlay()
        val userCollection = db.collection("Usuarios")
        userCollection.whereEqualTo("isPersonal", true).get().addOnSuccessListener { documents ->
            originalItemArray.clear()

            val brokImageBitmap = context?.resources?.let { res ->
                BitmapFactory.decodeResource(res, R.drawable.brok_logo)
            } ?: run {
                Log.e("ChatUser", "Failed to decode brok_logo: context or resources were null.")
                null
            }

            if (brokImageBitmap != null) {
                originalItemArray.add(
                    ListaPersonaisItem(name = "Brok", userId = "BROK_AI_AGENT", image = brokImageBitmap))
            } else {
                Log.w("ChatUser", "Brok item not added due to missing image resource.")
            }

            if (documents.isEmpty()) {
                Handler(Looper.getMainLooper()).post {
                    filter("")
                }
                return@addOnSuccessListener
            }

            var itemsProcessed = 0
            val totalItemsToProcess = documents.size()
            val fetchedFirestoreItems = mutableListOf<ListaPersonaisItem>()

            for (document in documents) {
                AvatarManager.getUserAvatar(document.getString("id").orEmpty(), document.getString("email").orEmpty(), document.getString("name").orEmpty(), 40, lifecycleScope
                ) { bitmap ->
                    val newItem = ListaPersonaisItem(name = document.getString("name"), userId = document.getString("id"), image = bitmap)
                    fetchedFirestoreItems.add(newItem)

                    itemsProcessed++
                    if (itemsProcessed == totalItemsToProcess) { originalItemArray.addAll(fetchedFirestoreItems)
                        Handler(Looper.getMainLooper()).post {
                            filter("")
                        }
                    }
                }
            }
            communicator.hideLoadingOverlay()
        }.addOnFailureListener { exception ->
            Log.e("ChatUser", "Error getting documents: ", exception)
            Handler(Looper.getMainLooper()).post {
                filter("")
            }
        }
    }
}
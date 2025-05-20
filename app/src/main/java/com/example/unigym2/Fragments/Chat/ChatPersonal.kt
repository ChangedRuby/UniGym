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
import androidx.compose.ui.text.toLowerCase
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Chat.Recyclerviews.ListaPersonaisItem
import com.example.unigym2.Fragments.Chat.Recyclerviews.ListaUsuariosItem
import com.example.unigym2.Fragments.Chat.Recyclerviews.ListaUsuariosAdapter
import com.example.unigym2.Managers.AvatarManager
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatUser.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatPersonal : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var adapter: ListaUsuariosAdapter
    lateinit var communicator: Communicator
    private lateinit var recyclerView: RecyclerView
    lateinit var db: FirebaseFirestore
    private lateinit var searchView: SearchView

    private lateinit var originalItemArray: MutableList<ListaPersonaisItem>
    private lateinit var displayedItemArray: MutableList<ListaPersonaisItem>


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
        val v = inflater.inflate(R.layout.fragment_chat_trainer, container, false)
        db = FirebaseFirestore.getInstance()

        recyclerView = v.findViewById(R.id.listaUsuariosRecyclerview)
        communicator = activity as Communicator
        searchView = v.findViewById(R.id.conversasSearchView)


        originalItemArray = mutableListOf()
        displayedItemArray = mutableListOf()

        setupSearchView()
        createItems()

        val layoutManager = LinearLayoutManager(context)
        adapter = ListaUsuariosAdapter(displayedItemArray, communicator, parentFragmentManager)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

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
        val lowerCaseQuery = (query as java.lang.String).toLowerCase(Locale.ROOT) // Simplified toLowerCase

        if (query.isEmpty()) {
            displayedItemArray.addAll(originalItemArray)
        } else {
            for (item in originalItemArray) {
                val itemNameLowerCase = (item.name as java.lang.String?)?.toLowerCase(Locale.ROOT) ?: ""
                if (itemNameLowerCase.contains(lowerCaseQuery)) {
                    displayedItemArray.add(item)
                }
            }
        }
        //displayedItemArray.sortBy { (it.name as java.lang.String?)?.toLowerCase(Locale.ROOT) }
        adapter.notifyDataSetChanged()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatUser.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatUser().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun createItems(){
        val userCollection = db.collection("Usuarios")
        userCollection.whereEqualTo("isPersonal", false).get().addOnSuccessListener { documents ->
            originalItemArray.clear()

            val brokImageBitmap = context?.resources?.let {res ->
                BitmapFactory.decodeResource(res, R.drawable.brok_logo)
        } ?: run {
            Log.e("ChatPersonal", "Failed to decode brok_logo")
                null
            }

            if (brokImageBitmap != null) {
                originalItemArray.add(
                    ListaPersonaisItem(name = "Brok", userId = "BROK_AI_AGENT", image = brokImageBitmap)
                )
            } else {
                Log.w("ChatPersonal", "Brok item not added due to missing image resouce")
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
                ) {bitmap ->
                    val newItem = ListaPersonaisItem(name = document.getString("name"), userId = document.getString("id"), image = bitmap)
                    fetchedFirestoreItems.add(newItem)

                    itemsProcessed++
                    if (itemsProcessed == totalItemsToProcess) {originalItemArray.addAll(fetchedFirestoreItems)
                    Handler(Looper.getMainLooper()).post {
                        filter("")
                    }}
                }
            }
        }
    }
}
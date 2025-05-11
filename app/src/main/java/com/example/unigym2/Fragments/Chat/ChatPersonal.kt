package com.example.unigym2.Fragments.Chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Chat.Recyclerviews.ListaPersonaisAdapter
import com.example.unigym2.Fragments.Chat.Recyclerviews.ListaPersonaisItem
import com.example.unigym2.Fragments.Chat.Recyclerviews.ListaUsuariosAdapter
import com.example.unigym2.Fragments.Treinos.Recyclerviews.ListaTreinosItem
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore

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

    lateinit var communicator: Communicator
    private lateinit var recyclerView: RecyclerView
    lateinit var db: FirebaseFirestore
    lateinit var adapter: ListaUsuariosAdapter

    private lateinit var itemArray: MutableList<ListaPersonaisItem>

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
        var v = inflater.inflate(R.layout.fragment_chat_trainer, container, false)
        db = FirebaseFirestore.getInstance()

        recyclerView = v.findViewById(R.id.listaUsuariosRecyclerview)
        communicator = activity as Communicator

        createItems()
        val layoutManager = LinearLayoutManager(context)
        adapter = ListaUsuariosAdapter(itemArray, communicator, parentFragmentManager)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        return v
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
        itemArray = arrayListOf()
        val userCollection = db.collection("Usuarios")
        userCollection.whereEqualTo("isPersonal", false).get().addOnSuccessListener { documents ->
            for(document in documents){
                itemArray.add(ListaPersonaisItem(name = document.get("name").toString(), userId = document.id))
                Log.d("ChatUser", "Item added: ${document.getString("name")}")
            }
            itemArray.add(0, ListaPersonaisItem(name = "Brok", userId = ""))
            adapter.notifyDataSetChanged()
        }
    }
}
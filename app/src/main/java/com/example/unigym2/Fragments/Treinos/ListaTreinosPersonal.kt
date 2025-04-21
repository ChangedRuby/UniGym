package com.example.unigym2.Fragments.Treinos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Treinos.Recyclerviews.ListaTreinosAdapter
import com.example.unigym2.Fragments.Treinos.Recyclerviews.ListaTreinosItem
import com.example.unigym2.Fragments.Treinos.Recyclerviews.ListaUsuariosClickListener
import com.example.unigym2.Fragments.Treinos.Recyclerviews.TreinoUserItem
import com.example.unigym2.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListaTreinosPersonal.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListaTreinosPersonal : Fragment(), ListaUsuariosClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var verTreinoBtn: Button
    private lateinit var communicator: Communicator

    private lateinit var recyclerView: RecyclerView

    private lateinit var namesArray: Array<String>
    private lateinit var itemArray: ArrayList<ListaTreinosItem>

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
        var v = inflater.inflate(R.layout.fragment_treinos_lista_personal, container, false)

        recyclerView = v.findViewById(R.id.TreinosRecyclerview)
        communicator = activity as Communicator

        createItems()
        val layoutManager = LinearLayoutManager(context)
        val adapter = ListaTreinosAdapter(itemArray, this)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
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
         * @return A new instance of fragment FazerTreino.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListaTreinosPersonal().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun createItems(){

        itemArray = arrayListOf()
        
        namesArray = arrayOf(
            "Name A",
            "Name B",
            "Name C",
            "Name D",
            "Name E",
        )

        for(i in namesArray.indices){
            val nameItem = ListaTreinosItem(namesArray[i])
            itemArray.add(nameItem)
        }
    }

    override fun onItemClick(listaTreinosItem: ListaTreinosItem) {
        Log.d("listaTreinosPersonal", "Recyclerview ${listaTreinosItem.name} clicked")
        parentFragmentManager.setFragmentResult("user_info_key", Bundle().apply {
            putString("name_user", listaTreinosItem.name)
        })
        communicator.replaceFragment(TreinoUsuarioPersonal())
    }
}
package com.example.unigym2.Fragments.Treinos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Fragments.Home.RequestsData
import com.example.unigym2.Fragments.Home.RequestsRecyclerAdapter
import com.example.unigym2.R

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

    private lateinit var adapter : TreinoUserAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var repeticoesArray: ArrayList<TreinoUserItem>

    private lateinit var repeticoes: Array<String>



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

        createItems()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = v.findViewById(R.id.treinoUserRecyclerview)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = TreinoUserAdapter(repeticoesArray)
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

    private fun createItems(){

        repeticoesArray = arrayListOf()

        repeticoes = arrayOf(
            "1 x 5",
            "2 x 4",
            "3 x 3",
            "4 x 2",
            "5 x 1",
        )


        for(i in repeticoes.indices){

            val exercicios = TreinoUserItem(repeticoes[i])
            repeticoesArray.add(exercicios)
        }
    }
}
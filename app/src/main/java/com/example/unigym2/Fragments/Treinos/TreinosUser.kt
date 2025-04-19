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

    lateinit var radioGroupBtn: RadioGroup

    private lateinit var recyclerView: RecyclerView
    private lateinit var repeticoesArrayA: ArrayList<TreinoUserItem>
    private lateinit var repeticoesArrayB: ArrayList<TreinoUserItem>

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

        radioGroupBtn = v.findViewById(R.id.treinoToggle)

        radioGroupBtn.setOnCheckedChangeListener { radioGroup, checkedID ->
            if(checkedID != -1){
                val checkedRadioBtn = radioGroup.findViewById<RadioButton>(checkedID)

                if(checkedRadioBtn != null){
                    when(checkedID){
                        R.id.treinoButtonA -> {
                            changeAdapter(repeticoesArrayA)
                            Log.d("TreinoUser", "ButtonA click")
                        }

                        R.id.treinoButtonB -> {
                            changeAdapter(repeticoesArrayB)
                            Log.d("TreinoUser", "ButtonB click")
                        }

                        else -> {
                            Log.d("TreinoUser", "Button not found")
                        }
                    }
                }
            }
        }

        createItems()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = v.findViewById(R.id.treinoUserRecyclerview)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        changeAdapter(repeticoesArrayB)

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

        repeticoesArrayA = arrayListOf()
        repeticoesArrayB = arrayListOf()

        repeticoesA = arrayOf(
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
        }
    }
}
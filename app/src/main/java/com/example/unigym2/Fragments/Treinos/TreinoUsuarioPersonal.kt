package com.example.unigym2.Fragments.Treinos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Treinos.Recyclerviews.TreinoUserAdapter
import com.example.unigym2.Fragments.Treinos.Recyclerviews.TreinoUserItem
import com.example.unigym2.Fragments.Treinos.Recyclerviews.TreinoUserPersonalAdapter
import com.example.unigym2.Fragments.Treinos.Recyclerviews.TreinoUserPersonalItem
import com.example.unigym2.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var communicator: Communicator
/**
 * A simple [Fragment] subclass.
 * Use the [TreinoUsuarioPersonal.newInstance] factory method to
 * create an instance of this fragment.
 */
class TreinoUsuarioPersonal : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    lateinit var nameTextView: TextView
    lateinit var nameUser: String
    lateinit var userId: String

    private lateinit var itemsArrayA: ArrayList<TreinoUserPersonalItem>
    private lateinit var itemsArrayB: ArrayList<TreinoUserPersonalItem>

    private lateinit var repeticoesA: Array<String>
    private lateinit var repeticoesB: Array<String>

    lateinit var addTreinoBtn: Button

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
        var v = inflater.inflate(R.layout.fragment_treino_usuario_personal, container, false)

        nameTextView = v.findViewById(R.id.treinoUserName)
        communicator = activity as Communicator

        addTreinoBtn= v.findViewById(R.id.addTreinoBtn)
        addTreinoBtn.setOnClickListener{

            parentFragmentManager.setFragmentResult("user_info", Bundle().apply {
                putString("id_user", userId)
                putString("name_user", nameUser)
            })

            communicator.replaceFragment(AdicionarExercicioATreino())
        }

        createItems()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = v.findViewById(R.id.treinoUsuarioPersonalRecycler)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        changeAdapter(itemsArrayA)

        parentFragmentManager.setFragmentResultListener("user_info_key", viewLifecycleOwner) { _, bundle ->
            nameUser = bundle.getString("name_user").toString()
            userId = bundle.getString("id_user").toString()
            val exercicioAdicionado = bundle.getBoolean("exercicio_adicionado", false)

            nameTextView.text = "Treino de $nameUser"

            Log.d("treino_usuario_personal", "$nameUser: $userId")
            Toast.makeText(requireContext(), "Visualizando treino do usuário $nameUser .", Toast.LENGTH_SHORT).show()

            if(exercicioAdicionado){
                Toast.makeText(requireContext(), "Exercicio foi adicionado .", Toast.LENGTH_SHORT).show()
            }
        }
        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VerTreinoPersonal.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TreinoUsuarioPersonal().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun changeAdapter(userItemArray: ArrayList<TreinoUserPersonalItem>){
        val adapter = TreinoUserPersonalAdapter(userItemArray)
        recyclerView.adapter = adapter
    }

    private fun createItems(){

        itemsArrayA = arrayListOf()
        itemsArrayB = arrayListOf()








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

            val exercicios = TreinoUserPersonalItem(0, 0, "", "")
            itemsArrayA.add(exercicios)
        }

        for(i in repeticoesB.indices){

            val exercicios = TreinoUserPersonalItem(0, 0, "", "")
            itemsArrayB.add(exercicios)
        }
    }
}
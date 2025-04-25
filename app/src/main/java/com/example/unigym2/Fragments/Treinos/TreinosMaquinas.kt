package com.example.unigym2.Fragments.Treinos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Home.Recyclerviews.RequestsData
import com.example.unigym2.Fragments.Treinos.Recyclerviews.MaquinaInnerItem
import com.example.unigym2.Fragments.Treinos.Recyclerviews.MaquinaOuterAdapter
import com.example.unigym2.Fragments.Treinos.Recyclerviews.MaquinaOuterItem
import com.example.unigym2.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TreinosMaquinas.newInstance] factory method to
 * create an instance of this fragment.
 */
class TreinosMaquinas : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var addButton: Button
    private lateinit var communicator: Communicator

    private lateinit var adapter : MaquinaOuterAdapter
    private lateinit var outerRecyclerView: RecyclerView
    lateinit var viewPool: RecyclerView.RecycledViewPool
    private lateinit var outerItems: ArrayList<RequestsData>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
//        val searchView = findViewById<SearchView>(R.id.MaquinasSearchView)
//        val searchPlate = searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
//        searchPlate.setBackgroundColor(Color.parseColor("#3372788C"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_treinos_maquinas, container, false)


        addButton = v.findViewById(R.id.AddMaquinaButton)
        addButton.setOnClickListener{
            communicator = activity as Communicator
            communicator.replaceFragment(AdicionarMaquina())

        }

        parentFragmentManager.setFragmentResultListener("maquina_adicionada_key", viewLifecycleOwner) { _, bundle ->
            val foiAdicionada = bundle.getBoolean("maquina_adicionada", false)
            if (foiAdicionada) {
                val maquinaName = bundle.getString("maquina_name")

                Toast.makeText(requireContext(), "Máquina $maquinaName adicionada!", Toast.LENGTH_SHORT).show()
            }
        }

        parentFragmentManager.setFragmentResultListener("exercicio_adicionado_key", viewLifecycleOwner) { _, bundle ->
            val foiAdicionada = bundle.getBoolean("exercicio_adicionado", false)
            if (foiAdicionada) {
                Toast.makeText(requireContext(), "Exercício adicionado!", Toast.LENGTH_SHORT).show()
            }
        }

        parentFragmentManager.setFragmentResultListener("treino_adicionado_key", viewLifecycleOwner) { _, bundle ->
            val foiAdicionada = bundle.getBoolean("treino_adicionado", false)
            if (foiAdicionada) {
                Toast.makeText(requireContext(), "Treino adicionado!", Toast.LENGTH_SHORT).show()
            }
        }



        // RECYCLER VIEW

        val maquinasNames = createOuterItems()
        val layoutManager = LinearLayoutManager(context)

        viewPool = RecycledViewPool()

        outerRecyclerView = v.findViewById(R.id.maquinasOuterRecyclerview)
        outerRecyclerView.layoutManager = layoutManager
        // outerRecyclerView.setHasFixedSize(true)
        outerRecyclerView.setRecycledViewPool(viewPool)
        adapter = MaquinaOuterAdapter(maquinasNames, viewPool)
        outerRecyclerView.adapter = adapter





        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment treinos_maquinas.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TreinosMaquinas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun createOuterItems(): List<MaquinaOuterItem> {
        // Generate sample data
        val innerItems1 = List(10) { MaquinaInnerItem("Inner Item $it") }
        val innerItems2 = List(10) { MaquinaInnerItem("Inner Item ${it + 10}") }
        val innerItems3 = List(10) { MaquinaInnerItem("Inner Item ${it + 20}") }
        return listOf(MaquinaOuterItem(innerItems1), MaquinaOuterItem(innerItems2), MaquinaOuterItem(innerItems3))
    }
}
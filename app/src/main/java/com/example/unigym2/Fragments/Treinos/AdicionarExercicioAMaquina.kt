package com.example.unigym2.Fragments.Treinos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.R
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdicionarExercicioAMaquina.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdicionarExercicioAMaquina : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var goBackBtn: ImageView
    lateinit var addBtn: TextView
    lateinit var maquinaTitle: TextView
    lateinit var editTextExercicioAMaquina: EditText
    lateinit var maquinaId: String
    private lateinit var communicator: Communicator

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
        var v = inflater.inflate(R.layout.fragment_adicionar_exercicio_a_maquina, container, false)
        goBackBtn = v.findViewById(R.id.goBackView)
        goBackBtn.setOnClickListener{
            communicator = activity as Communicator
            communicator.replaceFragment(TreinosMaquinas())
        }

        editTextExercicioAMaquina = v.findViewById(R.id.editTextExercicioAMaquina)
        maquinaTitle = v.findViewById(R.id.nomeTextView)

        addBtn = v.findViewById(R.id.addExercicioButton)
        addBtn.setOnClickListener{
            val exercicio = editTextExercicioAMaquina.text.toString().trim()
            if(exercicio.isNotEmpty()) {
                parentFragmentManager.setFragmentResult("exercicio_adicionado_key", Bundle().apply {
                    putBoolean("exercicio_adicionado", true)
                    putString("exercicio_name", editTextExercicioAMaquina.text.toString())
                    putString("maquina_id", maquinaId)
                })
                communicator = activity as Communicator
                communicator.replaceFragment(TreinosMaquinas())

            } else{
                Toast.makeText(requireContext(), "Preencha o campo de exercício", Toast.LENGTH_SHORT).show()
            }


            //Essa parte tem que ser modificada quando for trabalhar com banco de dados, por enquantp, ela so faz o efeito que irá acontecer ao adicionar

        }

        parentFragmentManager.setFragmentResultListener("maquina_info_key", viewLifecycleOwner) { _, bundle ->
            val name = bundle.getString("maquina_name")
            maquinaId = bundle.getString("maquina_id").toString()
            maquinaTitle.text = name

            Toast.makeText(requireContext(), "Visualizando maquina $name .", Toast.LENGTH_SHORT).show()
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
         * @return A new instance of fragment AdicionarExercicioAMaquina.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdicionarExercicioAMaquina().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
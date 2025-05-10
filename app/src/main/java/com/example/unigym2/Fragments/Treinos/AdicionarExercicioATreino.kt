package com.example.unigym2.Fragments.Treinos

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdicionarExercicioATreino : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var backBtn: ImageView
    lateinit var maquinasSpinner: Spinner
    lateinit var exerciciosSpinner: Spinner
    lateinit var seriesEditText: EditText
    lateinit var repeticoesEditText: EditText
    lateinit var addTreinoBtn: Button
    lateinit var maquinasNames: MutableList<String>
    lateinit var db: FirebaseFirestore
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
        val v = inflater.inflate(R.layout.fragment_adicionar_exercicio_a_treino, container, false)

        backBtn = v.findViewById(R.id.backBtn)
        backBtn.setOnClickListener {

            communicator = activity as Communicator
            communicator.replaceFragment(TreinoUsuarioPersonal())
        }

        maquinasSpinner = v.findViewById(R.id.maquinasSpinner)
        exerciciosSpinner = v.findViewById(R.id.exerciciosSpinner)
        db = FirebaseFirestore.getInstance()
        val collectionMaquinas = db.collection("Maquinas")

        maquinasNames = mutableListOf("Adicione uma máquina")

        resetExerciciosSpinner(mutableListOf())

        // pegar nome das maquina e setar no spinner
        collectionMaquinas.get().addOnSuccessListener { documents ->
            for(document in documents){
                var data = document.data

                val title = data.get("title").toString()

                maquinasNames.add(title)
                Log.d("adicionar_exercicio", "Maquina: $title")
            }
            Log.d("adicionar_exercicio", maquinasNames.toString())

            val adapter = object : ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
//            resources.getStringArray(R.array.maquinas_array)
                maquinasNames,
            ) {
                override fun isEnabled(position: Int): Boolean {
                    return position != 0
                }

                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    val textView = view as TextView
                    textView.setTextColor(if (position == 0) Color.LTGRAY else Color.WHITE)
                    return view
                }
            }

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            maquinasSpinner.adapter = adapter

            // função de mudar os exercicios no spinner de exercicio com base na maquina selecionada
            maquinasSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val selectedName = maquinasSpinner.selectedItem.toString()
                    collectionMaquinas.whereEqualTo("title", selectedName).get().addOnSuccessListener { maquinaDocument ->
                        for(document in maquinaDocument){
                            var exerciciosArray: MutableList<String>
                            exerciciosArray = document.data.get("exercises") as MutableList<String>
                            exerciciosArray.add(0, "Adicione um exercício")
                            resetExerciciosSpinner(exerciciosArray)
                        }
                    }.addOnFailureListener { exception ->
                        Log.d("adicionar_exercicio", "Erro eu pegar documento com nome $selectedName, $exception")

                    }
                    Log.d("adicionar_exercicio", "Maquina selecionada: $selectedName")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                    Log.d("adicionar_exercicio", "Nada selecionado no spinner de maquinas")
                }

            }

        }.addOnFailureListener { exception ->
            Log.d("adicionar_exercicio", "Erro ao pegar documentos da coleção maquinas $exception")
        }

        addTreinoBtn = v.findViewById(R.id.addTreino)
        seriesEditText = v.findViewById(R.id.editTextExercicioAMaquina)
        repeticoesEditText = v.findViewById(R.id.editTextRepeticoesExercicio)
        addTreinoBtn.setOnClickListener{


            addTreinoBtn.setOnClickListener {
                val maquinaSelecionada = maquinasSpinner.selectedItemPosition
                val exercicioSelecionado = exerciciosSpinner.selectedItemPosition
                val series = seriesEditText.text.toString().trim()
                val repeticoes = repeticoesEditText.text.toString().trim()

                if (maquinaSelecionada != 0 &&
                    exercicioSelecionado != 0 &&
                    series.isNotEmpty() &&
                    repeticoes.isNotEmpty()
                ) {
                    parentFragmentManager.setFragmentResult("treino_adicionado_key", Bundle().apply {
                        putBoolean("treino_adicionado", true)

                    })

                    communicator = activity as Communicator
                    communicator.replaceFragment(TreinoUsuarioPersonal())


                } else {
                    Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            }

        }

        return v
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdicionarExercicioATreino().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun resetExerciciosSpinner(exercises: MutableList<String>){

        val adapter2 = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            exercises,
        ){
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(if (position == 0) Color.LTGRAY else Color.WHITE)
                return view
            }
        }
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        exerciciosSpinner.adapter = adapter2
    }
}

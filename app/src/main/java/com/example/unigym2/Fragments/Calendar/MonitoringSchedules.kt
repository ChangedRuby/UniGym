package com.example.unigym2.Fragments.Calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Profile.VisualizarPerfilPersonal
import com.example.unigym2.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MonitoringSchedules.newInstance] factory method to
 * create an instance of this fragment.
 */
class MonitoringSchedules : Fragment() {
    lateinit var communicator: Communicator
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var personalID : String
    lateinit var personalName : String
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
        return inflater.inflate(R.layout.fragment_monitoring_schedules, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener("personal_monitoring_key", viewLifecycleOwner) { _, bundle ->
            personalID = bundle.getString("personal_id").toString()
            personalName = bundle.getString("personal_name").toString()
        }

        val calendarView = view.findViewById<CalendarView>(R.id.calendarView3)
        val blocoNovaSessao = view.findViewById<LinearLayout>(R.id.nova_sessao)
        val btnAgendar = view.findViewById<Button>(R.id.btnAgendar)
        val inputHora = view.findViewById<EditText>(R.id.inputHora)
        val inputMinuto = view.findViewById<EditText>(R.id.inputMinuto)

        val autoServico = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteServico)
        val servicos = arrayOf("Consulta", "Treino Personal ", "Massagem ", "Avaliação")
        val textIndisponivel = view.findViewById<android.widget.TextView>(R.id.textIndisponivel)

        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, servicos)
        autoServico.setAdapter(adapter)

        autoServico.setOnClickListener{
            autoServico.showDropDown()
        }

        val btnVoltar = view.findViewById<ImageButton>(R.id.btnVoltar)
        communicator = activity as Communicator
        btnVoltar.setOnClickListener {
            communicator.replaceFragment(VisualizarPerfilPersonal())
        }
            // talvez ajeitar essa botao de voltar

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            blocoNovaSessao.visibility = View.VISIBLE
        }

        btnAgendar.setOnClickListener {
            val hora = inputHora.text.toString()
            val minuto = inputMinuto.text.toString()
            val servicoSelecionado = autoServico.text.toString()

            if (hora == "12" && minuto == "00" && servicoSelecionado == "Personal Trainer"){
                textIndisponivel.visibility = View.VISIBLE
            }
            else {
                textIndisponivel.visibility = View.GONE
                Toast.makeText(requireContext(), "Sessão : $hora:$minuto - $servicoSelecionado", Toast.LENGTH_SHORT).show()

                communicator.replaceFragment(VisualizarPerfilPersonal())

            }

        }



    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MonitoringSchedules.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MonitoringSchedules().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
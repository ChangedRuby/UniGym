package com.example.unigym2.Fragments.Calendar

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Profile.VisualizarPerfilPersonal
import com.example.unigym2.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime
import java.util.Calendar


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
    private var dataSelecionada: String? = null
    private var dayOfMonthSelecionado: Int = -1
    private var monthSelecionado: Int = -1
    private var yearSelecionado: Int = -1
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var personalName : String
    lateinit var personalID: String
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

        val calendarView = view.findViewById<CalendarView>(R.id.calendarView3)
        val blocoNovaSessao = view.findViewById<LinearLayout>(R.id.nova_sessao)
        val btnAgendar = view.findViewById<Button>(R.id.btnAgendar)
        val inputHora = view.findViewById<EditText>(R.id.inputHora)
        val inputMinuto = view.findViewById<EditText>(R.id.inputMinuto)
        val textIndisponivel = view.findViewById<android.widget.TextView>(R.id.textIndisponivel)
        val spinnerServico = view.findViewById<Spinner>(R.id.spinner)

        val firestore = FirebaseFirestore.getInstance()

        /*parentFragmentManager.setFragmentResultListener("personal_info_key", viewLifecycleOwner) { _, bundle ->
            personalID = bundle.getString("personal_id").toString()
            Log.d("personal_id", "$personalID")

        }*/

        parentFragmentManager.setFragmentResultListener("personal_monitoring_key", viewLifecycleOwner) { _, bundle ->
            personalID = bundle.getString("personal_id").toString()
            personalName = bundle.getString("personal_name").toString()
            Log.d("personal_id", "$personalID")

            firestore.collection("Usuarios").document(personalID)
                .get()
                .addOnSuccessListener { document ->
                    val servicosList = document.get("services") as? ArrayList<*>
                    val servicosFiltrados = servicosList?.filterIsInstance<String>()?: emptyList()
                    val servicos = listOf("Selecione um serviço") + servicosFiltrados

                    val adapter = object: ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, servicos){
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
                    spinnerServico.adapter = adapter


                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Erro ao carregar serviços", Toast.LENGTH_SHORT)
                }
        }



        val btnVoltar = view.findViewById<ImageButton>(R.id.btnVoltar)
        communicator = activity as Communicator
        btnVoltar.setOnClickListener {
            val fragment = VisualizarPerfilPersonal()
            val bundle = Bundle().apply {
                putString("personal_id", personalID)
            }
            parentFragmentManager.setFragmentResult("personal_info_key", bundle)
            communicator.replaceFragment(VisualizarPerfilPersonal())
        }


        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            blocoNovaSessao.visibility = View.VISIBLE

            yearSelecionado = year
            monthSelecionado = month
            dayOfMonthSelecionado = dayOfMonth
            dataSelecionada = String.format("%02d/%02d/%04d", dayOfMonth, month+1, year)
        }

        btnAgendar.setOnClickListener {


            val horaString = inputHora.text.toString().trim()
            val minutoString = inputMinuto.text.toString().trim()
            val servicoSelecionado = spinnerServico.selectedItem?.toString()

            // Verificação se algum campo está vazio
            if (horaString.isEmpty() || minutoString.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha a hora e os minutos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hora = horaString.toIntOrNull()
            val minuto = minutoString.toIntOrNull()


            if (hora == null || hora !in 3..23) {
                Toast.makeText(requireContext(), "O horário deve ser válido!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (minuto == null || minuto !in 0..59) {
                Toast.makeText(requireContext(), "O horário deve ser válido!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dataSelecionada == null) {
                Toast.makeText(requireContext(), "Selecione uma data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (servicoSelecionado == "Selecione um serviço") {
                Toast.makeText(requireContext(), "Selecione um serviço!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val auth = FirebaseAuth.getInstance()
            val clienteID = auth.currentUser?.uid
            val horaFormatada = String.format("%02d:%02d", hora, minuto)



            firestore.collection("Agendamentos")
                .whereEqualTo("personalID", personalID)
                .whereEqualTo("data", dataSelecionada)
                .get()
                .addOnSuccessListener { result ->
                    var conflito = false
                    for(document in result){
                        val horaDoc = document.getString("hora")?:continue
                        val partes = horaDoc.split(":")

                        val h = partes[0].toInt()
                        val m = partes[1].toInt()
                        var minutosDoc = h * 60 + m
                        val horasEmMinutos = hora*60+minuto
                        if(kotlin.math.abs(horasEmMinutos - minutosDoc)<50){
                            conflito = true
                            break
                        }
                    }

                    if(conflito) Toast.makeText(requireContext(), "Horário indisponível!", Toast.LENGTH_SHORT).show()
                    else{
                        val calendar = Calendar.getInstance()
                        calendar.set(yearSelecionado, monthSelecionado, dayOfMonthSelecionado, hora, minuto)
                        val data = calendar.time
                        val timestamp = data.time
                        val agendamento = hashMapOf(
                            "clienteID" to clienteID,
                            "personalID" to personalID,
                            "data" to dataSelecionada,
                            "hora" to horaFormatada,
                            "timestamp" to timestamp,
                            "servico" to servicoSelecionado,
                            "status" to "pendente",
                            "notificado" to false
                        )
                        firestore.collection("Agendamentos")
                            .add(agendamento)
                            .addOnSuccessListener {
                                textIndisponivel.visibility = View.GONE
                                Toast.makeText(requireContext(), "Sessão: $horaFormatada - $servicoSelecionado solicitada ao personal!", Toast.LENGTH_SHORT).show()
                                val fragment = VisualizarPerfilPersonal()
                                val bundle = Bundle().apply {
                                    putString("personal_id", personalID)
                                }
                                parentFragmentManager.setFragmentResult("personal_info_key", bundle)
//                                requireActivity().supportFragmentManager.beginTransaction()
                                communicator.replaceFragment(VisualizarPerfilPersonal())
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Erro ao agendar. Tente novamente.", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener{
                    Toast.makeText(requireContext(), "Erro ao consultar disponibilidade", Toast.LENGTH_SHORT)
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
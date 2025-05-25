package com.example.unigym2.Fragments.Home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Chat.Recyclerviews.ListaPersonaisItem
import com.example.unigym2.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [HomeUser.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeUser : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var titleView: TextView
    lateinit var communicator: Communicator
    lateinit var button: TextView


    val db = FirebaseFirestore.getInstance()

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
        var v = inflater.inflate(R.layout.fragment_home_user, container, false)


        titleView = v.findViewById(R.id.nameTitle)
        button = v.findViewById(R.id.treinoFeitoButton)

        communicator = activity as Communicator

        db.collection("Usuarios").document(communicator.getAuthUser())
            .get()
            .addOnSuccessListener { result ->
                val userName = result.data?.get("name").toString()
                titleView.text = userName
                communicator.setAuthUserName(userName)
                Log.d("firestore", "${result.id} => ${result.data}")
            }.addOnFailureListener { exception ->
                Log.w("firestore", "Error getting document.", exception)
            }

            button.setOnClickListener {
                db.collection("Usuarios").document(communicator.getAuthUser())
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val totalAtual = snapshot.getLong("totalTreinos")?:0
                        db.collection("Usuarios").document(communicator.getAuthUser())
                            .update("totalTreinos", totalAtual + 1)
                            .addOnSuccessListener {
                                button.visibility = View.GONE
                                Log.d("firestore", "Treino registrado com sucesso")
                            }
                            .addOnFailureListener {
                                Log.e( "firestore ", "Erro ao registrar treino", it)
                            }
                    }
                    .addOnFailureListener {
                        Log.e("firestore", "Erro ao obter total de treinos", it)
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
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeUser().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
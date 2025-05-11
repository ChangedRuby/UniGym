package com.example.unigym2.Fragments.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Calendar.MonitoringSchedules
import com.example.unigym2.Fragments.Chat.ChatPersonal
import com.example.unigym2.Fragments.Chat.ChatUser
import com.example.unigym2.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VisualizarPerfilUser : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var communicator : Communicator
    private lateinit var backBtn : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_visualizar_profile_user, container, false)
        communicator = activity as Communicator
        backBtn = v.findViewById(R.id.backButton)

        backBtn.setOnClickListener {
            communicator.replaceFragment(if (communicator.getMode()) ChatPersonal() else ChatUser())
        }
        return v
    }
}
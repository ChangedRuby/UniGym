package com.example.unigym2.Fragments.Profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfilePersonal : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var editBtn : ImageView
    private lateinit var communicator : Communicator

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
        var v = inflater.inflate(R.layout.fragment_profile_personal, container, false)
        communicator = activity as Communicator
        editBtn = v.findViewById(R.id.EditProfilePersonal)
        editBtn.setOnClickListener {
            communicator.replaceFragment(EditProfilePersonal())
            Log.d("personalLog", "Clicked")
        }
        return v
    }
}
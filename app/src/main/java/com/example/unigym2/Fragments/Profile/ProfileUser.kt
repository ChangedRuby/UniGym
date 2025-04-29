package com.example.unigym2.Fragments.Profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileUser : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var userEditBtn : ImageView
    lateinit var exitBtn : ImageView
    lateinit var accessibilityBtn: TextView
    private lateinit var comunicator : Communicator

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
        var v = inflater.inflate(R.layout.fragment_profile_user, container, false)
        comunicator = activity as Communicator
        userEditBtn = v.findViewById(R.id.EditProfileUser)
        exitBtn = v.findViewById(R.id.ExitButton)
        accessibilityBtn = v.findViewById(R.id.AcessibilidadeUser)
        userEditBtn.setOnClickListener {
            comunicator.replaceFragment(EditProfileUser())
            Log.d("userLog", "Clicked")
        }

        accessibilityBtn.setOnClickListener {
            val intent = Intent("android.settings.ACCESSIBILITY_SETTINGS")
            startActivity(intent)
            Log.d("userLog", "Opening Accessibility Settings")
        }

        exitBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(android.R.id.content, ProfileLogout())
                .addToBackStack(null)
                .commit()
        }

        return v
    }
}
package com.example.unigym2.Fragments.Profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.LoginMenu
import com.example.unigym2.R

class ProfileLogout : Fragment() {

    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_logout, container, false)


        val btnConfirm = view.findViewById<TextView>(R.id.ButtonConfirma)
        val btnCancel = view.findViewById<TextView>(R.id.ButtonNega)

        btnConfirm.setOnClickListener {
            val intent = Intent(requireActivity(), LoginMenu::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btnCancel.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
        return view
    }
}
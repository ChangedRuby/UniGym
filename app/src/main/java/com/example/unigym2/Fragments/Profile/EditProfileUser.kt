package com.example.unigym2.Fragments.Profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.TypedValueCompat.ComplexDimensionUnit
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.R
import com.google.android.material.textfield.TextInputEditText

class EditProfileUser : Fragment() {
    private lateinit var comunicator : Communicator
    private lateinit var saveButton : TextView
    private lateinit var usernameEdit : TextInputEditText
    private lateinit var objetivo1 : TextInputEditText
    private lateinit var objetivo2 : TextInputEditText
    private lateinit var objetivo3 : TextInputEditText
    private lateinit var objetivo4 : TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile_user, container, false)

        comunicator = activity as Communicator

        usernameEdit = view.findViewById(R.id.editTextUsername)
        objetivo1 = view.findViewById(R.id.editObjetivo1)
        objetivo2 = view.findViewById(R.id.editObjetivo2)
        objetivo3 = view.findViewById(R.id.editObjetivo3)
        objetivo4 = view.findViewById(R.id.editObjetivo4)
        saveButton = view.findViewById(R.id.ConfirmarEditUser)
        saveButton.setOnClickListener {
            saveProfileChanges()
            comunicator.replaceFragment(ProfileUser())
            Log.d("userlog", "Profile Saved")
        }

        return view
    }

    private fun saveProfileChanges() {
        val username = usernameEdit.text.toString()
        val objetivo1 = objetivo1.text.toString()
        val objetivo2 = objetivo2.text.toString()
        val objetivo3 = objetivo3.text.toString()
        val objetivo4 = objetivo4.text.toString()
    }


}
package com.example.unigym2.Fragments.Profile

    import android.content.Intent
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.TextView
    import androidx.fragment.app.Fragment
    import com.example.unigym2.Activities.Communicator
    import com.example.unigym2.Activities.ResetPassword
    import com.example.unigym2.R
    import com.google.android.material.textfield.TextInputEditText

    class EditProfilePersonal : Fragment() {
        private lateinit var communicator: Communicator
        private lateinit var saveButton: TextView
        private lateinit var usernameEditText: TextInputEditText
        private lateinit var specialtyET1: TextInputEditText
        private lateinit var specialtyET2: TextInputEditText
        private lateinit var specialtyET3: TextInputEditText
        private lateinit var specialtyET4: TextInputEditText
        private lateinit var serviceNameET1: TextInputEditText
        private lateinit var serviceNameET2: TextInputEditText
        private lateinit var serviceNameET3: TextInputEditText
        private lateinit var serviceNameET4: TextInputEditText
        private lateinit var servicePriceET1: TextInputEditText
        private lateinit var servicePriceET2: TextInputEditText
        private lateinit var servicePriceET3: TextInputEditText
        private lateinit var servicePriceET4: TextInputEditText

        private lateinit var alterarSenha: TextView

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_edit_profile_personal, container, false)

            communicator = activity as Communicator

            usernameEditText = view.findViewById(R.id.editTextUsername)
            specialtyET1 = view.findViewById(R.id.editEspecialidade1)
            specialtyET2 = view.findViewById(R.id.editEspecialidade2)
            specialtyET3 = view.findViewById(R.id.editEspecialidade3)
            specialtyET4 = view.findViewById(R.id.editEspecialidade4)
            serviceNameET1 = view.findViewById(R.id.editText5)
            serviceNameET2 = view.findViewById(R.id.editText6)
            serviceNameET3 = view.findViewById(R.id.editText7)
            serviceNameET4 = view.findViewById(R.id.editText8)
            servicePriceET1 = view.findViewById(R.id.editText9)
            servicePriceET2 = view.findViewById(R.id.editText10)
            servicePriceET3 = view.findViewById(R.id.editText11)
            servicePriceET4 = view.findViewById(R.id.editText12)

            saveButton = view.findViewById(R.id.ConfirmarEditPersonal)
            saveButton.setOnClickListener {
                saveProfileChanges()
                communicator.replaceFragment(ProfilePersonal())
                Log.d("personalLog", "Profile Saved")
            }

            alterarSenha = view.findViewById(R.id.AlterarSenhaEditPersonal)
            alterarSenha.setOnClickListener {
                var intent = Intent(requireContext(), ResetPassword::class.java)
                startActivity(intent)
            }

            return view
        }

        private fun saveProfileChanges() {
            val username = usernameEditText.text.toString()
            val specialty1 = specialtyET1.text.toString()
            val specialty2 = specialtyET2.text.toString()
            val specialty3 = specialtyET3.text.toString()
            val specialty4 = specialtyET4.text.toString()
            val service1 = serviceNameET1.text.toString()
            val service2 = serviceNameET2.text.toString()
            val service3 = serviceNameET3.text.toString()
            val service4 = serviceNameET4.text.toString()
            val servicePrice1 = servicePriceET1.text.toString()
            val servicePrice2 = servicePriceET2.text.toString()
            val servicePrice3 = servicePriceET3.text.toString()
            val servicePrice4 = servicePriceET4.text.toString()

            // TODO: Implement saving data to your database or storage
            // For example: FirebaseDatabase.getInstance().getReference("users").child(userId).setValue(userProfile)
        }
    }
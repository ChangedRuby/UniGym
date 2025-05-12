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
    import com.google.firebase.firestore.FirebaseFirestore

class EditProfilePersonal : Fragment() {
        private lateinit var communicator: Communicator
        private lateinit var db: FirebaseFirestore
        private lateinit var saveButton: TextView
        private lateinit var usernameEditText: TextInputEditText
        private lateinit var userProfileEmail: TextView
        private lateinit var crefTextView: TextView
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

            db = FirebaseFirestore.getInstance()
            communicator = activity as Communicator

            usernameEditText = view.findViewById(R.id.editTextUsername)
            userProfileEmail = view.findViewById(R.id.userProfileEmail)
            crefTextView = view.findViewById(R.id.userCREF)
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
            db.collection("Usuarios").document(communicator.getAuthUser())
                .get()
                .addOnSuccessListener { result ->
                    usernameEditText.hint = result.data?.get("name").toString()
                    userProfileEmail.text = communicator.getAuthUserEmail()
                    crefTextView.text = result.data?.get("CREF").toString()
                    specialtyET1.hint = result.data?.get("specialty1").toString()
                    specialtyET2.hint = result.data?.get("specialty2").toString()
                    specialtyET3.hint = result.data?.get("specialty3").toString()
                    specialtyET4.hint = result.data?.get("specialty4").toString()
                    serviceNameET1.hint = result.data?.get("service1").toString()
                    serviceNameET2.hint = result.data?.get("service2").toString()
                    serviceNameET3.hint = result.data?.get("service3").toString()
                    serviceNameET4.hint = result.data?.get("service4").toString()
                    servicePriceET1.hint = result.data?.get("servicePrice1").toString()
                    servicePriceET2.hint = result.data?.get("servicePrice2").toString()
                    servicePriceET3.hint = result.data?.get("servicePrice3").toString()
                    servicePriceET4.hint = result.data?.get("servicePrice4").toString()
                    Log.d("firestore", "${result.id} => ${result.data}")
                }.addOnFailureListener { exception ->
                    Log.w("firestore", "Error getting document.", exception)
                }

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

            val userRef = db.collection("Usuarios").document(communicator.getAuthUser())
            val updates = hashMapOf<String, Any>()

            // Add username if not empty
            if (username.isNotEmpty()) {
                updates["name"] = username
            }

            // Add specialties if not empty
            if (specialty1.isNotEmpty()) updates["specialty1"] = specialty1
            if (specialty2.isNotEmpty()) updates["specialty2"] = specialty2
            if (specialty3.isNotEmpty()) updates["specialty3"] = specialty3
            if (specialty4.isNotEmpty()) updates["specialty4"] = specialty4

            // Add services and prices if not empty
            if (service1.isNotEmpty()) updates["service1"] = service1
            if (service2.isNotEmpty()) updates["service2"] = service2
            if (service3.isNotEmpty()) updates["service3"] = service3
            if (service4.isNotEmpty()) updates["service4"] = service4

            if (servicePrice1.isNotEmpty()) updates["servicePrice1"] = servicePrice1
            if (servicePrice2.isNotEmpty()) updates["servicePrice2"] = servicePrice2
            if (servicePrice3.isNotEmpty()) updates["servicePrice3"] = servicePrice3
            if (servicePrice4.isNotEmpty()) updates["servicePrice4"] = servicePrice4

            // Only update if there are changes to make
            if (updates.isNotEmpty()) {
                userRef.update(updates)
                    .addOnSuccessListener {
                        Log.d("firestore", "Personal trainer profile successfully updated!")
                    }
                    .addOnFailureListener { e ->
                        Log.w("firestore", "Error updating personal trainer profile", e)
                    }
            }
        }
    }
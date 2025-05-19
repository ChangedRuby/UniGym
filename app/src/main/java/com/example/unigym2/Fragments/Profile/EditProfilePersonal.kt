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
    import androidx.lifecycle.lifecycleScope
    import com.example.unigym2.Activities.Communicator
    import com.example.unigym2.Activities.ResetPassword
    import com.example.unigym2.Managers.AvatarManager
    import com.example.unigym2.R
    import com.google.android.material.imageview.ShapeableImageView
    import com.google.android.material.textfield.TextInputEditText
    import com.google.firebase.firestore.FieldValue
    import com.google.firebase.firestore.FirebaseFirestore
    import kotlin.reflect.typeOf

class EditProfilePersonal : Fragment() {
        private lateinit var communicator: Communicator
        private lateinit var db: FirebaseFirestore
        private lateinit var saveButton: TextView
        private lateinit var usernameEditText: TextInputEditText
        private lateinit var userProfileEmail: TextView
        private lateinit var crefTextView: TextView
        private lateinit var imageView: ShapeableImageView
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
            imageView = view.findViewById(R.id.profilePersonalEditImage)
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

                    val specialties = result.data?.get("specialties") as List<*>
                    val services = result.data?.get("services") as List<*>
                    val prices = result.data?.get("servicePrices") as List<*>
                    for (i in 0 until specialties.size) {
                        when (i) {
                            0 -> specialtyET1.hint = specialties[i].toString()
                            1 -> specialtyET2.hint = specialties[i].toString()
                            2 -> specialtyET3.hint = specialties[i].toString()
                            3 -> specialtyET4.hint = specialties[i].toString()
                        }
                    }

                    for (i in 0 until services.size) {
                        when (i) {
                            0 -> serviceNameET1.hint = services[i].toString()
                            1 -> serviceNameET2.hint = services[i].toString()
                            2 -> serviceNameET3.hint = services[i].toString()
                            3 -> serviceNameET4.hint = services[i].toString()
                        }
                    }

                    for (i in 0 until prices.size) {
                        when (i) {
                            0 -> servicePriceET1.hint = prices[i].toString()
                            1 -> servicePriceET2.hint = prices[i].toString()
                            2 -> servicePriceET3.hint = prices[i].toString()
                            3 -> servicePriceET4.hint = prices[i].toString()
                        }
                    }
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

            AvatarManager.getUserAvatar(communicator.getAuthUser(), communicator.getAuthUserEmail(), communicator.getAuthUser(), 80, lifecycleScope) { bitmap ->
                imageView.setImageBitmap(bitmap)
            }

            return view
        }

    private fun saveProfileChanges() {
        val username = usernameEditText.text.toString()
        val userRef = db.collection("Usuarios").document(communicator.getAuthUser())
        val updates = hashMapOf<String, Any>()

        if (username.isNotEmpty()) {
            updates["name"] = username
        }

        val specialties = ArrayList<String>()
        val services = ArrayList<String>()
        val prices = ArrayList<String>()

        specialties.add(if (specialtyET1.text.toString().isNotEmpty()) specialtyET1.text.toString() else specialtyET1.hint.toString())
        specialties.add(if (specialtyET2.text.toString().isNotEmpty()) specialtyET2.text.toString() else specialtyET2.hint.toString())
        specialties.add(if (specialtyET3.text.toString().isNotEmpty()) specialtyET3.text.toString() else specialtyET3.hint.toString())
        specialties.add(if (specialtyET4.text.toString().isNotEmpty()) specialtyET4.text.toString() else specialtyET4.hint.toString())

        services.add(if (serviceNameET1.text.toString().isNotEmpty()) serviceNameET1.text.toString() else serviceNameET1.hint.toString())
        services.add(if (serviceNameET2.text.toString().isNotEmpty()) serviceNameET2.text.toString() else serviceNameET2.hint.toString())
        services.add(if (serviceNameET3.text.toString().isNotEmpty()) serviceNameET3.text.toString() else serviceNameET3.hint.toString())
        services.add(if (serviceNameET4.text.toString().isNotEmpty()) serviceNameET4.text.toString() else serviceNameET4.hint.toString())

        prices.add(if (servicePriceET1.text.toString().isNotEmpty()) servicePriceET1.text.toString() else servicePriceET1.hint.toString())
        prices.add(if (servicePriceET2.text.toString().isNotEmpty()) servicePriceET2.text.toString() else servicePriceET2.hint.toString())
        prices.add(if (servicePriceET3.text.toString().isNotEmpty()) servicePriceET3.text.toString() else servicePriceET3.hint.toString())
        prices.add(if (servicePriceET4.text.toString().isNotEmpty()) servicePriceET4.text.toString() else servicePriceET4.hint.toString())

        updates["specialties"] = specialties
        updates["services"] = services
        updates["servicePrices"] = prices

        userRef.update(updates)
            .addOnSuccessListener {
                Log.d("firestore", "Personal trainer profile successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("firestore", "Error updating personal trainer profile", e)
            }
    }
}
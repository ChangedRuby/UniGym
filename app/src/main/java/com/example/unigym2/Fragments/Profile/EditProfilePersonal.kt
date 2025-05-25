package com.example.unigym2.Fragments.Profile

    import android.content.Intent
    import android.net.Uri
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.EditText
    import android.widget.TextView
    import android.widget.Toast
    import androidx.activity.result.ActivityResultLauncher
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.lifecycleScope
    import com.example.unigym2.Activities.Communicator
    import com.example.unigym2.Activities.LoginMenu
    import com.example.unigym2.Activities.ResetPassword
    import com.example.unigym2.Managers.AvatarManager
    import com.example.unigym2.R
    import com.google.android.material.imageview.ShapeableImageView
    import com.google.android.material.textfield.TextInputEditText
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FieldValue
    import com.google.firebase.firestore.FirebaseFirestore
    import kotlin.reflect.typeOf

class EditProfilePersonal : Fragment() {
        private lateinit var communicator: Communicator
        private lateinit var db: FirebaseFirestore
        private lateinit var auth : FirebaseAuth
        private lateinit var saveButton: TextView
        private lateinit var usernameEditText: TextInputEditText
        private lateinit var userProfileEmail: TextView
        private var inputTreinadorEmail: EditText ?= null
        private lateinit var crefTextView: TextView
        private lateinit var imagePersonal: ShapeableImageView
        private lateinit var imageConverted: String
        private lateinit var galleryLauncher: ActivityResultLauncher<String>
        private var changedImage: Boolean = false
        private lateinit var changeImageButton: Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                var imageUri: Uri?

                // uri to base 64
                imageUri = uri
                imageConverted = AvatarManager.uriToBase64(imageUri, 20, requireContext())
                changedImage = true
//            imageUser.setImageURI(imageUri)
                Log.d("userlog", "Image converted to base 64")

                // base 64 to bitmap
                imagePersonal.setImageBitmap(AvatarManager.base64ToBitmap(imageConverted))
            }
        }
    }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_edit_profile_personal, container, false)

            db = FirebaseFirestore.getInstance()
            auth = FirebaseAuth.getInstance()
            communicator = activity as Communicator

            usernameEditText = view.findViewById(R.id.editTextUsername)
            userProfileEmail = view.findViewById(R.id.userProfileEmail)
            crefTextView = view.findViewById(R.id.userCREF)
            imagePersonal = view.findViewById(R.id.profilePersonalEditImage)
            changeImageButton = view.findViewById(R.id.changePerfilPersonalImageBtn)
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
            inputTreinadorEmail = view.findViewById(R.id.editTextTextTreinadorEmailAddress)

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

            changeImageButton.setOnClickListener {

                galleryLauncher.launch("image/*")
            }

            AvatarManager.getUserAvatar(communicator.getAuthUser(), communicator.getAuthUserEmail(), communicator.getAuthUser(), 80, lifecycleScope) { bitmap ->
                imagePersonal.setImageBitmap(bitmap)
            }

            return view
        }

    private fun saveProfileChanges() {
        val username = usernameEditText.text.toString()
        val newEmail = inputTreinadorEmail?.text?.toString()?.trim().orEmpty()
        val currentEmail = communicator.getAuthUserEmail()
        val currentUser = communicator.getAuthInstance().currentUser

        val userRef = db.collection("Usuarios").document(communicator.getAuthUser())
        val updates = hashMapOf<String, Any>()

        if (username.isNotEmpty()) {
            updates["name"] = username
        }

        if(changedImage){
            AvatarManager.storeAvatarForUser(communicator.getAuthUser(), communicator.getAuthUserEmail(), imageConverted)
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


        if(newEmail.isNotEmpty() && newEmail != currentEmail){ // sharedpreferences e pedir armazenar o id do usuario
            currentUser?.verifyBeforeUpdateEmail(newEmail)                ?.addOnSuccessListener{
                Log.d("firestore", "Email NOVO NOVO NOVO atualizado com sucesso no Firestore.")

            }

//            @Suppress("DEPRECATION")
//            currentUser?.updateEmail(newEmail)
//                ?.addOnSuccessListener{
//                    userRef.update("email",newEmail)
//                        .addOnSuccessListener{
//                            Toast.makeText(requireContext(),"Email atualizado com sucesso!", Toast.LENGTH_SHORT).show()
//                            Log.d("firestore", "Email atualizado com sucesso no Firestore.")
//                        }
//                        .addOnFailureListener{ e->
//                            Log.w("firestore", "Erro ao atualizar email no Firestore", e)
//                            Toast.makeText(requireContext(), "Erro ao atualizar no banco de dados", Toast.LENGTH_LONG).show()
//                        }
//                            currentUser.sendEmailVerification()
//                            Log.d("firebaseAuth", "Email atualizado com sucesso.")
//                    Toast.makeText(requireContext(), "Verifique seu novo e-mail para continuar.", Toast.LENGTH_LONG).show()
//
//                }
//                ?.addOnFailureListener { e ->
//                    Log.e("firebaseAuth", "Erro ao atualizar o email. Tentando reautenticar.", e)
//
//                        val builder = android.app.AlertDialog.Builder(requireContext())
//                        builder.setTitle("Reautenticação necessária")
//                        builder.setMessage("Digite sua senha atual para confirmar a mudança de e-mail.")
//                      //  builder.setMessage("Aperte em confirmar para que haja a mudança de email")
//
//                        val input = android.widget.EditText(requireContext())
//                        input.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
//                        builder.setView(input)
//
//                    builder.setPositiveButton("Confirmar"){_,_ ->
//                        val password = input.text.toString().trim()
//
//                        if(password.isEmpty()){
//                            Toast.makeText(requireContext(), "Senha não pode estar vazia", Toast.LENGTH_SHORT).show()
//                            return@setPositiveButton
//                        }
//
//                        val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(currentEmail, password)
//
//                        currentUser.reauthenticate(credential)
//                            .addOnSuccessListener{
//                                Log.d("firebaseAuth", "Reautenticado com sucesso.")
//                                Toast.makeText(requireContext(), "Reautenticação bem-sucedida", Toast.LENGTH_SHORT).show()
//
//                                    @Suppress("DEPRECATION")
//                                    currentUser.updateEmail(newEmail)
//                                        .addOnSuccessListener{
//                                            userRef.update("email",newEmail)
//                                                .addOnSuccessListener{
//                                                    Log.d("firestore", "Email atualizado com sucesso no Firestore após reautenticação.")
//                                                    Toast.makeText(requireContext(), "Email alterado com sucesso!", Toast.LENGTH_SHORT).show()
//                                                }
//                                                .addOnFailureListener{ e2->
//                                                    Log.w("firestore", "Erro ao atualizar email no Firestore após reautenticação", e2)
//                                                    Toast.makeText(requireContext(), "Erro ao salvar email no banco de dados", Toast.LENGTH_LONG).show()
//                                                }
//                                            currentUser.sendEmailVerification()
//                                            Log.d("firebaseAuth", "Email atualizado com sucesso após reautenticação.")
//                                            Toast.makeText(requireContext(), "Verifique seu novo e-mail.", Toast.LENGTH_LONG).show()
//                                            FirebaseAuth.getInstance().signOut()
//                                            val intent = Intent(requireContext(), LoginMenu::class.java)
//                                            intent.putExtra("email_alterado",true)
//                                            startActivity(intent)
//
//                                        }
//
//                                        .addOnFailureListener{ e2 ->
//                                            Log.e("firebaseAuth", "Falha ao atualizar email após a reautenticação", e2)
//                                            Toast.makeText(requireContext(), "Falha ao alterar e-mail", Toast.LENGTH_LONG).show()
//                                        }
//                            }
//                            .addOnFailureListener{ e2 ->
//                                Log.e("firebaseAuth", "Erro ao reutenticar o usuário", e2)
//                                Toast.makeText(requireContext(), "Senha incorreta. Não foi possível reautenticar.", Toast.LENGTH_LONG).show()
//
//                            }
//                    }
//                            builder.setNegativeButton("Cancelar"){ dialog,_ -> dialog.cancel()}
//                            builder.show()
//                }






        }

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
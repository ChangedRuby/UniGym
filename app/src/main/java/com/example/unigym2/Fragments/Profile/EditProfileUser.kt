package com.example.unigym2.Fragments.Profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Activities.ResetPassword
import com.example.unigym2.R
import com.google.android.material.textfield.TextInputEditText
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.unigym2.Managers.AvatarManager
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class EditProfileUser : Fragment() {
    private lateinit var communicator : Communicator
    private lateinit var db: FirebaseFirestore
    private lateinit var saveButton : TextView
    private lateinit var usernameEdit : TextInputEditText
    private lateinit var userProfileEmail : TextView
    private var inputEmail : EditText ?=null
    private lateinit var changeImageButton: Button
    private lateinit var imageUser: ShapeableImageView
    private lateinit var objetivo1 : TextInputEditText
    private lateinit var objetivo2 : TextInputEditText
    private lateinit var objetivo3 : TextInputEditText
    private lateinit var objetivo4 : TextInputEditText
    private lateinit var imageConverted: String
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private lateinit var alterarSenha: TextView
    private var changedImage: Boolean = false

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
                imageUser.setImageBitmap(AvatarManager.base64ToBitmap(imageConverted))
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile_user, container, false)

        db = FirebaseFirestore.getInstance()
        communicator = activity as Communicator
        usernameEdit = view.findViewById(R.id.editTextUsername)
        userProfileEmail = view.findViewById(R.id.userProfileEmail)
        changeImageButton = view.findViewById(R.id.changeUserImageBtn)
        imageUser = view.findViewById(R.id.profileUserEditImage)
        objetivo1 = view.findViewById(R.id.editObjetivo1)
        objetivo2 = view.findViewById(R.id.editObjetivo2)
        objetivo3 = view.findViewById(R.id.editObjetivo3)
        objetivo4 = view.findViewById(R.id.editObjetivo4)
        inputEmail = view.findViewById(R.id.editTextTextEmailAddress)
        saveButton = view.findViewById(R.id.ConfirmarEditUser)
        saveButton.setOnClickListener {
            saveProfileChanges()
            communicator.replaceFragment(ProfileUser())
            Log.d("userlog", "Profile Saved")
        }

        AvatarManager.getUserAvatar(communicator.getAuthUser(), communicator.getAuthUserEmail(), communicator.getAuthUser(), 80, lifecycleScope) { bitmap ->
            imageUser.setImageBitmap(bitmap)
        }

        alterarSenha = view.findViewById(R.id.AlterarSenhaEditUser)
        alterarSenha.setOnClickListener {
            var intent = Intent(requireContext(), ResetPassword::class.java)
            startActivity(intent)
        }

        changeImageButton.setOnClickListener {

            galleryLauncher.launch("image/*")
        }

        db.collection("Usuarios").document(communicator.getAuthUser())
            .get()
            .addOnSuccessListener { result ->
                usernameEdit.hint = result.data?.get("name").toString()
                userProfileEmail.text = communicator.getAuthUserEmail()
                val objectives = result.data?.get("objetivos") as List<*>
                for (i in 0 until objectives.size) {
                    when (i) {
                        0 -> objetivo1.hint = objectives[i].toString()
                        1 -> objetivo2.hint = objectives[i].toString()
                        2 -> objetivo3.hint = objectives[i].toString()
                        3 -> objetivo4.hint = objectives[i].toString()
                    }
                }
                Log.d("firestore", "${result.id} => ${result.data}")
            }.addOnFailureListener { exception ->
                Log.w("firestore", "Error getting document.", exception)
            }

        return view
    }


    private fun saveProfileChanges() {
        val username = usernameEdit.text.toString()
        val obj1 = objetivo1.text.toString()
        val obj2 = objetivo2.text.toString()
        val obj3 = objetivo3.text.toString()
        val obj4 = objetivo4.text.toString()
        val newEmail = inputEmail?.text?.toString()?.trim().orEmpty()
        val currentEmail = communicator.getAuthUserEmail()
        val currentUser = communicator.getAuthInstance().currentUser


        val userRef = db.collection("Usuarios").document(communicator.getAuthUser())
        val updates = hashMapOf<String, Any>()

        if (username.isNotEmpty()) {
            updates["name"] = username
        }

        if(changedImage){
            AvatarManager.storeAvatarForUser(communicator.getAuthUser(), imageConverted)
        }

        val objectives = ArrayList<String>()

        objectives.add(if (obj1.isNotEmpty()) obj1 else objetivo1.hint.toString())
        objectives.add(if (obj2.isNotEmpty()) obj2 else objetivo2.hint.toString())
        objectives.add(if (obj3.isNotEmpty()) obj3 else objetivo3.hint.toString())
        objectives.add(if (obj4.isNotEmpty()) obj4 else objetivo4.hint.toString())

        if (newEmail.isNotEmpty() && newEmail != currentEmail){
            @Suppress("DEPRECATION")
            currentUser?.updateEmail(newEmail)
                ?.addOnSuccessListener{
                    userRef.update("email", newEmail)
                        .addOnSuccessListener{
                            Log.d("firestore", "Email atualizado com sucesso no Firestore.")
                        }
                        .addOnFailureListener{ e ->
                            Log.w("firestore", "Erro ao atualizar email no Firestore", e)
                        }
                    Log.d("firebaseAuth", "Email atualizado com sucesso.")

                }
                ?.addOnFailureListener { e ->
                    Log.e("firebaseAuth", "Erro ao atualizar o email. Tentando reautenticar.", e)

                    val builder = android.app.AlertDialog.Builder(requireContext())
                    builder.setTitle("Reautenticação necessária")
                    builder.setMessage("Aperte em confirmar para que haja a mudança de email")

                    val input = android.widget.EditText(requireContext())
                    input.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    builder.setView(input)

                    builder.setPositiveButton("Confirmar"){_,_ ->
                        val password = input.text.toString()
                        val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(currentEmail, password)

                        currentUser.reauthenticate(credential)
                            .addOnSuccessListener{
                                Log.d("firebaseAuth", "Reautenticado com sucesso.")

                                @Suppress("DEPRECATION")
                                currentUser.updateEmail(newEmail)
                                    .addOnSuccessListener{
                                        userRef.update("email",newEmail)
                                            .addOnSuccessListener{
                                                Log.d("firestore", "Email atualizado com sucesso no Firestore após reautenticação.")
                                            }
                                            .addOnFailureListener{ e2 ->
                                                Log.w("firestore", "Erro ao atualizar email no Firestore após reautenticação", e2)
                                            }

                                        Log.d("firebaseAuth", "Email atualizado com sucesso após reautenticação.")
                                    }
                                    .addOnFailureListener{ e2 ->
                                        Log.e("firebaseAuth", "Falha ao atualizar email após reautenticação", e2)
                                    }
                            }
                            .addOnFailureListener { e2 ->
                                Log.e("firebaseAuth", "Erro ao reautenticar usuário", e2)
                            }
                    }
                    builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
                    builder.show()
                }

        }
        updates["objetivos"] = objectives
        updates["email"] = if (newEmail.isNotEmpty()) newEmail else currentEmail

        if (updates.isNotEmpty()) {
            userRef.update(updates)
                .addOnSuccessListener {
                    Log.d("firestore", "User objectives successfully updated!")
                }
                .addOnFailureListener { e ->
                    Log.w("firestore", "Error updating user objectives", e)
                }
        }

        //verificação de email

   


}}
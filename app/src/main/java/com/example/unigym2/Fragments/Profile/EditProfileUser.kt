package com.example.unigym2.Fragments.Profile

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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.unigym2.Managers.AvatarManager
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class EditProfileUser : Fragment() {
    private lateinit var communicator : Communicator
    private lateinit var db: FirebaseFirestore
    private lateinit var saveButton : TextView
    private lateinit var usernameEdit : TextInputEditText
    private lateinit var userProfileEmail : TextView
    private lateinit var changeImageButton: Button
    private lateinit var imageUser: ShapeableImageView
    private lateinit var objetivo1 : TextInputEditText
    private lateinit var objetivo2 : TextInputEditText
    private lateinit var objetivo3 : TextInputEditText
    private lateinit var objetivo4 : TextInputEditText
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private lateinit var alterarSenha: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            var imageUri: Uri?

            // uri to base 64
            imageUri = uri
            var imageConverted = AvatarManager.uriToBase64(imageUri, 20, requireContext())
            AvatarManager.storeAvatarForUser(communicator.getAuthUser(), imageConverted)
//            imageUser.setImageURI(imageUri)
            Log.d("userlog", "Image converted to base 64")

            // base 64 to bitmap
            imageUser.setImageBitmap(AvatarManager.base64ToBitmap(imageConverted))
        }
    }

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
        saveButton = view.findViewById(R.id.ConfirmarEditUser)
        saveButton.setOnClickListener {
            saveProfileChanges()
            communicator.replaceFragment(ProfileUser())
            Log.d("userlog", "Profile Saved")
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

        val userRef = db.collection("Usuarios").document(communicator.getAuthUser())
        val updates = hashMapOf<String, Any>()

        if (username.isNotEmpty()) {
            updates["name"] = username
        }

        val objectives = ArrayList<String>()

        objectives.add(if (obj1.isNotEmpty()) obj1 else objetivo1.hint.toString())
        objectives.add(if (obj2.isNotEmpty()) obj2 else objetivo2.hint.toString())
        objectives.add(if (obj3.isNotEmpty()) obj3 else objetivo3.hint.toString())
        objectives.add(if (obj4.isNotEmpty()) obj4 else objetivo4.hint.toString())

        updates["objetivos"] = objectives

        if (updates.isNotEmpty()) {
            userRef.update(updates)
                .addOnSuccessListener {
                    Log.d("firestore", "User objectives successfully updated!")
                }
                .addOnFailureListener { e ->
                    Log.w("firestore", "Error updating user objectives", e)
                }
        }
    }


}
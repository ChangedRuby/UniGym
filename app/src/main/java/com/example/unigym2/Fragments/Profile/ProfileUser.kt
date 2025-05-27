package com.example.unigym2.Fragments.Profile

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Managers.AvatarManager
import com.example.unigym2.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileUser : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var db: FirebaseFirestore
    lateinit var userEditBtn : ImageView
    lateinit var exitBtn : ImageView
    lateinit var accessibilityBtn: TextView
    private lateinit var communicator : Communicator
    lateinit var nameTextView : TextView
    lateinit var emailTextView: TextView
    lateinit var objetivo1TextView: TextView
    lateinit var objetivo2TextView: TextView
    lateinit var objetivo3TextView: TextView
    lateinit var objetivo4TextView: TextView
    lateinit var deleteUserButton: ImageView
    lateinit var profileView: ShapeableImageView
    lateinit var profileBitmap: Bitmap
    private lateinit var quantidadeTreinos : TextView
    lateinit var auth: FirebaseAuth

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
        auth = FirebaseAuth.getInstance()
        communicator = activity as Communicator
        db = FirebaseFirestore.getInstance()
        userEditBtn = v.findViewById(R.id.EditProfileUser)
        exitBtn = v.findViewById(R.id.ExitButton)
        deleteUserButton = v.findViewById(R.id.deleteUserBtn)
        accessibilityBtn = v.findViewById(R.id.AcessibilidadeUser)
        nameTextView = v.findViewById(R.id.UserProfileName)
        emailTextView = v.findViewById(R.id.userCREF)
        objetivo1TextView = v.findViewById(R.id.especialidade1)
        objetivo2TextView = v.findViewById(R.id.especialidade2)
        objetivo3TextView = v.findViewById(R.id.objetivo3)
        objetivo4TextView = v.findViewById(R.id.objetivo4)
        quantidadeTreinos = v.findViewById(R.id.CountTreinos)
        profileView = v.findViewById(R.id.profileUserImage)
        db.collection("Usuarios").document(communicator.getAuthUser())
            .get()
            .addOnSuccessListener { result ->
                nameTextView.text = result.data?.get("name").toString()
                emailTextView.text = communicator.getAuthUserEmail()

                val totalTreinos = result.get("totalTreinos") ?: 0
                quantidadeTreinos.text = "$totalTreinos"

                val objetivos = result.data?.get("objectives") as List<*>

                for (i in 0 until objetivos.size) {
                    when (i) {
                        0 -> objetivo1TextView.text = objetivos[i].toString()
                        1 -> objetivo2TextView.text = objetivos[i].toString()
                        2 -> objetivo3TextView.text = objetivos[i].toString()
                        3 -> objetivo4TextView.text = objetivos[i].toString()
                    }
                }

                Log.d("firestore", "Collected data")
            }.addOnFailureListener { exception ->
                Log.w("firestore", "Error getting document.", exception)
            }
        userEditBtn.setOnClickListener {
            communicator.replaceFragment(EditProfileUser())
            Log.d("userLog", "Clicked")
        }

        accessibilityBtn.setOnClickListener {
            val intent = Intent("android.settings.ACCESSIBILITY_SETTINGS")
            startActivity(intent)
            Log.d("userLog", "Opening Accessibility Settings")
        }

        exitBtn.setOnClickListener {
            Log.d("userLog", "Clicked")
            requireActivity().supportFragmentManager.beginTransaction()
                .add(android.R.id.content, ProfileLogout())
                .addToBackStack(null)
                .commit()
        }

        deleteUserButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(android.R.id.content, ProfileConfirmDeleteAccount())
                .addToBackStack(null)
                .commit()
        }

        val apiUrl = "https://gravatar.com/avatar/56b7594a16c05252e9373288a16e7edb5bcd735e6c94b731a45c4cf35bb7be4c"

        /*lifecycleScope.launch(Dispatchers.IO) {
            val url: URL = URI.create(apiUrl).toURL()
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 10000
            connection.setRequestProperty("Authorization", "Bearer 4182:gk-4fL9c25ISRDpREuWqFIewKMrXBB4DaHcZGLVMwQnpaEKhsh6RXzGP2hvkZhcr")

            val responseCode: Int = connection.responseCode
            Log.d("user_profile", "Response Code: $responseCode")

            if(responseCode == 200){
                val inputStream = connection.inputStream
                val bufferedInputStream = BufferedInputStream(inputStream)
                val originalWidth = profileView.width
                val originalHeight = profileView.height
                Log.d("user_profile", "width; $originalWidth")
                Log.d("user_profile", "height; $originalHeight")

                profileBitmap = BitmapFactory.decodeStream(bufferedInputStream)
                profileBitmap.scale(originalWidth, originalHeight)

                Log.d("user_profile", "$profileBitmap")
            }
        }.invokeOnCompletion {

            lifecycleScope.launch(Dispatchers.Main) {
                profileView.setImageBitmap(profileBitmap)
            }
        }*/

        AvatarManager.getUserAvatar(communicator.getAuthUser(), communicator.getAuthUserEmail(), communicator.getAuthUserName(), 80, lifecycleScope){ bitmap ->
            profileView.setImageBitmap(bitmap)
        }

        return v
    }
}
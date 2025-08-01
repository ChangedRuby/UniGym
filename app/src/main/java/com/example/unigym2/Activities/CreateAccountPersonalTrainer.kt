package com.example.unigym2.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unigym2.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccountPersonalTrainer : AppCompatActivity() {

    lateinit var concludeButton: Button
    lateinit var emailTextInput: EditText
    lateinit var crefTextInput: EditText
    lateinit var nameTextInput: EditText
    lateinit var passwordTextInput: TextInputEditText
    lateinit var confirmPasswordTextInput : TextInputEditText
    lateinit var exitButton: ImageView
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account_personal_trainer)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        concludeButton = findViewById(R.id.concludePersonalAccountButton)

    }

    override fun onStart() {
        super.onStart()

        emailTextInput = findViewById(R.id.emailPersonalEditText)
        nameTextInput = findViewById(R.id.namePersonalEditText)
        crefTextInput = findViewById(R.id.CrefEditText)
        passwordTextInput = findViewById(R.id.passwordPersonalEditText)
        confirmPasswordTextInput = findViewById(R.id.confirmPasswordPersonalEditText)
        exitButton = findViewById(R.id.closeScreen3)

        exitButton.setOnClickListener {
            startActivity(Intent(this, CreateAccountUser::class.java))
        }

        concludeButton.setOnClickListener{
            val email = emailTextInput.text.toString()
            val password = passwordTextInput.text.toString()
            Log.d("create_account", email)
            Log.d("create_account", password)

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    Log.d("create_account", task.exception.toString())
                    if(auth.currentUser != null){
                        Log.d("create_account", "User is not null")
                    }
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("create_account", "createUserWithEmail:success")
                        val user = auth.currentUser
                        Log.d("create_account", "account created")

                        db.collection("Usuarios").document(user!!.uid).set(
                            hashMapOf(
                                "id" to user.uid,
                                "name" to nameTextInput.text.toString(),
                                "isPersonal" to true,
                                "CREF" to crefTextInput.text.toString(),
                                "email" to emailTextInput.text.toString(),
                                "specialties" to listOf(
                                    "",
                                    "",
                                    "",
                                    ""
                                ),
                                "services" to listOf(
                                    "",
                                    "",
                                    "",
                                    ""
                                ),
                                "servicePrices" to listOf(
                                    "",
                                    "",
                                    "",
                                    ""
                                ),
                            )
                        ).addOnSuccessListener { documentReference ->
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("login_activity", "signInWithEmail:success")
                                        val user = auth.currentUser

                                        var intent = Intent(this, MainActivity::class.java)
                                        intent.putExtra("userId", user!!.uid)
                                        intent.putExtra("userEmail", user.email)
                                        startActivity(intent)

                                    } else {
                                        Log.w("login_activity", "signInWithEmail:failure", task.exception)
                                        Toast.makeText(
                                            baseContext,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT,
                                        ).show()

                                    }
                                }

                            Log.d("create_account", "DocumentSnapshot added with ID: ${user.uid}")
                        }.addOnFailureListener { e ->
                            Log.w("create_account", "Error adding document", e)
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("create_account", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }
}
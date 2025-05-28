package com.example.unigym2.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unigym2.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccountUser : AppCompatActivity() {
    lateinit var createBtn: MaterialButton
    lateinit var personalBtn: TextView
    lateinit var emailTextInput: EditText
    lateinit var nameTextInput: TextInputEditText
    lateinit var passwordTextInput: EditText
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account_user)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        personalBtn = findViewById(R.id.createAccPersonal)
        createBtn = findViewById(R.id.concludePersonalAccountButton)
    }

    override fun onStart() {
        super.onStart()

        personalBtn.setOnClickListener {
            var intent = Intent(this, CreateAccountPersonalTrainer::class.java)

            startActivity(intent)
        }

        emailTextInput = findViewById(R.id.emailUserEditText)
        nameTextInput = findViewById(R.id.nameUserEditText)
        passwordTextInput = findViewById(R.id.passwordUserEditText)

        createBtn.setOnClickListener {

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
                        val userDoc = db.collection("Usuarios").document(user!!.uid)
                        Log.d("create_account", "account created")

                        userDoc.set(
                            hashMapOf(
                                "id" to user.uid,
                                "name" to nameTextInput.text.toString(),
                                "isPersonal" to false,
                                "email" to emailTextInput.text.toString(),
                                "avatar" to "",
                                "objectives" to listOf(
                                    "",
                                    "",
                                    "",
                                    ""
                                ),
                                "totalTreinos" to 0,
                                "treinosConsecutivos" to 0,
                                "treinosSemana" to 0,
                                "lastSessionReset" to System.currentTimeMillis(),
                                "lastWorkout" to System.currentTimeMillis()
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
                        val treinosCollection = userDoc.collection("Treinos")

                        treinosCollection.add(
                            hashMapOf(
                                "exercicios" to listOf(
                                    ""
                                ),
                                "name" to "A",
                            ),
                        )

                        treinosCollection.add(
                            hashMapOf(
                                "exercicios" to listOf(
                                    ""
                                ),
                                "name" to "B",
                            ),
                        )

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
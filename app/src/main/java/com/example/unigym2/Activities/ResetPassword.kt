package com.example.unigym2.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unigym2.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ResetPassword : AppCompatActivity() {

    lateinit var emailField : TextInputEditText
    lateinit var newPassawordField : TextInputEditText
    lateinit var newConfirmsPassawordField: TextInputEditText
    lateinit var newPasswordButton: Button
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        emailField = findViewById(R.id.emailEditText)
        newPassawordField = findViewById(R.id.passwordResetEditText)
        newConfirmsPassawordField = findViewById(R.id.confirmsPassawordResetEditText)
        newPasswordButton = findViewById(R.id.confirmChangesBtn)
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        newPasswordButton.setOnClickListener {
            val inputEmail = emailField.text.toString().trim()
            val newPassword = newPassawordField.text.toString().trim()
            val newConfirmsPassword = newConfirmsPassawordField.text.toString().trim()

            if(inputEmail.isEmpty() ||newPassword.isEmpty() || newConfirmsPassword.isEmpty()){
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(newPassword != newConfirmsPassword) {
                Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if( user != null){
                val userEmail = user.email
                if (userEmail == inputEmail) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Senha alterada com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, LoginMenu::class.java)
                                intent.putExtra("senha_alterada", true)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this, "Erroa ao alterar senha: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                Toast.makeText(this, "O e-mail informado não corresponde ao do usuário logado.", Toast.LENGTH_SHORT).show()
            }

            } else {
                Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
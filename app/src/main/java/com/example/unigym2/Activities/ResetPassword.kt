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
    lateinit var newPasswordButton: Button
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        emailField = findViewById(R.id.emailEditText)
        newPasswordButton = findViewById(R.id.confirmChangesBtn)
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        newPasswordButton.setOnClickListener {
            val email = emailField.text.toString().trim()

            if(email.isEmpty()){
                emailField.error = "Insira o e-mail"
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,"Link de redefinição de senha enviado para: $email",
                            Toast.LENGTH_LONG).show()

                        val intent = Intent(this, LoginMenu::class.java)
                        intent.putExtra("senha_alterdada", true)
                        startActivity(intent)
                        finish()
                    }
                        else{
                        Toast.makeText(this,"Erro ao enviar link: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                }

        }

    }
}
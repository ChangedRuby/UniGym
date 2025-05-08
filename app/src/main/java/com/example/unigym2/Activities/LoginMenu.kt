package com.example.unigym2.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unigym2.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginMenu : AppCompatActivity() {
    lateinit var entrarBtn: Button
    lateinit var entrarBtnPersonal: Button
    lateinit var createAccount: Button
    lateinit var recuperarSenhaBtn: Button
    lateinit var emailInput: TextInputEditText
    lateinit var passwordView: TextInputEditText
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_menu)

        entrarBtn = findViewById(R.id.entrarBtnUser)
        entrarBtnPersonal = findViewById(R.id.entrarBtnPersonal)
        createAccount = findViewById(R.id.createAccountBtn)
        recuperarSenhaBtn = findViewById(R.id.recuperarSenha)

        val senhaAlterada = intent.getBooleanExtra("senha_alterada", false)
        if(senhaAlterada){
            Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()

        emailInput = findViewById(R.id.emailView)
        passwordView = findViewById(R.id.passwordView)

        auth = FirebaseAuth.getInstance()

        entrarBtn.setOnClickListener{

            if(!emailInput.text.toString().isEmpty() && !passwordView.text.toString().isEmpty()){
                auth.signInWithEmailAndPassword(emailInput.text.toString().trim(), passwordView.text.toString().trim())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login_activity", "signInWithEmail:success")
                            val user = auth.currentUser

                            var intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("personalMode", "false")
                            intent.putExtra("userId", user!!.uid)
                            startActivity(intent)

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login_activity", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()

                        }
                    }

            } else{
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }


        }

        entrarBtnPersonal.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("personalMode", "true")
            startActivity(intent)
        }

        createAccount.setOnClickListener {
            var intent = Intent(this, CreateAccountUser::class.java)

            startActivity(intent)
        }

        recuperarSenhaBtn.setOnClickListener {
            var intent = Intent(this, ResetPassword::class.java)

            startActivity(intent)
        }
    }
}
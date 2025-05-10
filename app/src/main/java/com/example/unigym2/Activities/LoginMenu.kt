package com.example.unigym2.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unigym2.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginMenu : AppCompatActivity() {
    lateinit var entrarBtn: Button
    lateinit var entrarBtnUser: Button
    lateinit var entrarBtnPersonal: Button
    lateinit var createAccount: Button
    lateinit var recuperarSenhaBtn: Button
    lateinit var emailInput: TextInputEditText
    lateinit var passwordView: TextInputEditText
    lateinit var foreground: View
    lateinit var progressBar: ProgressBar
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_menu)

        entrarBtn = findViewById(R.id.entrarBtn)
        entrarBtnPersonal = findViewById(R.id.entrarBtnPersonal)
        entrarBtnUser = findViewById(R.id.entrarBtnUser)
        createAccount = findViewById(R.id.createAccountBtn)
        recuperarSenhaBtn = findViewById(R.id.recuperarSenha)

        foreground = findViewById(R.id.foreground)
        progressBar = findViewById(R.id.loadingProgressBar)

        val senhaAlterada = intent.getBooleanExtra("senha_alterada", false)
        if(senhaAlterada){
            Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()

        emailInput = findViewById(R.id.emailView)
        passwordView = findViewById(R.id.passwordView)

        foreground.visibility = View.GONE
        progressBar.visibility = View.GONE

        auth = FirebaseAuth.getInstance()

        entrarBtn.setOnClickListener{
            authLogin(emailInput.text.toString().trim(), passwordView.text.toString().trim())


        }

        entrarBtnPersonal.setOnClickListener {
            authLogin("gustavochavesmacedo@edu.unifor.br", "123456") // usuário personal
        }

        entrarBtnUser.setOnClickListener {
            authLogin("gugasboy7@gmail.com", "123456") // Usuário comum
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

    fun authLogin(email: String, password: String){

        if(!email.isEmpty() && !password.isEmpty()){
            foreground.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("login_activity", "signInWithEmail:success")
                        val user = auth.currentUser

                        var intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("userId", user!!.uid)
                        startActivity(intent)

                    } else {
                        // If sign in fails, display a message to the user.
                        foreground.visibility = View.GONE
                        progressBar.visibility = View.GONE

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
}
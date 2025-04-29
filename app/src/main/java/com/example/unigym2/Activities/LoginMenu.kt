package com.example.unigym2.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unigym2.R

class LoginMenu : AppCompatActivity() {
    lateinit var entrarBtn: Button
    lateinit var entrarBtnPersonal: Button
    lateinit var createAccount: Button
    lateinit var recuperarSenhaBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_menu)

        entrarBtn = findViewById(R.id.confirmNewPasswordButton)
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

        entrarBtn.setOnClickListener{
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("personalMode", "false")
            startActivity(intent)
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
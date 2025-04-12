package com.example.unigym2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginMenu : AppCompatActivity() {
    lateinit var entrarBtn: Button
    lateinit var entrarBtnPersonal: Button
    lateinit var createAccount: Button
    lateinit var recuperarSenhaBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_menu)

        entrarBtn = findViewById(R.id.EntrarBtn)
        entrarBtnPersonal = findViewById(R.id.entrarBtnPersonal)
        createAccount = findViewById(R.id.createAccountBtn)
        recuperarSenhaBtn = findViewById(R.id.recuperarSenha)
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
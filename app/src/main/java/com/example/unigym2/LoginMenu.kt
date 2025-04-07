package com.example.unigym2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginMenu : AppCompatActivity() {
    lateinit var entrarBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_menu)

        entrarBtn = findViewById(R.id.EntrarBtn)
    }

    override fun onStart() {
        super.onStart()

        entrarBtn.setOnClickListener{
            var intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }
    }
}
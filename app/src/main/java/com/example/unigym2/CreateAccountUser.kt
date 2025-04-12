package com.example.unigym2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreateAccountUser : AppCompatActivity() {

    lateinit var personalBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account_user)

        personalBtn = findViewById(R.id.createAccPersonal)
    }

    override fun onStart() {
        super.onStart()

        personalBtn.setOnClickListener {
            var intent = Intent(this, CreateAccountPersonalTrainer::class.java)

            startActivity(intent)
        }
    }
}
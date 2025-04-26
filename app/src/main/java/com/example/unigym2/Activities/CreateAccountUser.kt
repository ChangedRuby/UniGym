package com.example.unigym2.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.unigym2.R
import com.google.android.material.button.MaterialButton

class CreateAccountUser : AppCompatActivity() {
    lateinit var createBtn: MaterialButton
    lateinit var personalBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account_user)

        personalBtn = findViewById(R.id.createAccPersonal)
        createBtn = findViewById(R.id.concludeButton)
    }

    override fun onStart() {
        super.onStart()

        personalBtn.setOnClickListener {
            var intent = Intent(this, CreateAccountPersonalTrainer::class.java)

            startActivity(intent)
        }

        createBtn.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("personalMode", "false")
            startActivity(intent)
        }
    }
}
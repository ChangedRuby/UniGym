package com.example.unigym2.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.unigym2.R

class CreateAccountPersonalTrainer : AppCompatActivity() {

    lateinit var concludeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account_personal_trainer)

        concludeButton = findViewById(R.id.concludeButton)

    }

    override fun onStart() {
        super.onStart()

        concludeButton.setOnClickListener{
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("personalMode", "true")
            startActivity(intent)
        }
    }
}
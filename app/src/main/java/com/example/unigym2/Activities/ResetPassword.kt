package com.example.unigym2.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.unigym2.R

class ResetPassword : AppCompatActivity() {

    lateinit var newPasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        newPasswordButton = findViewById(R.id.entrarBtn)

    }

    override fun onStart() {
        super.onStart()

        newPasswordButton.setOnClickListener {

            var intent = Intent(this, LoginMenu::class.java)
            intent.putExtra("senha_alterada", true)
            startActivity(intent)
        }

    }
}
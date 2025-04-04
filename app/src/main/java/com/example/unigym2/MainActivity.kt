package com.example.unigym2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    lateinit var entrarBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        entrarBtn = findViewById(R.id.EntrarBtn)
    }

    override fun onStart() {
        super.onStart()

        entrarBtn.setOnClickListener{
            var intent = Intent(this, MainMenuUser::class.java)

            startActivity(intent)
        }
    }
}
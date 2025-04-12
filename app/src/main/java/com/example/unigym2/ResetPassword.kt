package com.example.unigym2

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ResetPassword : AppCompatActivity() {

    lateinit var continuarBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
    }
}
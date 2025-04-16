package com.example.unigym2.Activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.unigym2.R

class CreateAccountPersonalTrainer : AppCompatActivity() {

    lateinit var personalBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account_personal_trainer)
    }
}
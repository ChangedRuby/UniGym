package com.example.unigym2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdicionarMaquina : AppCompatActivity() {
    lateinit var cardView: CardView
    lateinit var addButton: Button
    lateinit var nomeMaquina: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adicionar_maquina)
        cardView = findViewById(R.id.CardViewMaquina)
        addButton = findViewById(R.id.AdicionarButton)
        nomeMaquina = findViewById(R.id.editTextNomeMaquina)

    }

}
package com.example.unigym2.Fragments.Home.Recyclerviews

import android.graphics.Bitmap

data class RequestsData(
    val nomeCliente: String? = null,
    val data: String? = null,
    val hora: String? = null,
    val servico: String? = null,
    val clienteID: String? = null,
    val personalID: String? = null,
    val agendamentoID: String? = null,
    val image: Bitmap? = null,
    val requesterFcmToken: String? = null
)
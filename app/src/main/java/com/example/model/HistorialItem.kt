package com.example.model

import java.util.UUID

data class HistorialItem(
    val id: String = UUID.randomUUID().toString(),
    val fecha: String,
    val docente: String,
    val asignatura: String,
    val seccion: String,
    val tipo: String,
    val numeroAsistentes: Int,
    val asistentesFirmados: Int,
    val tema: String = "",
    val personas: List<String> = emptyList()
)

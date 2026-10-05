package com.example.model

import java.util.UUID

data class Asistente(
    val id: String = UUID.randomUUID().toString(),
    var nombre: String = "",
    var rut: String = "",
    var firmado: Boolean = false,
    var observacion: String = ""
)

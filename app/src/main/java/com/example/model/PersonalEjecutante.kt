package com.example.model

import java.util.UUID

data class PersonalEjecutante(
    val id: String = UUID.randomUUID().toString(),
    var nombre: String = "",
    var rut: String = "",
    var firmado: Boolean = false,
    var horaFirma: String = ""
)

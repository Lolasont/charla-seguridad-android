package com.example.model

import java.util.UUID

data class EtapaTrabajo(
    val id: String = UUID.randomUUID().toString(),
    var etapa: String = "",
    var riesgoAsociado: String = "",
    var medidaControl: String = ""
)

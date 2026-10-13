package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "plantillas_actividad")
data class PlantillaActividadEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val actividad: String,
    val especialidad: String,
    val equiposSugeridos: String,
    val eppSugeridos: String,
    val riesgosSugeridos: String,
    val medidasSugeridas: String
)

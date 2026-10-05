package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "arts")
data class ArtEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val trabajoActividad: String,
    val especialidad: String,
    val fecha: String,
    val docente: String,
    val lugar: String,
    val responsableFirmado: Boolean,
    val cerrada: Boolean = false,
    val sincronizado: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

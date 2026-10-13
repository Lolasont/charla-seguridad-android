package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "estudiantes_seccion")
data class EstudianteEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val seccionId: String,
    val nombre: String,
    val rut: String,
    val pinHash: String = "1234"
)

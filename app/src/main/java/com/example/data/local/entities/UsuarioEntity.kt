package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val rut: String,
    val nombre: String,
    val correo: String,
    val rol: String, // "DOCENTE", "JEFE_CARRERA", "MONITOR"
    val passwordHash: String = "1234"
)

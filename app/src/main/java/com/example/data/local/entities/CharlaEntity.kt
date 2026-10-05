package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "charlas")
data class CharlaEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val docente: String,
    val asignatura: String,
    val seccion: String,
    val fecha: String,
    val horaInicio: String,
    val horaTermino: String,
    val tema: String,
    val tipoCharla: String,
    val cerrada: Boolean = false,
    val sincronizado: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

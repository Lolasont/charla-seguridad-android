package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "asignaturas_secciones")
data class AsignaturaEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val codigoRamo: String,
    val nombreRamo: String,
    val seccion: String,
    val docenteRut: String
)

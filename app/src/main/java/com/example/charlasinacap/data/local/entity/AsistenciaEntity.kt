package com.example.charlasinacap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asistencia")
data class AsistenciaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    var charlaId: Long = 0L,
    var nombre: String = "",
    var rut: String = "",
    var firma: String = "",
    var observacion: String = ""
)

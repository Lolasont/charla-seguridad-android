package com.example.charlasinacap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "charla")
data class CharlaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    var docente: String = "",
    var asignatura: String = "",
    var seccion: String = "",
    var horaInicio: String = "",
    var horaTermino: String = "",
    var fecha: String = "",
    var tema: String = "",
    var tipoCharla: String = ""
)

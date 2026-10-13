package com.example.charlasinacap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asaa")
data class AsaaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    var trabajoActividad: String = "",
    var especialidad: String = "",
    var fecha: String = "",
    var tipoActividad: String = "",
    var docente: String = "",
    var ubicacion: String = "",
    var nivelInicial: Boolean = false,
    var nivelIntermedio: Boolean = false,
    var nivelAvanzado: Boolean = false,
    var charlaInicial: Boolean = false,
    var asaaComprendido: Boolean = false,
    var loto: Boolean = false,
    var observaciones: String = "",
    var cerrado: Boolean = false
)

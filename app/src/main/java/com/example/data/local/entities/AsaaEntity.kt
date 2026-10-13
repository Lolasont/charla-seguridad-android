package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "asaas")
data class AsaaEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val trabajoActividad: String,
    val especialidad: String,
    val fecha: String,
    val docente: String,
    val lugar: String,
    val asignatura: String = "",
    val seccion: String = "",
    val horaInicio: String = "",
    val horaTermino: String = "",
    val tipoCharla: String = "Charla 5 minutos",
    val tema: String = "",
    val tipoActividad: String = "Práctica",
    val nivelTecnico: String = "Inicial",
    val equipoApoyo: String = "",
    val equipoApoyoOtros: String = "",
    val epp: String = "",
    val eppOtros: String = "",
    val comandoVoz: String = "",
    val condicionesAmbientales: String = "",
    val condicionesAmbientalesOtros: String = "",
    val charlaSeguridadInicial: Boolean = false,
    val charlaVinculadaId: String? = null,
    val charlaVinculadaFecha: String? = null,
    val charlaVinculadaTema: String? = null,
    val medidasControl: String = "",
    val medidasControlOtros: String = "",
    val loto: Boolean = false,
    val asaaComprendido: Boolean = false,
    val observaciones: String = "",
    val hayFar: Boolean = false,
    val responsableFirmado: Boolean = false,
    val cerrada: Boolean = false,
    val sincronizado: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

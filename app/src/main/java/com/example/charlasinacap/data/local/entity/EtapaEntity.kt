package com.example.charlasinacap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asaa_etapa")
data class EtapaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    var asaaId: Long = 0L,
    var descripcion: String = "",
    var riesgos: String = "",
    var medidas: String = ""
)

package com.example.charlasinacap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asaa_personal")
data class PersonalEjecutanteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    var asaaId: Long = 0L,
    var nombre: String = ""
)

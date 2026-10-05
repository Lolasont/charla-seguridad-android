package com.example.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class CharlaConAsistentes(
    @Embedded val charla: CharlaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "charlaId"
    )
    val asistentes: List<AsistenteEntity>
)

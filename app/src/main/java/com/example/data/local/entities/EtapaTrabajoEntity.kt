package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "etapas_trabajo",
    foreignKeys = [
        ForeignKey(
            entity = AsaaEntity::class,
            parentColumns = ["id"],
            childColumns = ["asaaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("asaaId")]
)
data class EtapaTrabajoEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val asaaId: String,
    val etapa: String,
    val riesgoAsociado: String,
    val medidaControl: String,
    val orden: Int = 0
)

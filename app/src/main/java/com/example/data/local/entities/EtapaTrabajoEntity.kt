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
            entity = ArtEntity::class,
            parentColumns = ["id"],
            childColumns = ["artId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("artId")]
)
data class EtapaTrabajoEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val artId: String,
    val etapa: String,
    val riesgoAsociado: String,
    val medidaControl: String,
    val orden: Int = 0
)

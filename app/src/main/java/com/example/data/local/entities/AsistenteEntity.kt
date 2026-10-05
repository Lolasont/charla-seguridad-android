package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "asistentes",
    foreignKeys = [
        ForeignKey(
            entity = CharlaEntity::class,
            parentColumns = ["id"],
            childColumns = ["charlaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("charlaId")]
)
data class AsistenteEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val charlaId: String,
    val nombre: String,
    val rut: String,
    val firmado: Boolean,
    val observacion: String,
    val orden: Int = 0
)

package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "personal_ejecutante",
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
data class PersonalEjecutanteEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val artId: String,
    val nombre: String,
    val rut: String,
    val firmado: Boolean = false,
    val horaFirma: String = "",
    val orden: Int = 0
)

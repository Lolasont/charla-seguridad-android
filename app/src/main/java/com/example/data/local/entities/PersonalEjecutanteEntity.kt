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
            entity = AsaaEntity::class,
            parentColumns = ["id"],
            childColumns = ["asaaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("asaaId")]
)
data class PersonalEjecutanteEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val asaaId: String,
    val nombre: String,
    val rut: String,
    val firmado: Boolean = false,
    val horaFirma: String = "",
    val orden: Int = 0,
    val far: Boolean = false
)

package com.example.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ArtConEtapas(
    @Embedded val art: ArtEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "artId"
    )
    val etapas: List<EtapaTrabajoEntity>
)

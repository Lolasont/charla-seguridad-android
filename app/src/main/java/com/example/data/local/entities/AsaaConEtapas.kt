package com.example.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class AsaaConEtapas(
    @Embedded val asaa: AsaaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "asaaId"
    )
    val etapas: List<EtapaTrabajoEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "asaaId"
    )
    val personalEjecutante: List<PersonalEjecutanteEntity> = emptyList()
) {
    val art: AsaaEntity get() = asaa
}

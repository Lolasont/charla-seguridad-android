package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.local.entities.ArtConEtapas
import com.example.data.local.entities.ArtEntity
import com.example.data.local.entities.EtapaTrabajoEntity
import com.example.data.local.entities.PersonalEjecutanteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtDao {
    @Transaction
    @Query("SELECT * FROM arts ORDER BY timestamp DESC")
    fun getArtsConEtapas(): Flow<List<ArtConEtapas>>

    @Transaction
    @Query("SELECT * FROM arts WHERE id = :id")
    suspend fun getArtConEtapasById(id: String): ArtConEtapas?

    @Query("SELECT * FROM arts WHERE sincronizado = 0")
    suspend fun getArtsPendientes(): List<ArtEntity>

    @Query("UPDATE arts SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizado(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArt(art: ArtEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEtapas(etapas: List<EtapaTrabajoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalEjecutante(personal: List<PersonalEjecutanteEntity>)

    @Transaction
    suspend fun insertArtCompleto(
        art: ArtEntity,
        etapas: List<EtapaTrabajoEntity>,
        personal: List<PersonalEjecutanteEntity> = emptyList()
    ) {
        insertArt(art)
        insertEtapas(etapas)
        if (personal.isNotEmpty()) {
            insertPersonalEjecutante(personal)
        }
    }

    @Query("SELECT COUNT(*) FROM arts")
    suspend fun getCount(): Int
}

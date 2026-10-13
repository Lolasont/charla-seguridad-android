package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.local.entities.AsaaConEtapas
import com.example.data.local.entities.AsaaEntity
import com.example.data.local.entities.EtapaTrabajoEntity
import com.example.data.local.entities.PersonalEjecutanteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AsaaDao {
    @Transaction
    @Query("SELECT * FROM asaas ORDER BY timestamp DESC")
    fun getAsaasConEtapas(): Flow<List<AsaaConEtapas>>

    @Transaction
    @Query("SELECT * FROM asaas WHERE id = :id")
    suspend fun getAsaaConEtapasById(id: String): AsaaConEtapas?

    @Query("SELECT * FROM asaas WHERE sincronizado = 0")
    suspend fun getAsaasPendientes(): List<AsaaEntity>

    @Query("UPDATE asaas SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizado(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsaa(asaa: AsaaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEtapas(etapas: List<EtapaTrabajoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalEjecutante(personal: List<PersonalEjecutanteEntity>)

    @Query("DELETE FROM etapas_trabajo WHERE asaaId = :asaaId")
    suspend fun deleteEtapasByAsaaId(asaaId: String)

    @Query("DELETE FROM personal_ejecutante WHERE asaaId = :asaaId")
    suspend fun deletePersonalByAsaaId(asaaId: String)

    @Transaction
    suspend fun insertAsaaCompleto(
        asaa: AsaaEntity,
        etapas: List<EtapaTrabajoEntity>,
        personal: List<PersonalEjecutanteEntity> = emptyList()
    ) {
        insertAsaa(asaa)
        deleteEtapasByAsaaId(asaa.id)
        deletePersonalByAsaaId(asaa.id)
        if (etapas.isNotEmpty()) {
            insertEtapas(etapas)
        }
        if (personal.isNotEmpty()) {
            insertPersonalEjecutante(personal)
        }
    }

    @Query("SELECT COUNT(*) FROM asaas")
    suspend fun getCount(): Int
}

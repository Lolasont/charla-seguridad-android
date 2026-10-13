package com.example.charlasinacap.data.local.dao

import androidx.room.*
import com.example.charlasinacap.data.local.entity.*

@Dao
interface AsaaDao {

    @Insert
    suspend fun insertAsaa(asaa: AsaaEntity): Long

    @Update
    suspend fun updateAsaa(asaa: AsaaEntity)

    @Query("SELECT * FROM asaa ORDER BY id DESC LIMIT 1")
    suspend fun getLatestAsaa(): AsaaEntity?

    @Query("SELECT * FROM asaa WHERE id = :id")
    suspend fun getAsaa(id: Long): AsaaEntity?

    @Query("SELECT * FROM asaa")
    suspend fun getAllAsaa(): List<AsaaEntity>

    @Delete
    suspend fun deleteAsaa(asaa: AsaaEntity)

    // Inserts para relaciones
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipos(equipos: List<AsaaEquipoApoyoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpps(epps: List<EppEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCondiciones(condiciones: List<CondicionAmbientalEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedidas(medidas: List<MedidaControlEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonal(personal: List<PersonalEjecutanteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEtapas(etapas: List<EtapaEntity>)

    // Consultas de relaciones de ASAA
    @Query("SELECT * FROM asaa_equipo_apoyo WHERE asaaId = :asaaId")
    suspend fun getEquiposByAsaa(asaaId: Long): List<AsaaEquipoApoyoEntity>

    @Query("SELECT * FROM asaa_epp WHERE asaaId = :asaaId")
    suspend fun getEppsByAsaa(asaaId: Long): List<EppEntity>

    @Query("SELECT * FROM asaa_condicion WHERE asaaId = :asaaId")
    suspend fun getCondicionesByAsaa(asaaId: Long): List<CondicionAmbientalEntity>

    @Query("SELECT * FROM asaa_medida WHERE asaaId = :asaaId")
    suspend fun getMedidasByAsaa(asaaId: Long): List<MedidaControlEntity>

    @Query("SELECT * FROM asaa_personal WHERE asaaId = :asaaId")
    suspend fun getPersonalByAsaa(asaaId: Long): List<PersonalEjecutanteEntity>

    @Query("SELECT * FROM asaa_etapa WHERE asaaId = :asaaId")
    suspend fun getEtapas(asaaId: Long): List<EtapaEntity>
}

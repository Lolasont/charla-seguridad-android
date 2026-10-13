package com.example.charlasinacap.data.local.dao

import androidx.room.*
import com.example.charlasinacap.data.local.entity.*

@Dao
interface CharlaDao {

    @Insert
    suspend fun insertCharla(charla: CharlaEntity): Long

    @Update
    suspend fun updateCharla(charla: CharlaEntity)

    @Query("SELECT * FROM charla ORDER BY id DESC LIMIT 1")
    suspend fun getLatestCharla(): CharlaEntity?

    @Query("SELECT * FROM charla WHERE id = :id")
    suspend fun getCharla(id: Long): CharlaEntity?

    @Delete
    suspend fun deleteCharla(charla: CharlaEntity)

    // Asistencias
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsistencias(asistencias: List<AsistenciaEntity>)

    @Query("DELETE FROM asistencia WHERE charlaId = :charlaId")
    suspend fun deleteAsistenciasByCharla(charlaId: Long)

    @Query("SELECT * FROM asistencia WHERE charlaId = :charlaId")
    suspend fun getAsistencias(charlaId: Long): List<AsistenciaEntity>
}

package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.local.entities.AsistenteEntity
import com.example.data.local.entities.CharlaConAsistentes
import com.example.data.local.entities.CharlaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharlaDao {
    @Transaction
    @Query("SELECT * FROM charlas ORDER BY timestamp DESC")
    fun getCharlasConAsistentes(): Flow<List<CharlaConAsistentes>>

    @Transaction
    @Query("SELECT * FROM charlas WHERE id = :id")
    suspend fun getCharlaConAsistentesById(id: String): CharlaConAsistentes?

    @Query("SELECT * FROM charlas WHERE sincronizado = 0")
    suspend fun getCharlasPendientes(): List<CharlaEntity>

    @Query("UPDATE charlas SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizado(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharla(charla: CharlaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsistentes(asistentes: List<AsistenteEntity>)

    @Transaction
    suspend fun insertCharlaCompleta(charla: CharlaEntity, asistentes: List<AsistenteEntity>) {
        insertCharla(charla)
        insertAsistentes(asistentes)
    }

    @Query("SELECT COUNT(*) FROM charlas")
    suspend fun getCount(): Int
}

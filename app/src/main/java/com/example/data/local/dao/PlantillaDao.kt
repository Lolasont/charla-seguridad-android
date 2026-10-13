package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.PlantillaActividadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantillaDao {
    @Query("SELECT * FROM plantillas_actividad WHERE actividad LIKE '%' || :query || '%'")
    fun buscarPlantillas(query: String): Flow<List<PlantillaActividadEntity>>

    @Query("SELECT * FROM plantillas_actividad")
    suspend fun getTodasLasPlantillas(): List<PlantillaActividadEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlantilla(plantilla: PlantillaActividadEntity)
}

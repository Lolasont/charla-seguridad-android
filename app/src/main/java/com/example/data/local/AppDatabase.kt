package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AsaaDao
import com.example.data.local.dao.CharlaDao
import com.example.data.local.dao.PlantillaDao
import com.example.data.local.dao.UsuarioDao
import com.example.data.local.entities.AsaaEntity
import com.example.data.local.entities.AsignaturaEntity
import com.example.data.local.entities.AsistenteEntity
import com.example.data.local.entities.CharlaEntity
import com.example.data.local.entities.EstudianteEntity
import com.example.data.local.entities.EtapaTrabajoEntity
import com.example.data.local.entities.PersonalEjecutanteEntity
import com.example.data.local.entities.PlantillaActividadEntity
import com.example.data.local.entities.UsuarioEntity

@Database(
    entities = [
        CharlaEntity::class,
        AsistenteEntity::class,
        AsaaEntity::class,
        EtapaTrabajoEntity::class,
        PersonalEjecutanteEntity::class,
        UsuarioEntity::class,
        AsignaturaEntity::class,
        EstudianteEntity::class,
        PlantillaActividadEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun charlaDao(): CharlaDao
    abstract fun asaaDao(): AsaaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun plantillaDao(): PlantillaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inacap_seguridad.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

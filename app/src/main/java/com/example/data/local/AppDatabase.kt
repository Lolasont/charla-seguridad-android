package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ArtDao
import com.example.data.local.dao.CharlaDao
import com.example.data.local.entities.ArtEntity
import com.example.data.local.entities.AsistenteEntity
import com.example.data.local.entities.CharlaEntity
import com.example.data.local.entities.EtapaTrabajoEntity

@Database(
    entities = [
        CharlaEntity::class,
        AsistenteEntity::class,
        ArtEntity::class,
        EtapaTrabajoEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun charlaDao(): CharlaDao
    abstract fun artDao(): ArtDao

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

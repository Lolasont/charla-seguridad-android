package com.example.charlasinacap.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.charlasinacap.data.local.dao.AsaaDao
import com.example.charlasinacap.data.local.dao.CharlaDao
import com.example.charlasinacap.data.local.entity.*

@Database(
    entities = [
        AsaaEntity::class,
        AsaaEquipoApoyoEntity::class,
        EppEntity::class,
        CondicionAmbientalEntity::class,
        MedidaControlEntity::class,
        PersonalEjecutanteEntity::class,
        EtapaEntity::class,
        CharlaEntity::class,
        AsistenciaEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun asaaDao(): AsaaDao
    abstract fun charlaDao(): CharlaDao
}

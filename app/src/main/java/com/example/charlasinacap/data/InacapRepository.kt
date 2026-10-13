package com.example.charlasinacap.data

import android.content.Context
import androidx.room.Room
import com.example.charlasinacap.data.local.AppDatabase
import com.example.charlasinacap.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InacapRepository private constructor(private val db: AppDatabase) {

    private val asaaDao = db.asaaDao()
    private val charlaDao = db.charlaDao()

    // ---------- ASAA ----------
    suspend fun saveAsaa(
        asaa: AsaaEntity,
        equipos: List<AsaaEquipoApoyoEntity>,
        epps: List<EppEntity>,
        condiciones: List<CondicionAmbientalEntity>,
        medidas: List<MedidaControlEntity>,
        personal: List<PersonalEjecutanteEntity>,
        etapas: List<EtapaEntity>
    ): Long = withContext(Dispatchers.IO) {
        val asaaId = asaaDao.insertAsaa(asaa)
        equipos.forEach { it.asaaId = asaaId }
        epps.forEach { it.asaaId = asaaId }
        condiciones.forEach { it.asaaId = asaaId }
        medidas.forEach { it.asaaId = asaaId }
        personal.forEach { it.asaaId = asaaId }
        etapas.forEach { it.asaaId = asaaId }
        asaaDao.insertEquipos(equipos)
        asaaDao.insertEpps(epps)
        asaaDao.insertCondiciones(condiciones)
        asaaDao.insertMedidas(medidas)
        asaaDao.insertPersonal(personal)
        asaaDao.insertEtapas(etapas)
        asaaId
    }

    suspend fun getLatestAsaa(): AsaaEntity? = withContext(Dispatchers.IO) {
        asaaDao.getLatestAsaa()
    }

    suspend fun getAllAsaa(): List<AsaaEntity> = withContext(Dispatchers.IO) {
        asaaDao.getAllAsaa()
    }

    // Duplicar ASAA (copia el último registro)
    suspend fun duplicateLastAsaa(): Long? = withContext(Dispatchers.IO) {
        val last = asaaDao.getLatestAsaa() ?: return@withContext null
        // Copiar entidad principal (sin id)
        val copy = last.copy(id = 0L, cerrado = false)
        // Insertar nueva ASAA y obtener nuevo id
        val newId = asaaDao.insertAsaa(copy)
        // Copiar relaciones (equipos, epp, etc.)
        val equipos = asaaDao.getEquiposByAsaa(last.id)
        val epps = asaaDao.getEppsByAsaa(last.id)
        val condiciones = asaaDao.getCondicionesByAsaa(last.id)
        val medidas = asaaDao.getMedidasByAsaa(last.id)
        val personal = asaaDao.getPersonalByAsaa(last.id)
        val etapas = asaaDao.getEtapas(last.id)
        // Asignar nuevo id a cada elemento y guardarlos
        equipos.forEach { it.asaaId = newId }
        epps.forEach { it.asaaId = newId }
        condiciones.forEach { it.asaaId = newId }
        medidas.forEach { it.asaaId = newId }
        personal.forEach { it.asaaId = newId }
        etapas.forEach { it.asaaId = newId }
        asaaDao.insertEquipos(equipos)
        asaaDao.insertEpps(epps)
        asaaDao.insertCondiciones(condiciones)
        asaaDao.insertMedidas(medidas)
        asaaDao.insertPersonal(personal)
        asaaDao.insertEtapas(etapas)
        newId
    }

    // ---------- CHARLA ----------
    suspend fun saveCharla(
        charla: CharlaEntity,
        asistencias: List<AsistenciaEntity>
    ): Long = withContext(Dispatchers.IO) {
        val charlaId = charlaDao.insertCharla(charla)
        asistencias.forEach { it.charlaId = charlaId }
        charlaDao.insertAsistencias(asistencias)
        charlaId
    }

    suspend fun getLatestCharla(): CharlaEntity? = withContext(Dispatchers.IO) {
        charlaDao.getLatestCharla()
    }

    companion object {
        @Volatile private var INSTANCE: InacapRepository? = null
        fun getInstance(context: Context): InacapRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildRepository(context).also { INSTANCE = it }
            }
        private fun buildRepository(context: Context): InacapRepository {
            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "inacap-db"
            )
                .fallbackToDestructiveMigration()
                .build()
            return InacapRepository(db)
        }
    }
}

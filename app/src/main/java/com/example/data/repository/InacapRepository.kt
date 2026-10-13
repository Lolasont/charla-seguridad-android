package com.example.data.repository

import com.example.data.local.dao.AsaaDao
import com.example.data.local.dao.CharlaDao
import com.example.data.local.entities.AsaaConEtapas
import com.example.data.local.entities.AsaaEntity
import com.example.data.local.entities.AsistenteEntity
import com.example.data.local.entities.CharlaConAsistentes
import com.example.data.local.entities.CharlaEntity
import com.example.data.local.entities.EtapaTrabajoEntity
import com.example.data.local.entities.PersonalEjecutanteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class InacapRepository(
    private val charlaDao: CharlaDao,
    private val asaaDao: AsaaDao
) {
    val todasLasCharlas: Flow<List<CharlaConAsistentes>> = charlaDao.getCharlasConAsistentes()
    val todosLosAsaas: Flow<List<AsaaConEtapas>> = asaaDao.getAsaasConEtapas()

    suspend fun guardarCharlaCompleta(charla: CharlaEntity, asistentes: List<AsistenteEntity>) {
        withContext(Dispatchers.IO) {
            charlaDao.insertCharlaCompleta(charla, asistentes)
        }
    }

    suspend fun guardarAsaaCompleto(
        asaa: AsaaEntity,
        etapas: List<EtapaTrabajoEntity>,
        personal: List<PersonalEjecutanteEntity> = emptyList()
    ) {
        withContext(Dispatchers.IO) {
            asaaDao.insertAsaaCompleto(asaa, etapas, personal)
        }
    }

    suspend fun getCharlasPendientes(): List<CharlaEntity> {
        return withContext(Dispatchers.IO) {
            charlaDao.getCharlasPendientes()
        }
    }

    suspend fun getAsaasPendientes(): List<AsaaEntity> {
        return withContext(Dispatchers.IO) {
            asaaDao.getAsaasPendientes()
        }
    }

    suspend fun sincronizarCharla(id: String) {
        withContext(Dispatchers.IO) {
            charlaDao.marcarSincronizado(id)
        }
    }

    suspend fun sincronizarAsaa(id: String) {
        withContext(Dispatchers.IO) {
            asaaDao.marcarSincronizado(id)
        }
    }

    suspend fun inicializarDatosSiEsNecesario() {
        withContext(Dispatchers.IO) {
            if (charlaDao.getCount() == 0) {
                prepopularCharlasIniciales()
            }
            if (asaaDao.getCount() == 0) {
                prepopularAsaasIniciales()
            }
        }
    }

    private suspend fun prepopularCharlasIniciales() {
        // Ejemplo 1: Sincronizado
        val c1Id = UUID.randomUUID().toString()
        val c1 = CharlaEntity(
            id = c1Id,
            docente = "Carlos Silva Rojas",
            asignatura = "Taller de Mantenimiento Electromecánico",
            seccion = "002D",
            fecha = "02/10/2026",
            horaInicio = "08:30",
            horaTermino = "09:15",
            tema = "Inspección previa de herramientas eléctricas y bloqueo LOTO",
            tipoCharla = "Charla 5 minutos",
            cerrada = true,
            sincronizado = true,
            timestamp = 1790928000000L
        )
        val a1 = listOf(
            AsistenteEntity(charlaId = c1Id, nombre = "Juan Morales Carrasco", rut = "19.845.210-4", firmado = true, horaFirma = "08:35", observacion = "EPP completo", esExterno = false, procedencia = "", orden = 1),
            AsistenteEntity(charlaId = c1Id, nombre = "Felipe Carrasco Vidal", rut = "20.114.892-7", firmado = true, horaFirma = "08:36", observacion = "Sin observaciones", esExterno = false, procedencia = "", orden = 2),
            AsistenteEntity(charlaId = c1Id, nombre = "Daniela Vega Poblete", rut = "19.531.004-K", firmado = true, horaFirma = "08:37", observacion = "Verificación multímetro OK", esExterno = false, procedencia = "", orden = 3),
            AsistenteEntity(charlaId = c1Id, nombre = "Ignacio Reyes Pinto", rut = "20.301.442-1", firmado = true, horaFirma = "08:40", observacion = "EPP dieléctrico verificado", esExterno = true, procedencia = "Liceo Industrial de Concepción", orden = 4)
        )
        charlaDao.insertCharlaCompleta(c1, a1)

        // Ejemplo 2: ⏳ Pendiente de sincronizar
        val c2Id = UUID.randomUUID().toString()
        val c2 = CharlaEntity(
            id = c2Id,
            docente = "Marcela Pardo Fuentes",
            asignatura = "Automatización y Robótica Industrial",
            seccion = "001D",
            fecha = "29/09/2026",
            horaInicio = "10:00",
            horaTermino = "10:45",
            tema = "Paradas de emergencia y protocolos en celdas robotizadas",
            tipoCharla = "Charla Integral",
            cerrada = true,
            sincronizado = false,
            timestamp = 1790668800000L
        )
        val a2 = listOf(
            AsistenteEntity(charlaId = c2Id, nombre = "Ignacio Soto Palma", rut = "19.442.109-8", firmado = true, horaFirma = "10:05", observacion = "Uso de calzado de seguridad", esExterno = false, procedencia = "", orden = 1),
            AsistenteEntity(charlaId = c2Id, nombre = "Valentina Rivas Gómez", rut = "20.551.782-3", firmado = true, horaFirma = "10:07", observacion = "Sin novedades", esExterno = false, procedencia = "", orden = 2),
            AsistenteEntity(charlaId = c2Id, nombre = "Matías Alarcón Leal", rut = "19.980.231-5", firmado = true, horaFirma = "10:10", observacion = "Barreras ópticas revisadas", esExterno = true, procedencia = "Liceo Politécnico Carampangue", orden = 3),
            AsistenteEntity(charlaId = c2Id, nombre = "Camila Torres Muñoz", rut = "20.123.456-0", firmado = true, horaFirma = "10:12", observacion = "Sin novedades", esExterno = false, procedencia = "", orden = 4)
        )
        charlaDao.insertCharlaCompleta(c2, a2)

        // Ejemplo 3: Sincronizado
        val c3Id = UUID.randomUUID().toString()
        val c3 = CharlaEntity(
            id = c3Id,
            docente = "Héctor Garrido Vera",
            asignatura = "Sistemas de Frenos y Dirección",
            seccion = "003V",
            fecha = "25/09/2026",
            horaInicio = "14:15",
            horaTermino = "15:00",
            tema = "Riesgos en el uso de elevadores hidráulicos y fosas de inspección",
            tipoCharla = "Reinstrucción",
            cerrada = true,
            sincronizado = true,
            timestamp = 1790323200000L
        )
        val a3 = listOf(
            AsistenteEntity(charlaId = c3Id, nombre = "Sebastián Muñoz Rivas", rut = "18.994.512-1", firmado = true, horaFirma = "14:20", observacion = "Traba mecánica verificada", esExterno = false, procedencia = "", orden = 1),
            AsistenteEntity(charlaId = c3Id, nombre = "Lucas Henríquez Silva", rut = "19.231.874-9", firmado = true, horaFirma = "14:21", observacion = "Sin observaciones", esExterno = false, procedencia = "", orden = 2),
            AsistenteEntity(charlaId = c3Id, nombre = "Javier Castro Pino", rut = "20.004.112-6", firmado = true, horaFirma = "14:23", observacion = "Gafas de seguridad puestas", esExterno = false, procedencia = "", orden = 3),
            AsistenteEntity(charlaId = c3Id, nombre = "Nicolás Peña Vera", rut = "19.782.339-K", firmado = true, horaFirma = "14:25", observacion = "Sin observaciones", esExterno = false, procedencia = "", orden = 4)
        )
        charlaDao.insertCharlaCompleta(c3, a3)

        // Ejemplo 4: ⏳ Pendiente de sincronizar
        val c4Id = UUID.randomUUID().toString()
        val c4 = CharlaEntity(
            id = c4Id,
            docente = "Andrea Navarrete Soto",
            asignatura = "Prevención de Riesgos en Minería y Construcción",
            seccion = "001V",
            fecha = "18/09/2026",
            horaInicio = "09:00",
            horaTermino = "09:40",
            tema = "Procedimientos de trabajo seguro en excavaciones y taludes",
            tipoCharla = "Charla Externa",
            cerrada = true,
            sincronizado = false,
            timestamp = 1789718400000L
        )
        val a4 = listOf(
            AsistenteEntity(charlaId = c4Id, nombre = "Camilo Ortiz Durán", rut = "18.774.290-3", firmado = true, horaFirma = "09:08", observacion = "Casco barboquejo OK", esExterno = false, procedencia = "", orden = 1),
            AsistenteEntity(charlaId = c4Id, nombre = "Sofía Valenzuela Parra", rut = "20.401.882-7", firmado = true, horaFirma = "09:10", observacion = "Sin observaciones", esExterno = false, procedencia = "", orden = 2),
            AsistenteEntity(charlaId = c4Id, nombre = "Pablo Pinto Sanhueza", rut = "19.664.120-2", firmado = true, horaFirma = "09:12", observacion = "Chaleco reflectante OK", esExterno = false, procedencia = "", orden = 3),
            AsistenteEntity(charlaId = c4Id, nombre = "Constanza Morales", rut = "20.198.441-5", firmado = true, horaFirma = "09:15", observacion = "Sin novedades", esExterno = true, procedencia = "Liceo Comercial San Pedro", orden = 4)
        )
        charlaDao.insertCharlaCompleta(c4, a4)

        // Ejemplo 5: Sincronizado
        val c5Id = UUID.randomUUID().toString()
        val c5 = CharlaEntity(
            id = c5Id,
            docente = "Rodrigo Morales Castillo",
            asignatura = "Laboratorio de Ensayos No Destructivos",
            seccion = "002D",
            fecha = "11/09/2026",
            horaInicio = "11:30",
            horaTermino = "12:15",
            tema = "Protección radiológica y manejo seguro de líquidos penetrantes",
            tipoCharla = "Charla Interna",
            cerrada = true,
            sincronizado = true,
            timestamp = 1789113600000L
        )
        val a5 = listOf(
            AsistenteEntity(charlaId = c5Id, nombre = "Constanza Bravo Jara", rut = "19.345.109-7", firmado = true, horaFirma = "11:35", observacion = "Guantes de nitrilo", esExterno = false, procedencia = "", orden = 1),
            AsistenteEntity(charlaId = c5Id, nombre = "Diego Salgado Flores", rut = "20.211.890-4", firmado = true, horaFirma = "11:38", observacion = "Campana de extracción activa", esExterno = false, procedencia = "", orden = 2),
            AsistenteEntity(charlaId = c5Id, nombre = "Esteban Reyes Vidal", rut = "19.892.331-2", firmado = true, horaFirma = "11:40", observacion = "Sin observaciones", esExterno = false, procedencia = "", orden = 3),
            AsistenteEntity(charlaId = c5Id, nombre = "Francisca Lara", rut = "20.089.123-K", firmado = true, horaFirma = "11:42", observacion = "Sin novedades", esExterno = false, procedencia = "", orden = 4)
        )
        charlaDao.insertCharlaCompleta(c5, a5)
    }

    private suspend fun prepopularAsaasIniciales() {
        val asaa1Id = UUID.randomUUID().toString()
        val asaa1 = AsaaEntity(
            id = asaa1Id,
            trabajoActividad = "Desarme y montaje de culata de motor Diésel",
            especialidad = "Mecánica Automotriz y Autotrónica",
            fecha = "03/10/2026",
            docente = "Rodrigo Morales Castillo",
            lugar = "Taller Mecánico N° 2",
            tipoActividad = "Práctica",
            nivelTecnico = "Intermedio",
            equipoApoyo = "Herramientas mecánicas|Máquinas de presión hidráulica|Taladro eléctrico",
            equipoApoyoOtros = "",
            epp = "Guante de cabritilla|Zapatos de Seguridad dieléctrico|Antiparra de Seguridad",
            eppOtros = "",
            comandoVoz = "Atención|Precaución",
            condicionesAmbientales = "Ruido|Superficies resbaladizas",
            condicionesAmbientalesOtros = "",
            charlaSeguridadInicial = true,
            charlaVinculadaTema = "Riesgos en el uso de elevadores hidráulicos y fosas de inspección",
            charlaVinculadaFecha = "25/09/2026",
            medidasControl = "1. Verificación de ausencia de tensión|3. Inspección de herramientas y equipos|6. Protección auditiva y visual según la tarea",
            loto = true,
            asaaComprendido = true,
            observaciones = "Sin novedades durante la actividad",
            hayFar = false,
            responsableFirmado = true,
            cerrada = true,
            sincronizado = true,
            timestamp = 1791014400000L
        )
        val e1 = listOf(
            EtapaTrabajoEntity(asaaId = asaa1Id, etapa = "Desconexión de batería y drenaje de refrigerante", riesgoAsociado = "9 CC superficies calientes", medidaControl = "1. Verificación de ausencia de tensión", orden = 1),
            EtapaTrabajoEntity(asaaId = asaa1Id, etapa = "Retiro de múltiples de admisión y escape", riesgoAsociado = "19 Golpeado por/Contra/Con", medidaControl = "3. Inspección de herramientas y equipos", orden = 2),
            EtapaTrabajoEntity(asaaId = asaa1Id, etapa = "Izaje y retiro de culata", riesgoAsociado = "16 Caída igual o distinto nivel", medidaControl = "4. Delimitación y señalización del área de trabajo", orden = 3)
        )
        val p1 = listOf(
            PersonalEjecutanteEntity(asaaId = asaa1Id, nombre = "Juan Morales Carrasco", rut = "19.845.210-4", firmado = true, horaFirma = "08:45", orden = 1, far = false),
            PersonalEjecutanteEntity(asaaId = asaa1Id, nombre = "Felipe Carrasco Vidal", rut = "20.114.892-7", firmado = true, horaFirma = "08:46", orden = 2, far = false),
            PersonalEjecutanteEntity(asaaId = asaa1Id, nombre = "Daniela Vega Poblete", rut = "19.531.004-K", firmado = true, horaFirma = "08:48", orden = 3, far = false)
        )
        asaaDao.insertAsaaCompleto(asaa1, e1, p1)

        val asaa2Id = UUID.randomUUID().toString()
        val asaa2 = AsaaEntity(
            id = asaa2Id,
            trabajoActividad = "Calibración de sensores y tablero de control PLC",
            especialidad = "Electricidad Industrial",
            fecha = "30/09/2026",
            docente = "Carlos Silva Rojas",
            lugar = "Laboratorio de Control y Automatización",
            tipoActividad = "Práctica",
            nivelTecnico = "Avanzado",
            equipoApoyo = "Red eléctrica trifásica|Dispositivos de maniobra eléctrica|Tablero o Gabinete eléctrico|Instrumento de medida",
            equipoApoyoOtros = "",
            epp = "Guante dieléctrico|Zapatos de Seguridad dieléctrico|Antiparra de Seguridad|Casco dieléctrico|Lápiz detector de voltaje",
            eppOtros = "",
            comandoVoz = "Alto|Atención|Precaución",
            condicionesAmbientales = "Sin Ventilación",
            condicionesAmbientalesOtros = "",
            charlaSeguridadInicial = true,
            charlaVinculadaTema = "Inspección previa de herramientas eléctricas y bloqueo LOTO",
            charlaVinculadaFecha = "02/10/2026",
            medidasControl = "1. Verificación de ausencia de tensión|2. Equipo de protección personal dieléctrico|4. Delimitación y señalización del área de trabajo",
            loto = true,
            asaaComprendido = true,
            observaciones = "Uso riguroso de bloqueo LOTO en tablero principal",
            hayFar = false,
            responsableFirmado = true,
            cerrada = true,
            sincronizado = false,
            timestamp = 1790755200000L
        )
        val e2 = listOf(
            EtapaTrabajoEntity(asaaId = asaa2Id, etapa = "Verificación de ausencia de tensión en barra principal", riesgoAsociado = "1 Choque eléctrico", medidaControl = "1. Verificación de ausencia de tensión", orden = 1),
            EtapaTrabajoEntity(asaaId = asaa2Id, etapa = "Conexión de cableado de comunicación", riesgoAsociado = "6 CC partes activas", medidaControl = "2. Equipo de protección personal dieléctrico", orden = 2)
        )
        val p2 = listOf(
            PersonalEjecutanteEntity(asaaId = asaa2Id, nombre = "Ignacio Soto Palma", rut = "19.442.109-8", firmado = true, horaFirma = "10:15", orden = 1, far = false),
            PersonalEjecutanteEntity(asaaId = asaa2Id, nombre = "Valentina Rivas Gómez", rut = "20.551.782-3", firmado = true, horaFirma = "10:16", orden = 2, far = false)
        )
        asaaDao.insertAsaaCompleto(asaa2, e2, p2)
    }
}

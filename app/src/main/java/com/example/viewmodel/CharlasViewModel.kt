package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.Asistente
import com.example.model.HistorialItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.Locale
import java.util.UUID

data class RegistroFormState(
    val docente: String = "",
    val asignatura: String = "",
    val seccion: String = "",
    val fecha: String = "",
    val horaInicio: String = "",
    val horaTermino: String = "",
    val tema: String = "",
    val tipoCharla: String = "Charla 5 minutos",
    val asistentes: List<Asistente> = listOf(Asistente(), Asistente(), Asistente()),
    val isCerrado: Boolean = false
)

class CharlasViewModel : ViewModel() {

    private val _historial = MutableStateFlow<List<HistorialItem>>(createInitialHistorial())
    val historial: StateFlow<List<HistorialItem>> = _historial.asStateFlow()

    private val _formState = MutableStateFlow(createInitialFormState())
    val formState: StateFlow<RegistroFormState> = _formState.asStateFlow()

    // Form Update methods
    fun updateDocente(value: String) {
        if (!_formState.value.isCerrado) {
            _formState.update { it.copy(docente = value) }
        }
    }

    fun updateAsignatura(value: String) {
        if (!_formState.value.isCerrado) {
            _formState.update { it.copy(asignatura = value) }
        }
    }

    fun updateSeccion(value: String) {
        if (!_formState.value.isCerrado) {
            _formState.update { it.copy(seccion = value) }
        }
    }

    fun updateFecha(value: String) {
        if (!_formState.value.isCerrado) {
            _formState.update { it.copy(fecha = value) }
        }
    }

    fun updateHoraInicio(value: String) {
        if (!_formState.value.isCerrado) {
            _formState.update { it.copy(horaInicio = value) }
        }
    }

    fun updateHoraTermino(value: String) {
        if (!_formState.value.isCerrado) {
            _formState.update { it.copy(horaTermino = value) }
        }
    }

    fun updateTema(value: String) {
        if (!_formState.value.isCerrado) {
            _formState.update { it.copy(tema = value) }
        }
    }

    fun updateTipoCharla(value: String) {
        if (!_formState.value.isCerrado) {
            _formState.update { it.copy(tipoCharla = value) }
        }
    }

    fun updateAsistente(index: Int, updated: Asistente) {
        if (!_formState.value.isCerrado) {
            _formState.update { current ->
                val list = current.asistentes.toMutableList()
                if (index in list.indices) {
                    list[index] = updated
                }
                current.copy(asistentes = list)
            }
        }
    }

    fun agregarAsistente() {
        if (!_formState.value.isCerrado) {
            _formState.update { current ->
                current.copy(asistentes = current.asistentes + Asistente())
            }
        }
    }

    fun eliminarAsistente(index: Int) {
        if (!_formState.value.isCerrado && _formState.value.asistentes.size > 1) {
            _formState.update { current ->
                val list = current.asistentes.toMutableList()
                if (index in list.indices) {
                    list.removeAt(index)
                }
                current.copy(asistentes = list)
            }
        }
    }

    fun guardarRegistro(): HistorialItem {
        val current = _formState.value
        val firmadosCount = current.asistentes.count { it.firmado }
        val totalAsistentes = current.asistentes.size

        val personasList = mutableListOf<String>()
        if (current.docente.isNotBlank()) personasList.add(current.docente)
        current.asistentes.forEach { a ->
            if (a.nombre.isNotBlank()) personasList.add(a.nombre)
        }

        // Newly saved registration is created as ⏳ Pendiente de sincronizar (sincronizado = false)
        val nuevoItem = HistorialItem(
            id = UUID.randomUUID().toString(),
            fecha = current.fecha.ifBlank { getFormattedDate() },
            docente = current.docente.ifBlank { "Docente no especificado" },
            asignatura = current.asignatura.ifBlank { "Sin asignatura" },
            seccion = current.seccion.ifBlank { "S/S" },
            tipo = current.tipoCharla,
            numeroAsistentes = totalAsistentes,
            asistentesFirmados = firmadosCount,
            tema = current.tema.ifBlank { "Charla de Seguridad" },
            personas = personasList,
            asistentes = current.asistentes.map { it.copy() },
            sincronizado = false // Requirement 5: aparece como "⏳ Pendiente de sincronizar"
        )

        // Automatically add to Historial list
        _historial.update { listOf(nuevoItem) + it }

        // Lock the form
        _formState.update { it.copy(isCerrado = true) }

        return nuevoItem
    }

    fun nuevoRegistro() {
        _formState.value = createInitialFormState()
    }

    companion object {
        fun getFormattedDate(): String {
            val cal = Calendar.getInstance()
            return String.format(
                Locale.getDefault(),
                "%02d/%02d/%d",
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR)
            )
        }

        fun getFormattedHoraInicio(): String {
            val cal = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = (cal.get(Calendar.MINUTE) / 5) * 5
            return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        }

        fun getFormattedHoraTermino(): String {
            val cal = Calendar.getInstance()
            val hour = (cal.get(Calendar.HOUR_OF_DAY) + 1) % 24
            val minute = (cal.get(Calendar.MINUTE) / 5) * 5
            return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        }

        fun createInitialFormState(): RegistroFormState {
            return RegistroFormState(
                docente = "",
                asignatura = "",
                seccion = "",
                fecha = getFormattedDate(),
                horaInicio = getFormattedHoraInicio(),
                horaTermino = getFormattedHoraTermino(),
                tema = "",
                tipoCharla = "Charla 5 minutos",
                asistentes = listOf(Asistente(), Asistente(), Asistente()),
                isCerrado = false
            )
        }

        fun createInitialHistorial(): List<HistorialItem> {
            return listOf(
                HistorialItem(
                    id = "1",
                    fecha = "02/10/2026",
                    docente = "Carlos Silva Rojas",
                    asignatura = "Taller de Mantenimiento Electromecánico",
                    seccion = "002D",
                    tipo = "Charla 5 minutos",
                    numeroAsistentes = 4,
                    asistentesFirmados = 4,
                    tema = "Inspección previa de herramientas eléctricas y bloqueo LOTO",
                    personas = listOf("Carlos Silva Rojas", "Juan Morales Carrasco", "Felipe Carrasco Vidal", "Daniela Vega Poblete", "Ignacio Reyes Pinto"),
                    asistentes = listOf(
                        Asistente(nombre = "Juan Morales Carrasco", rut = "19.845.210-4", firmado = true, observacion = "EPP completo"),
                        Asistente(nombre = "Felipe Carrasco Vidal", rut = "20.114.892-7", firmado = true, observacion = "Sin observaciones"),
                        Asistente(nombre = "Daniela Vega Poblete", rut = "19.531.004-K", firmado = true, observacion = "Verificación multímetro OK"),
                        Asistente(nombre = "Ignacio Reyes Pinto", rut = "20.301.442-1", firmado = true, observacion = "EPP dieléctrico verificado")
                    ),
                    sincronizado = true // Sincronizado
                ),
                HistorialItem(
                    id = "2",
                    fecha = "29/09/2026",
                    docente = "Marcela Pardo Fuentes",
                    asignatura = "Automatización y Robótica Industrial",
                    seccion = "001D",
                    tipo = "Charla Integral",
                    numeroAsistentes = 4,
                    asistentesFirmados = 4,
                    tema = "Paradas de emergencia y protocolos en celdas robotizadas",
                    personas = listOf("Marcela Pardo Fuentes", "Ignacio Soto Palma", "Valentina Rivas Gómez", "Matías Alarcón Leal", "Camila Torres Muñoz"),
                    asistentes = listOf(
                        Asistente(nombre = "Ignacio Soto Palma", rut = "19.442.109-8", firmado = true, observacion = "Uso de calzado de seguridad"),
                        Asistente(nombre = "Valentina Rivas Gómez", rut = "20.551.782-3", firmado = true, observacion = "Sin novedades"),
                        Asistente(nombre = "Matías Alarcón Leal", rut = "19.980.231-5", firmado = true, observacion = "Barreras ópticas revisadas"),
                        Asistente(nombre = "Camila Torres Muñoz", rut = "20.123.456-0", firmado = true, observacion = "Sin novedades")
                    ),
                    sincronizado = false // ⏳ Pendiente de sincronizar
                ),
                HistorialItem(
                    id = "3",
                    fecha = "25/09/2026",
                    docente = "Héctor Garrido Vera",
                    asignatura = "Sistemas de Frenos y Dirección",
                    seccion = "003V",
                    tipo = "Reinstrucción",
                    numeroAsistentes = 4,
                    asistentesFirmados = 4,
                    tema = "Riesgos en el uso de elevadores hidráulicos y fosas de inspección",
                    personas = listOf("Héctor Garrido Vera", "Sebastián Muñoz Rivas", "Lucas Henríquez Silva", "Javier Castro Pino", "Nicolás Peña Vera"),
                    asistentes = listOf(
                        Asistente(nombre = "Sebastián Muñoz Rivas", rut = "18.994.512-1", firmado = true, observacion = "Traba mecánica verificada"),
                        Asistente(nombre = "Lucas Henríquez Silva", rut = "19.231.874-9", firmado = true, observacion = "Sin observaciones"),
                        Asistente(nombre = "Javier Castro Pino", rut = "20.004.112-6", firmado = true, observacion = "Gafas de seguridad puestas"),
                        Asistente(nombre = "Nicolás Peña Vera", rut = "19.782.339-K", firmado = true, observacion = "Sin observaciones")
                    ),
                    sincronizado = true // Sincronizado
                ),
                HistorialItem(
                    id = "4",
                    fecha = "18/09/2026",
                    docente = "Andrea Navarrete Soto",
                    asignatura = "Prevención de Riesgos en Minería y Construcción",
                    seccion = "001V",
                    tipo = "Charla Externa",
                    numeroAsistentes = 4,
                    asistentesFirmados = 4,
                    tema = "Procedimientos de trabajo seguro en excavaciones y taludes",
                    personas = listOf("Andrea Navarrete Soto", "Camilo Ortiz Durán", "Sofía Valenzuela Parra", "Pablo Pinto Sanhueza", "Constanza Morales"),
                    asistentes = listOf(
                        Asistente(nombre = "Camilo Ortiz Durán", rut = "18.774.290-3", firmado = true, observacion = "Casco barboquejo OK"),
                        Asistente(nombre = "Sofía Valenzuela Parra", rut = "20.401.882-7", firmado = true, observacion = "Sin observaciones"),
                        Asistente(nombre = "Pablo Pinto Sanhueza", rut = "19.664.120-2", firmado = true, observacion = "Chaleco reflectante OK"),
                        Asistente(nombre = "Constanza Morales", rut = "20.198.441-5", firmado = true, observacion = "Sin novedades")
                    ),
                    sincronizado = false // ⏳ Pendiente de sincronizar
                ),
                HistorialItem(
                    id = "5",
                    fecha = "11/09/2026",
                    docente = "Rodrigo Morales Castillo",
                    asignatura = "Laboratorio de Ensayos No Destructivos",
                    seccion = "002D",
                    tipo = "Charla Interna",
                    numeroAsistentes = 4,
                    asistentesFirmados = 4,
                    tema = "Protección radiológica y manejo seguro de líquidos penetrantes",
                    personas = listOf("Rodrigo Morales Castillo", "Constanza Bravo Jara", "Diego Salgado Flores", "Esteban Reyes Vidal", "Francisca Lara"),
                    asistentes = listOf(
                        Asistente(nombre = "Constanza Bravo Jara", rut = "19.345.109-7", firmado = true, observacion = "Guantes de nitrilo"),
                        Asistente(nombre = "Diego Salgado Flores", rut = "20.211.890-4", firmado = true, observacion = "Campana de extracción activa"),
                        Asistente(nombre = "Esteban Reyes Vidal", rut = "19.892.331-2", firmado = true, observacion = "Sin observaciones"),
                        Asistente(nombre = "Francisca Lara", rut = "20.089.123-K", firmado = true, observacion = "Sin novedades")
                    ),
                    sincronizado = true // Sincronizado
                )
            )
        }
    }
}

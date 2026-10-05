package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.InacapApp
import com.example.data.local.entities.AsistenteEntity
import com.example.data.local.entities.CharlaEntity
import com.example.model.Asistente
import com.example.model.HistorialItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

class CharlasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as InacapApp).repository

    // Reactive Flow from Room Database
    val historial: StateFlow<List<HistorialItem>> = repository.todasLasCharlas
        .map { list ->
            list.map { c ->
                val personasList = mutableListOf<String>()
                if (c.charla.docente.isNotBlank()) personasList.add(c.charla.docente)
                c.asistentes.forEach { a ->
                    if (a.nombre.isNotBlank()) personasList.add(a.nombre)
                }

                HistorialItem(
                    id = c.charla.id,
                    fecha = c.charla.fecha,
                    docente = c.charla.docente,
                    asignatura = c.charla.asignatura,
                    seccion = c.charla.seccion,
                    tipo = c.charla.tipoCharla,
                    numeroAsistentes = c.asistentes.size,
                    asistentesFirmados = c.asistentes.count { it.firmado },
                    tema = c.charla.tema,
                    personas = personasList,
                    asistentes = c.asistentes.map {
                        Asistente(
                            id = it.id,
                            nombre = it.nombre,
                            rut = it.rut,
                            firmado = it.firmado,
                            observacion = it.observacion
                        )
                    },
                    sincronizado = c.charla.sincronizado
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _formState = MutableStateFlow(createInitialFormState())
    val formState: StateFlow<RegistroFormState> = _formState.asStateFlow()

    // Strict validation: if isCerrado == true, reject updates
    fun updateDocente(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(docente = value) }
    }

    fun updateAsignatura(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(asignatura = value) }
    }

    fun updateSeccion(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(seccion = value) }
    }

    fun updateFecha(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(fecha = value) }
    }

    fun updateHoraInicio(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(horaInicio = value) }
    }

    fun updateHoraTermino(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(horaTermino = value) }
    }

    fun updateTema(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(tema = value) }
    }

    fun updateTipoCharla(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(tipoCharla = value) }
    }

    fun updateAsistente(index: Int, updated: Asistente) {
        if (_formState.value.isCerrado) return
        _formState.update { current ->
            val list = current.asistentes.toMutableList()
            if (index in list.indices) {
                list[index] = updated
            }
            current.copy(asistentes = list)
        }
    }

    fun agregarAsistente() {
        if (_formState.value.isCerrado) return
        _formState.update { current ->
            current.copy(asistentes = current.asistentes + Asistente())
        }
    }

    fun eliminarAsistente(index: Int) {
        if (_formState.value.isCerrado || _formState.value.asistentes.size <= 1) return
        _formState.update { current ->
            val list = current.asistentes.toMutableList()
            if (index in list.indices) {
                list.removeAt(index)
            }
            current.copy(asistentes = list)
        }
    }

    fun guardarRegistro() {
        if (_formState.value.isCerrado) return

        val current = _formState.value
        val charlaId = UUID.randomUUID().toString()

        val charlaEntity = CharlaEntity(
            id = charlaId,
            docente = current.docente.ifBlank { "Docente no especificado" },
            asignatura = current.asignatura.ifBlank { "Sin asignatura" },
            seccion = current.seccion.ifBlank { "S/S" },
            fecha = current.fecha.ifBlank { getFormattedDate() },
            horaInicio = current.horaInicio.ifBlank { getFormattedHoraInicio() },
            horaTermino = current.horaTermino.ifBlank { getFormattedHoraTermino() },
            tema = current.tema.ifBlank { "Charla de Seguridad" },
            tipoCharla = current.tipoCharla,
            cerrada = true,        // Bloqueada
            sincronizado = false,  // ⏳ Pendiente de sincronizar
            timestamp = System.currentTimeMillis()
        )

        val asistentesEntities = current.asistentes.mapIndexed { index, a ->
            AsistenteEntity(
                id = UUID.randomUUID().toString(),
                charlaId = charlaId,
                nombre = a.nombre,
                rut = a.rut,
                firmado = a.firmado,
                observacion = a.observacion,
                orden = index + 1
            )
        }

        // Lock form state in ViewModel
        _formState.update { it.copy(isCerrado = true) }

        // Persist in Room Database asynchronously
        viewModelScope.launch {
            repository.guardarCharlaCompleta(charlaEntity, asistentesEntities)
        }
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
    }
}

package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.InacapApp
import com.example.data.local.entities.AsistenteEntity
import com.example.data.local.entities.CharlaEntity
import com.example.model.Asistente
import com.example.model.HistorialItem
import kotlinx.coroutines.delay
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

sealed interface ValidacionCharlaResultado {
    object ValidoParaGuardar : ValidacionCharlaResultado
    data class Error(val mensaje: String, val campoDocente: Boolean = false, val campoTema: Boolean = false) : ValidacionCharlaResultado
    data class FirmasPendientes(val total: Int, val sinFirma: Int) : ValidacionCharlaResultado
}

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
    val isCerrado: Boolean = false,
    val docenteError: String? = null,
    val temaError: String? = null
)

class CharlasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as InacapApp).repository

    // Simulated Connection State
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

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
                            horaFirma = it.horaFirma,
                            observacion = it.observacion,
                            esExterno = it.esExterno,
                            procedencia = it.procedencia
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

    fun toggleConnection(connected: Boolean, onSyncComplete: (Int) -> Unit = {}) {
        _isOnline.value = connected
        if (connected) {
            simularSincronizacion(onSyncComplete)
        }
    }

    private fun simularSincronizacion(onSyncComplete: (Int) -> Unit) {
        viewModelScope.launch {
            if (_isSyncing.value) return@launch
            _isSyncing.value = true

            val charlasPendientes = repository.getCharlasPendientes()
            val artsPendientes = repository.getArtsPendientes()
            val totalPendientes = charlasPendientes.size + artsPendientes.size

            if (totalPendientes > 0) {
                // Sincronizar charlas de a una con animación / retardo de ~600ms
                for (charla in charlasPendientes) {
                    delay(600)
                    repository.sincronizarCharla(charla.id)
                }

                // Sincronizar arts de a uno
                for (art in artsPendientes) {
                    delay(600)
                    repository.sincronizarArt(art.id)
                }
            }

            _isSyncing.value = false
            onSyncComplete(totalPendientes)
        }
    }

    // Form Update methods with lock check and clearing error
    fun updateDocente(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(docente = value, docenteError = null) }
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
        _formState.update { it.copy(tema = value, temaError = null) }
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

    fun toggleFirmaAsistente(index: Int) {
        if (_formState.value.isCerrado) return
        _formState.update { current ->
            val list = current.asistentes.toMutableList()
            if (index in list.indices) {
                val a = list[index]
                val nuevoFirmado = !a.firmado
                val hora = if (nuevoFirmado) {
                    val cal = Calendar.getInstance()
                    String.format(Locale.getDefault(), "%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                } else {
                    ""
                }
                list[index] = a.copy(firmado = nuevoFirmado, horaFirma = hora)
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

    // Validates before saving
    fun validarParaGuardar(): ValidacionCharlaResultado {
        val current = _formState.value

        if (current.docente.trim().isBlank()) {
            _formState.update { it.copy(docenteError = "Ingresa el nombre del docente o expositor") }
            return ValidacionCharlaResultado.Error("Falta ingresar el Docente o Expositor", campoDocente = true)
        }

        if (current.tema.trim().isBlank()) {
            _formState.update { it.copy(temaError = "Ingresa el tema tratado en la charla") }
            return ValidacionCharlaResultado.Error("Falta ingresar el Tema de la charla", campoTema = true)
        }

        // Filter valid attendees (ignoring completely blank rows)
        val validos = current.asistentes.filter { it.nombre.isNotBlank() || it.rut.isNotBlank() }
        if (validos.isEmpty()) {
            return ValidacionCharlaResultado.Error("Debes registrar al menos un asistente en la lista")
        }

        // Validate mandatory procedencia for external attendees
        val externoSinProcedencia = validos.find { it.esExterno && it.procedencia.trim().isBlank() }
        if (externoSinProcedencia != null) {
            val nombre = if (externoSinProcedencia.nombre.isNotBlank()) externoSinProcedencia.nombre else "asistente externo"
            return ValidacionCharlaResultado.Error("El $nombre tiene la opción 'Externo' activa pero falta indicar su Procedencia")
        }

        val sinFirmaCount = validos.count { !it.firmado }
        if (sinFirmaCount > 0) {
            return ValidacionCharlaResultado.FirmasPendientes(total = validos.size, sinFirma = sinFirmaCount)
        }

        return ValidacionCharlaResultado.ValidoParaGuardar
    }

    // Direct save when all are signed
    fun ejecutarGuardadoDirecto() {
        guardarActaInterno(marcarSinFirma = false)
    }

    // Save when user confirms closing with pending signatures
    fun ejecutarCierreConFirmasPendientes() {
        guardarActaInterno(marcarSinFirma = true)
    }

    private fun guardarActaInterno(marcarSinFirma: Boolean) {
        if (_formState.value.isCerrado) return

        val current = _formState.value
        // Only keep non-empty attendees
        val validos = current.asistentes.filter { it.nombre.isNotBlank() || it.rut.isNotBlank() }

        val asistentesFinales = validos.map { a ->
            if (!a.firmado && marcarSinFirma) {
                val obs = if (a.observacion.isBlank()) "Sin firma" else "${a.observacion} (Sin firma)"
                a.copy(observacion = obs)
            } else {
                a
            }
        }

        val charlaId = UUID.randomUUID().toString()

        val charlaEntity = CharlaEntity(
            id = charlaId,
            docente = current.docente.trim(),
            asignatura = current.asignatura.trim().ifBlank { "General" },
            seccion = current.seccion.trim().ifBlank { "S/S" },
            fecha = current.fecha.ifBlank { getFormattedDate() },
            horaInicio = current.horaInicio.ifBlank { getFormattedHoraInicio() },
            horaTermino = current.horaTermino.ifBlank { getFormattedHoraTermino() },
            tema = current.tema.trim(),
            tipoCharla = current.tipoCharla,
            cerrada = true,        // Cerrada
            sincronizado = false,  // ⏳ Pendiente de sincronizar
            timestamp = System.currentTimeMillis()
        )

        val asistentesEntities = asistentesFinales.mapIndexed { index, a ->
            AsistenteEntity(
                id = UUID.randomUUID().toString(),
                charlaId = charlaId,
                nombre = a.nombre.trim(),
                rut = a.rut.trim(),
                firmado = a.firmado,
                horaFirma = a.horaFirma,
                observacion = a.observacion.trim(),
                esExterno = a.esExterno,
                procedencia = a.procedencia.trim(),
                orden = index + 1
            )
        }

        // Lock form state in ViewModel
        _formState.update { it.copy(isCerrado = true, asistentes = asistentesFinales) }

        // Persist to Room
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
                isCerrado = false,
                docenteError = null,
                temaError = null
            )
        }
    }
}

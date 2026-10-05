package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.InacapApp
import com.example.data.local.entities.ArtConEtapas
import com.example.data.local.entities.ArtEntity
import com.example.data.local.entities.EtapaTrabajoEntity
import com.example.model.EtapaTrabajo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.util.UUID

data class ArtFormState(
    val trabajoActividad: String = "",
    val especialidad: String = "",
    val fecha: String = "",
    val docente: String = "",
    val lugar: String = "",
    val etapas: List<EtapaTrabajo> = listOf(EtapaTrabajo(), EtapaTrabajo(), EtapaTrabajo()),
    val responsableFirmado: Boolean = false,
    val isCerrado: Boolean = false
)

class ArtViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as InacapApp).repository

    // Reactive Flow of all ARTs from Room Database
    val historialArt: StateFlow<List<ArtConEtapas>> = repository.todosLosArts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _formState = MutableStateFlow(createInitialFormState())
    val formState: StateFlow<ArtFormState> = _formState.asStateFlow()

    // Strict validation: if isCerrado == true, reject updates in ViewModel
    fun updateTrabajoActividad(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(trabajoActividad = value) }
    }

    fun updateEspecialidad(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(especialidad = value) }
    }

    fun updateFecha(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(fecha = value) }
    }

    fun updateDocente(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(docente = value) }
    }

    fun updateLugar(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(lugar = value) }
    }

    fun updateEtapa(index: Int, updated: EtapaTrabajo) {
        if (_formState.value.isCerrado) return
        _formState.update { current ->
            val list = current.etapas.toMutableList()
            if (index in list.indices) {
                list[index] = updated
            }
            current.copy(etapas = list)
        }
    }

    fun agregarEtapa() {
        if (_formState.value.isCerrado) return
        _formState.update { current ->
            current.copy(etapas = current.etapas + EtapaTrabajo())
        }
    }

    fun eliminarEtapa(index: Int) {
        if (_formState.value.isCerrado || _formState.value.etapas.size <= 1) return
        _formState.update { current ->
            val list = current.etapas.toMutableList()
            if (index in list.indices) {
                list.removeAt(index)
            }
            current.copy(etapas = list)
        }
    }

    fun toggleResponsableFirma() {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(responsableFirmado = !it.responsableFirmado) }
    }

    fun guardarArt() {
        if (_formState.value.isCerrado) return

        val current = _formState.value
        val artId = UUID.randomUUID().toString()

        val artEntity = ArtEntity(
            id = artId,
            trabajoActividad = current.trabajoActividad.ifBlank { "Actividad sin especificar" },
            especialidad = current.especialidad.ifBlank { "Sin especialidad" },
            fecha = current.fecha.ifBlank { getFormattedDate() },
            docente = current.docente.ifBlank { "Docente no especificado" },
            lugar = current.lugar.ifBlank { "Taller general" },
            responsableFirmado = current.responsableFirmado,
            cerrada = true,        // Bloqueada
            sincronizado = false,  // ⏳ Pendiente de sincronizar
            timestamp = System.currentTimeMillis()
        )

        val etapasEntities = current.etapas.mapIndexed { index, e ->
            EtapaTrabajoEntity(
                id = UUID.randomUUID().toString(),
                artId = artId,
                etapa = e.etapa,
                riesgoAsociado = e.riesgoAsociado,
                medidaControl = e.medidaControl,
                orden = index + 1
            )
        }

        // Lock form state in ViewModel
        _formState.update { it.copy(isCerrado = true) }

        // Persist to Room
        viewModelScope.launch {
            repository.guardarArtCompleto(artEntity, etapasEntities)
        }
    }

    fun nuevoArt() {
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

        fun createInitialFormState(): ArtFormState {
            return ArtFormState(
                trabajoActividad = "",
                especialidad = "",
                fecha = getFormattedDate(),
                docente = "",
                lugar = "",
                etapas = listOf(EtapaTrabajo(), EtapaTrabajo(), EtapaTrabajo()),
                responsableFirmado = false,
                isCerrado = false
            )
        }
    }
}

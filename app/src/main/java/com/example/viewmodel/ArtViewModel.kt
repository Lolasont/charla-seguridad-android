package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.InacapApp
import com.example.data.local.entities.ArtConEtapas
import com.example.data.local.entities.ArtEntity
import com.example.data.local.entities.EtapaTrabajoEntity
import com.example.data.local.entities.PersonalEjecutanteEntity
import com.example.model.EtapaTrabajo
import com.example.model.PersonalEjecutante
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
    val tipoActividad: String = "Práctica", // "Teórica" | "Práctica"
    val nivelTecnico: String = "Inicial",   // "Inicial" | "Intermedio" | "Avanzado"
    val equipoApoyoSeleccionados: Set<String> = emptySet(),
    val equipoApoyoOtros: String = "",
    val eppSeleccionados: Set<String> = emptySet(),
    val eppOtros: String = "",
    val comandoVozSeleccionados: Set<String> = emptySet(),
    val condicionesAmbientalesSeleccionadas: Set<String> = emptySet(),
    val condicionesAmbientalesOtros: String = "",
    val personalEjecutante: List<PersonalEjecutante> = listOf(PersonalEjecutante(), PersonalEjecutante(), PersonalEjecutante()),
    val charlaSeguridadInicial: Boolean = false,
    val charlaVinculadaId: String? = null,
    val charlaVinculadaFecha: String? = null,
    val charlaVinculadaTema: String? = null,
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

    fun updateTipoActividad(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(tipoActividad = value) }
    }

    fun updateNivelTecnico(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(nivelTecnico = value) }
    }

    fun toggleEquipoApoyo(item: String) {
        if (_formState.value.isCerrado) return
        _formState.update { current ->
            val set = current.equipoApoyoSeleccionados.toMutableSet()
            if (set.contains(item)) set.remove(item) else set.add(item)
            current.copy(equipoApoyoSeleccionados = set)
        }
    }

    fun updateEquipoApoyoOtros(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(equipoApoyoOtros = value) }
    }

    fun toggleEpp(item: String) {
        if (_formState.value.isCerrado) return
        _formState.update { current ->
            val set = current.eppSeleccionados.toMutableSet()
            if (set.contains(item)) set.remove(item) else set.add(item)
            current.copy(eppSeleccionados = set)
        }
    }

    fun updateEppOtros(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(eppOtros = value) }
    }

    fun toggleComandoVoz(item: String) {
        if (_formState.value.isCerrado) return
        _formState.update { current ->
            val set = current.comandoVozSeleccionados.toMutableSet()
            if (set.contains(item)) set.remove(item) else set.add(item)
            current.copy(comandoVozSeleccionados = set)
        }
    }

    fun toggleCondicionAmbiental(item: String) {
        if (_formState.value.isCerrado) return
        _formState.update { current ->
            val set = current.condicionesAmbientalesSeleccionadas.toMutableSet()
            if (set.contains(item)) set.remove(item) else set.add(item)
            current.copy(condicionesAmbientalesSeleccionadas = set)
        }
    }

    fun updateCondicionesAmbientalesOtros(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(condicionesAmbientalesOtros = value) }
    }

    fun updateCharlaSeguridadInicial(value: Boolean) {
        if (_formState.value.isCerrado) return
        _formState.update { current ->
            if (!value) {
                current.copy(charlaSeguridadInicial = false, charlaVinculadaId = null, charlaVinculadaFecha = null, charlaVinculadaTema = null)
            } else {
                current.copy(charlaSeguridadInicial = true)
            }
        }
    }

    fun vincularCharla(id: String, fecha: String, tema: String) {
        if (_formState.value.isCerrado) return
        _formState.update {
            it.copy(
                charlaSeguridadInicial = true,
                charlaVinculadaId = id,
                charlaVinculadaFecha = fecha,
                charlaVinculadaTema = tema
            )
        }
    }

    fun desvincularCharla() {
        if (_formState.value.isCerrado) return
        _formState.update {
            it.copy(
                charlaVinculadaId = null,
                charlaVinculadaFecha = null,
                charlaVinculadaTema = null
            )
        }
    }

    // Personal Ejecutante
    fun updatePersonalEjecutante(index: Int, updated: PersonalEjecutante) {
        if (_formState.value.isCerrado) return
        _formState.update { current ->
            val list = current.personalEjecutante.toMutableList()
            if (index in list.indices) {
                list[index] = updated
            }
            current.copy(personalEjecutante = list)
        }
    }

    fun toggleFirmaPersonalEjecutante(index: Int) {
        if (_formState.value.isCerrado) return
        _formState.update { current ->
            val list = current.personalEjecutante.toMutableList()
            if (index in list.indices) {
                val p = list[index]
                val nuevoFirmado = !p.firmado
                val hora = if (nuevoFirmado) {
                    val cal = Calendar.getInstance()
                    String.format(Locale.getDefault(), "%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                } else {
                    ""
                }
                list[index] = p.copy(firmado = nuevoFirmado, horaFirma = hora)
            }
            current.copy(personalEjecutante = list)
        }
    }

    fun agregarPersonalEjecutante() {
        if (_formState.value.isCerrado || _formState.value.personalEjecutante.size >= 13) return
        _formState.update { current ->
            current.copy(personalEjecutante = current.personalEjecutante + PersonalEjecutante())
        }
    }

    fun eliminarPersonalEjecutante(index: Int) {
        if (_formState.value.isCerrado || _formState.value.personalEjecutante.size <= 1) return
        _formState.update { current ->
            val list = current.personalEjecutante.toMutableList()
            if (index in list.indices) {
                list.removeAt(index)
            }
            current.copy(personalEjecutante = list)
        }
    }

    // Etapas
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

    // Duplicar ART anterior
    fun duplicarArtAnterior(): Boolean {
        val ultimo = historialArt.value.firstOrNull() ?: return false

        val equipoSet = ultimo.art.equipoApoyo.split("|").filter { it.isNotBlank() }.toSet()
        val eppSet = ultimo.art.epp.split("|").filter { it.isNotBlank() }.toSet()
        val vozSet = ultimo.art.comandoVoz.split("|").filter { it.isNotBlank() }.toSet()
        val ambSet = ultimo.art.condicionesAmbientales.split("|").filter { it.isNotBlank() }.toSet()

        val etapasCopia = if (ultimo.etapas.isNotEmpty()) {
            ultimo.etapas.sortedBy { it.orden }.map {
                EtapaTrabajo(
                    etapa = it.etapa,
                    riesgoAsociado = it.riesgoAsociado,
                    medidaControl = it.medidaControl
                )
            }
        } else {
            listOf(EtapaTrabajo(), EtapaTrabajo())
        }

        val personalCopia = if (ultimo.personalEjecutante.isNotEmpty()) {
            ultimo.personalEjecutante.sortedBy { it.orden }.map {
                PersonalEjecutante(
                    nombre = it.nombre,
                    rut = it.rut,
                    firmado = false,
                    horaFirma = ""
                )
            }
        } else {
            listOf(PersonalEjecutante(), PersonalEjecutante(), PersonalEjecutante())
        }

        _formState.value = ArtFormState(
            trabajoActividad = ultimo.art.trabajoActividad,
            especialidad = ultimo.art.especialidad,
            fecha = getFormattedDate(), // Fecha pasa a hoy
            docente = ultimo.art.docente,
            lugar = ultimo.art.lugar,
            tipoActividad = ultimo.art.tipoActividad,
            nivelTecnico = ultimo.art.nivelTecnico,
            equipoApoyoSeleccionados = equipoSet,
            equipoApoyoOtros = ultimo.art.equipoApoyoOtros,
            eppSeleccionados = eppSet,
            eppOtros = ultimo.art.eppOtros,
            comandoVozSeleccionados = vozSet,
            condicionesAmbientalesSeleccionadas = ambSet,
            condicionesAmbientalesOtros = ultimo.art.condicionesAmbientalesOtros,
            charlaSeguridadInicial = ultimo.art.charlaSeguridadInicial,
            charlaVinculadaId = ultimo.art.charlaVinculadaId,
            charlaVinculadaFecha = ultimo.art.charlaVinculadaFecha,
            charlaVinculadaTema = ultimo.art.charlaVinculadaTema,
            personalEjecutante = personalCopia,
            etapas = etapasCopia,
            responsableFirmado = false, // Firmas en blanco
            isCerrado = false           // Formulario editable
        )
        return true
    }

    fun guardarArt() {
        if (_formState.value.isCerrado) return

        val current = _formState.value
        val artId = UUID.randomUUID().toString()

        val equipoStr = current.equipoApoyoSeleccionados.joinToString("|")
        val eppStr = current.eppSeleccionados.joinToString("|")
        val vozStr = current.comandoVozSeleccionados.joinToString("|")
        val ambStr = current.condicionesAmbientalesSeleccionadas.joinToString("|")

        val artEntity = ArtEntity(
            id = artId,
            trabajoActividad = current.trabajoActividad.trim().ifBlank { "Actividad sin especificar" },
            especialidad = current.especialidad.trim().ifBlank { "Sin especialidad" },
            fecha = current.fecha.ifBlank { getFormattedDate() },
            docente = current.docente.trim().ifBlank { "Docente no especificado" },
            lugar = current.lugar.trim().ifBlank { "Taller general" },
            tipoActividad = current.tipoActividad,
            nivelTecnico = current.nivelTecnico,
            equipoApoyo = equipoStr,
            equipoApoyoOtros = current.equipoApoyoOtros.trim(),
            epp = eppStr,
            eppOtros = current.eppOtros.trim(),
            comandoVoz = vozStr,
            condicionesAmbientales = ambStr,
            condicionesAmbientalesOtros = current.condicionesAmbientalesOtros.trim(),
            charlaSeguridadInicial = current.charlaSeguridadInicial,
            charlaVinculadaId = current.charlaVinculadaId,
            charlaVinculadaFecha = current.charlaVinculadaFecha,
            charlaVinculadaTema = current.charlaVinculadaTema,
            responsableFirmado = current.responsableFirmado,
            cerrada = true,        // Bloqueada
            sincronizado = false,  // ⏳ Pendiente de sincronizar
            timestamp = System.currentTimeMillis()
        )

        // Etapas válidas
        val etapasEntities = current.etapas.mapIndexed { index, e ->
            EtapaTrabajoEntity(
                id = UUID.randomUUID().toString(),
                artId = artId,
                etapa = e.etapa.trim(),
                riesgoAsociado = e.riesgoAsociado.trim(),
                medidaControl = e.medidaControl.trim(),
                orden = index + 1
            )
        }

        // Personal Ejecutante válido
        val validosPersonal = current.personalEjecutante.filter { it.nombre.isNotBlank() || it.rut.isNotBlank() }
        val personalEntities = validosPersonal.mapIndexed { index, p ->
            PersonalEjecutanteEntity(
                id = UUID.randomUUID().toString(),
                artId = artId,
                nombre = p.nombre.trim(),
                rut = p.rut.trim(),
                firmado = p.firmado,
                horaFirma = p.horaFirma,
                orden = index + 1
            )
        }

        // Lock form state in ViewModel
        _formState.update { it.copy(isCerrado = true) }

        // Persist to Room
        viewModelScope.launch {
            repository.guardarArtCompleto(artEntity, etapasEntities, personalEntities)
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
                tipoActividad = "Práctica",
                nivelTecnico = "Inicial",
                equipoApoyoSeleccionados = emptySet(),
                equipoApoyoOtros = "",
                eppSeleccionados = emptySet(),
                eppOtros = "",
                comandoVozSeleccionados = emptySet(),
                condicionesAmbientalesSeleccionadas = emptySet(),
                condicionesAmbientalesOtros = "",
                personalEjecutante = listOf(PersonalEjecutante(), PersonalEjecutante(), PersonalEjecutante()),
                charlaSeguridadInicial = false,
                charlaVinculadaId = null,
                charlaVinculadaFecha = null,
                charlaVinculadaTema = null,
                etapas = listOf(EtapaTrabajo(), EtapaTrabajo(), EtapaTrabajo()),
                responsableFirmado = false,
                isCerrado = false
            )
        }
    }
}

package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.InacapApp
import com.example.data.local.entities.AsaaConEtapas
import com.example.data.local.entities.AsaaEntity
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

data class AsaaFormState(
    val trabajoActividad: String = "",
    val especialidad: String = "",
    val fecha: String = "",
    val docente: String = "",
    val lugar: String = "",
    val asignatura: String = "",
    val seccion: String = "",
    val horaInicio: String = "",
    val horaTermino: String = "",
    val tipoCharla: String = "Charla 5 minutos",
    val tema: String = "",
    val tipoActividad: String = "Práctica", // "Teórica" | "Práctica"
    val nivelTecnico: String = "Inicial",   // "Inicial" | "Intermedio" | "Avanzado"
    val equipoApoyoSeleccionados: Set<String> = emptySet(),
    val equipoApoyoOtros: String = "",
    val eppSeleccionados: Set<String> = emptySet(),
    val eppOtros: String = "",
    val comandoVozSeleccionados: Set<String> = emptySet(),
    val condicionesAmbientalesSeleccionadas: Set<String> = emptySet(),
    val condicionesAmbientalesOtros: String = "",
    val medidasControlSeleccionadas: Set<String> = emptySet(),
    val medidasControlOtros: String = "",
    val loto: Boolean = false,
    val asaaComprendido: Boolean = false,
    val observaciones: String = "",
    val hayFar: Boolean = false,
    val personalEjecutante: List<PersonalEjecutante> = listOf(PersonalEjecutante(), PersonalEjecutante(), PersonalEjecutante()),
    val charlaSeguridadInicial: Boolean = false,
    val charlaVinculadaId: String? = null,
    val charlaVinculadaFecha: String? = null,
    val charlaVinculadaTema: String? = null,
    val etapas: List<EtapaTrabajo> = listOf(EtapaTrabajo(), EtapaTrabajo(), EtapaTrabajo()),
    val responsableFirmado: Boolean = false,
    val isCerrado: Boolean = false
)

class AsaaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as InacapApp).repository

    // Reactive Flow of all ASAAs from Room Database
    val historialArt: StateFlow<List<AsaaConEtapas>> = repository.todosLosAsaas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _formState = MutableStateFlow(createInitialFormState())
    val formState: StateFlow<AsaaFormState> = _formState.asStateFlow()

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

    fun updateAsignatura(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(asignatura = value) }
    }

    fun updateSeccion(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(seccion = value) }
    }

    fun updateHoraInicio(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(horaInicio = value) }
    }

    fun updateHoraTermino(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(horaTermino = value) }
    }

    fun updateTipoCharla(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(tipoCharla = value) }
    }

    fun updateTema(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(tema = value) }
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

    fun toggleMedidaControl(item: String) {
        if (_formState.value.isCerrado) return
        _formState.update { current ->
            val set = current.medidasControlSeleccionadas.toMutableSet()
            if (set.contains(item)) set.remove(item) else set.add(item)
            current.copy(medidasControlSeleccionadas = set)
        }
    }

    fun updateMedidasControlOtros(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(medidasControlOtros = value) }
    }

    fun updateLoto(value: Boolean) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(loto = value) }
    }

    fun updateAsaaComprendido(value: Boolean) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(asaaComprendido = value) }
    }

    fun updateObservaciones(value: String) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(observaciones = value) }
    }

    fun updateHayFar(value: Boolean) {
        if (_formState.value.isCerrado) return
        _formState.update { it.copy(hayFar = value) }
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

    fun toggleFarPersonalEjecutante(index: Int) {
        if (_formState.value.isCerrado) return
        _formState.update { current ->
            val list = current.personalEjecutante.toMutableList()
            if (index in list.indices) {
                val p = list[index]
                list[index] = p.copy(far = !p.far)
            }
            val algunaFar = list.any { it.far }
            current.copy(personalEjecutante = list, hayFar = algunaFar)
        }
    }

    fun agregarPersonalEjecutante() {
        if (_formState.value.isCerrado) return
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
            val algunaFar = list.any { it.far }
            current.copy(personalEjecutante = list, hayFar = algunaFar)
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

    // Duplicar ASAA anterior
    fun duplicarArtAnterior(): Boolean {
        val ultimo = historialArt.value.firstOrNull() ?: return false

        val equipoSet = ultimo.asaa.equipoApoyo.split("|").filter { it.isNotBlank() }.toSet()
        val eppSet = ultimo.asaa.epp.split("|").filter { it.isNotBlank() }.toSet()
        val vozSet = ultimo.asaa.comandoVoz.split("|").filter { it.isNotBlank() }.toSet()
        val ambSet = ultimo.asaa.condicionesAmbientales.split("|").filter { it.isNotBlank() }.toSet()
        val medSet = ultimo.asaa.medidasControl.split("|").filter { it.isNotBlank() }.toSet()

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
                    horaFirma = "",
                    far = it.far
                )
            }
        } else {
            listOf(PersonalEjecutante(), PersonalEjecutante(), PersonalEjecutante())
        }

        _formState.value = AsaaFormState(
            trabajoActividad = ultimo.asaa.trabajoActividad,
            especialidad = ultimo.asaa.especialidad,
            fecha = getFormattedDate(), // Fecha pasa a hoy
            docente = ultimo.asaa.docente,
            lugar = ultimo.asaa.lugar,
            tipoActividad = ultimo.asaa.tipoActividad,
            nivelTecnico = ultimo.asaa.nivelTecnico,
            equipoApoyoSeleccionados = equipoSet,
            equipoApoyoOtros = ultimo.asaa.equipoApoyoOtros,
            eppSeleccionados = eppSet,
            eppOtros = ultimo.asaa.eppOtros,
            comandoVozSeleccionados = vozSet,
            condicionesAmbientalesSeleccionadas = ambSet,
            condicionesAmbientalesOtros = ultimo.asaa.condicionesAmbientalesOtros,
            medidasControlSeleccionadas = medSet,
            medidasControlOtros = ultimo.asaa.medidasControlOtros,
            loto = ultimo.asaa.loto,
            asaaComprendido = ultimo.asaa.asaaComprendido,
            observaciones = ultimo.asaa.observaciones,
            hayFar = ultimo.asaa.hayFar,
            charlaSeguridadInicial = ultimo.asaa.charlaSeguridadInicial,
            charlaVinculadaId = ultimo.asaa.charlaVinculadaId,
            charlaVinculadaFecha = ultimo.asaa.charlaVinculadaFecha,
            charlaVinculadaTema = ultimo.asaa.charlaVinculadaTema,
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
        val asaaId = UUID.randomUUID().toString()

        val equipoStr = current.equipoApoyoSeleccionados.joinToString("|")
        val eppStr = current.eppSeleccionados.joinToString("|")
        val vozStr = current.comandoVozSeleccionados.joinToString("|")
        val ambStr = current.condicionesAmbientalesSeleccionadas.joinToString("|")
        val medStr = current.medidasControlSeleccionadas.joinToString("|")

        val asaaEntity = AsaaEntity(
            id = asaaId,
            trabajoActividad = current.trabajoActividad.trim().ifBlank { "NA" },
            especialidad = current.especialidad.trim().ifBlank { "NA" },
            fecha = current.fecha.ifBlank { getFormattedDate() },
            docente = current.docente.trim().ifBlank { "NA" },
            lugar = current.lugar.trim().ifBlank { "NA" },
            asignatura = current.asignatura.trim(),
            seccion = current.seccion.trim(),
            horaInicio = current.horaInicio,
            horaTermino = current.horaTermino,
            tipoCharla = current.tipoCharla,
            tema = current.tema.trim(),
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
            medidasControl = medStr,
            medidasControlOtros = current.medidasControlOtros.trim(),
            loto = current.loto,
            asaaComprendido = current.asaaComprendido,
            observaciones = current.observaciones.trim(),
            hayFar = current.hayFar,
            responsableFirmado = current.responsableFirmado,
            cerrada = true,        // Bloqueada
            sincronizado = false,  // ⏳ Pendiente de sincronizar
            timestamp = System.currentTimeMillis()
        )

        // Etapas válidas
        val etapasEntities = current.etapas.mapIndexed { index, e ->
            EtapaTrabajoEntity(
                id = UUID.randomUUID().toString(),
                asaaId = asaaId,
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
                asaaId = asaaId,
                nombre = p.nombre.trim(),
                rut = p.rut.trim(),
                firmado = p.firmado,
                horaFirma = p.horaFirma,
                orden = index + 1,
                far = p.far
            )
        }

        // Lock form state in ViewModel
        _formState.update { it.copy(isCerrado = true) }

        // Persist to Room
        viewModelScope.launch {
            repository.guardarAsaaCompleto(asaaEntity, etapasEntities, personalEntities)
        }
    }

    fun actualizarAsaaExistente(
        asaaId: String,
        trabajoActividad: String,
        especialidad: String,
        fecha: String,
        docente: String,
        lugar: String,
        tipoActividad: String,
        nivelTecnico: String,
        equipoApoyo: String,
        equipoApoyoOtros: String,
        epp: String,
        eppOtros: String,
        condicionesAmbientales: String,
        condicionesAmbientalesOtros: String,
        medidasControl: String,
        medidasControlOtros: String,
        loto: Boolean,
        asaaComprendido: Boolean,
        observaciones: String,
        hayFar: Boolean,
        charlaSeguridadInicial: Boolean,
        responsableFirmado: Boolean,
        etapas: List<EtapaTrabajoEntity>,
        personal: List<PersonalEjecutanteEntity>
    ) {
        val asaaEntity = AsaaEntity(
            id = asaaId,
            trabajoActividad = trabajoActividad.trim().ifBlank { "NA" },
            especialidad = especialidad.trim().ifBlank { "NA" },
            fecha = fecha.ifBlank { getFormattedDate() },
            docente = docente.trim().ifBlank { "NA" },
            lugar = lugar.trim().ifBlank { "NA" },
            tipoActividad = tipoActividad,
            nivelTecnico = nivelTecnico,
            equipoApoyo = equipoApoyo,
            equipoApoyoOtros = equipoApoyoOtros.trim(),
            epp = epp,
            eppOtros = eppOtros.trim(),
            comandoVoz = "",
            condicionesAmbientales = condicionesAmbientales,
            condicionesAmbientalesOtros = condicionesAmbientalesOtros.trim(),
            medidasControl = medidasControl,
            medidasControlOtros = medidasControlOtros.trim(),
            loto = loto,
            asaaComprendido = asaaComprendido,
            observaciones = observaciones.trim(),
            hayFar = hayFar,
            charlaSeguridadInicial = charlaSeguridadInicial,
            charlaVinculadaId = null,
            charlaVinculadaFecha = null,
            charlaVinculadaTema = null,
            responsableFirmado = responsableFirmado,
            cerrada = true,
            sincronizado = false,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.guardarAsaaCompleto(asaaEntity, etapas, personal)
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

        fun createInitialFormState(): AsaaFormState {
            return AsaaFormState(
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
                medidasControlSeleccionadas = emptySet(),
                medidasControlOtros = "",
                loto = false,
                asaaComprendido = false,
                observaciones = "",
                hayFar = false,
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

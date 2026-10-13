package com.example.charlasinacap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.charlasinacap.data.InacapRepository
import com.example.charlasinacap.data.local.entity.*
import com.example.charlasinacap.model.AsaaCatalogos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AsaaViewModel(
    private val repository: InacapRepository
) : ViewModel() {

    // UI State fields
    val trabajoActividad = MutableStateFlow("")
    val especialidad = MutableStateFlow("")
    val fecha = MutableStateFlow("")
    val tipoActividad = MutableStateFlow("")
    val docente = MutableStateFlow("")
    val ubicacion = MutableStateFlow("")

    val equipamientoChecked = MutableStateFlow(
        AsaaCatalogos.equiposApoyo.map { false }.toMutableList()
    )
    val eppChecked = MutableStateFlow(
        AsaaCatalogos.epp.map { false }.toMutableList()
    )
    val condicionesChecked = MutableStateFlow(
        AsaaCatalogos.condicionesAmbientales.map { false }.toMutableList()
    )
    val medidasChecked = MutableStateFlow(
        AsaaCatalogos.medidasControl.map { false }.toMutableList()
    )

    val nivelInicial = MutableStateFlow(false)
    val nivelIntermedio = MutableStateFlow(false)
    val nivelAvanzado = MutableStateFlow(false)

    val charlaInicial = MutableStateFlow(false)
    val asaaComprendido = MutableStateFlow(false)
    val loto = MutableStateFlow(false)

    val personal = MutableStateFlow(List(12) { "" })

    data class Etapa(var descripcion: String = "", var riesgos: String = "", var medidas: String = "")
    val etapas = MutableStateFlow(List(6) { Etapa() })

    fun saveAsaa() {
        viewModelScope.launch {
            // ---- Validaciones obligatorias (NA) ----
            fun ensure(value: String): String = if (value.isBlank()) "NA" else value
            val asaa = AsaaEntity(
                trabajoActividad = ensure(trabajoActividad.value),
                especialidad = ensure(especialidad.value),
                fecha = ensure(fecha.value),
                tipoActividad = ensure(tipoActividad.value),
                docente = ensure(docente.value),
                ubicacion = ensure(ubicacion.value),
                nivelInicial = nivelInicial.value,
                nivelIntermedio = nivelIntermedio.value,
                nivelAvanzado = nivelAvanzado.value,
                charlaInicial = charlaInicial.value,
                asaaComprendido = asaaComprendido.value,
                loto = loto.value,
                cerrado = false
            )
            val equipos = AsaaCatalogos.equiposApoyo.mapIndexed { idx, nombre ->
                AsaaEquipoApoyoEntity(
                    asaaId = 0L,
                    nombre = nombre,
                    seleccionado = equipamientoChecked.value[idx]
                )
            }
            val epps = AsaaCatalogos.epp.mapIndexed { idx, nombre ->
                EppEntity(
                    asaaId = 0L,
                    nombre = nombre,
                    seleccionado = eppChecked.value[idx]
                )
            }
            val condiciones = AsaaCatalogos.condicionesAmbientales.mapIndexed { idx, nombre ->
                CondicionAmbientalEntity(
                    asaaId = 0L,
                    nombre = nombre,
                    seleccionado = condicionesChecked.value[idx]
                )
            }
            val medidas = AsaaCatalogos.medidasControl.mapIndexed { idx, nombre ->
                MedidaControlEntity(
                    asaaId = 0L,
                    nombre = nombre,
                    seleccionado = medidasChecked.value[idx]
                )
            }
            val personalEnt = personal.value.map { PersonalEjecutanteEntity(asaaId = 0L, nombre = it) }
            val etapasEnt = etapas.value.map { etapa ->
                EtapaEntity(
                    asaaId = 0L,
                    descripcion = etapa.descripcion,
                    riesgos = etapa.riesgos,
                    medidas = etapa.medidas
                )
            }
            repository.saveAsaa(asaa, equipos, epps, condiciones, medidas, personalEnt, etapasEnt)
        }
    }

    fun duplicateLastAsaa() {
        viewModelScope.launch {
            repository.duplicateLastAsaa()
        }
    }

    fun exportAsaa() {
        viewModelScope.launch {
            val latest = repository.getLatestAsaa() ?: return@launch
            // Exportar a PDF y Excel (implementaciones reales en ExportHelper)
            com.example.charlasinacap.util.export.ExportHelper.exportAsaaPdf(com.example.charlasinacap.App.instance, latest.id)
            com.example.charlasinacap.util.export.ExportHelper.exportAsaaExcel(com.example.charlasinacap.App.instance, latest.id)
        }
    }

}

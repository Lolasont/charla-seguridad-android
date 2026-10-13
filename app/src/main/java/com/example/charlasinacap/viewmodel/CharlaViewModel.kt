package com.example.charlasinacap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.charlasinacap.data.InacapRepository
import com.example.charlasinacap.data.local.entity.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CharlaViewModel(
    private val repository: InacapRepository
) : ViewModel() {

    val docente = MutableStateFlow("")
    val asignatura = MutableStateFlow("")
    val seccion = MutableStateFlow("")
    val horaInicio = MutableStateFlow("")
    val horaTermino = MutableStateFlow("")
    val fecha = MutableStateFlow("")
    val tema = MutableStateFlow("")
    val tipoCharla = MutableStateFlow(CharlaTipo.CINCO_MINUTOS)

    data class AsistenciaRow(
        var nombre: String = "",
        var rut: String = "",
        var firma: String = "",
        var observacion: String = ""
    )

    val asistencias = MutableStateFlow(List(24) { AsistenciaRow() })
    val comentarios = MutableStateFlow("")
    val responsableNombre = MutableStateFlow("")

    fun saveCharla() {
        viewModelScope.launch {
            val charla = CharlaEntity(
                docente = docente.value,
                asignatura = asignatura.value,
                seccion = seccion.value,
                horaInicio = horaInicio.value,
                horaTermino = horaTermino.value,
                fecha = fecha.value,
                tema = tema.value,
                tipoCharla = tipoCharla.value.name
            )
            val asistenciaEnt = asistencias.value.map {
                AsistenciaEntity(
                    charlaId = 0L,
                    nombre = it.nombre,
                    rut = it.rut,
                    firma = it.firma,
                    observacion = it.observacion
                )
            }
            repository.saveCharla(charla, asistenciaEnt)
        }
    }
}

enum class CharlaTipo(val displayName: String) {
    CINCO_MINUTOS("Charla 5 MINUTOS"),
    INTEGRAL("Charla Integral"),
    EXTERNA("Charla Externa"),
    INTERNA("Charla Interna"),
    REINSTRUIR("Reinstrucción")
}

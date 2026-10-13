package com.example.charlasinacap.util.export

import android.content.Context
import com.example.charlasinacap.data.InacapRepository
import com.example.charlasinacap.data.local.entity.*
import java.io.File
import java.io.FileWriter

object ExportHelper {
    suspend fun exportAsaaCsv(context: Context, asaaId: Long) {
        val repo = InacapRepository.getInstance(context)
        val asaa = repo.getLatestAsaa() ?: return
        val file = File(context.getExternalFilesDir(null), "ASAA_${asaaId}.csv")
        FileWriter(file, Charsets.UTF_8).use { writer ->
            writer.appendLine("TrabajoActividad,Especialidad,Fecha,TipoActividad,Docente,Ubicacion")
            writer.appendLine(
                listOf(
                    asaa.trabajoActividad,
                    asaa.especialidad,
                    asaa.fecha,
                    asaa.tipoActividad,
                    asaa.docente,
                    asaa.ubicacion
                ).joinToString(",")
            )
        }
    }

    suspend fun exportCharlaCsv(context: Context, charlaId: Long) {
        val repo = InacapRepository.getInstance(context)
        val charla = repo.getLatestCharla() ?: return
        val file = File(context.getExternalFilesDir(null), "Charla_${charlaId}.csv")
        FileWriter(file, Charsets.UTF_8).use { writer ->
            writer.appendLine("Docente,Asignatura,Sección,HoraInicio,HoraTermino,Fecha,Tema,TipoCharla")
            writer.appendLine(
                listOf(
                    charla.docente,
                    charla.asignatura,
                    charla.seccion,
                    charla.horaInicio,
                    charla.horaTermino,
                    charla.fecha,
                    charla.tema,
                    charla.tipoCharla
                ).joinToString(",")
            )
        }
    }

    suspend fun exportAsaaPdf(context: Context, asaaId: Long) {
        // Stub simple PDF creation (real implementation would render the form)
        val file = File(context.getExternalFilesDir(null), "ASAA_${asaaId}.pdf")
        file.createNewFile()
    }

    suspend fun exportAsaaExcel(context: Context, asaaId: Long) {
        // Stub Excel creation (requires Apache POI dependency)
        val file = File(context.getExternalFilesDir(null), "ASAA_${asaaId}.xlsx")
        file.createNewFile()
    }

}

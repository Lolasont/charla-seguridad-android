package com.example.util.export

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.local.entities.ArtConEtapas
import com.example.model.HistorialItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportHelper {

    private fun getExportsDir(context: Context): File {
        val dir = File(context.cacheDir, "exports")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun getTimestampString(): String {
        return SimpleDateFormat("yyyy-MM-dd_HHmm", Locale.getDefault()).format(Date())
    }

    private fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(sendIntent, "Compartir $title con:")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun escapeCsv(value: String): String {
        var str = value.trim()
        val containsSpecial = str.contains(";") || str.contains("\"") || str.contains("\n") || str.contains("\r")
        if (str.contains("\"")) {
            str = str.replace("\"", "\"\"")
        }
        return if (containsSpecial) "\"$str\"" else str
    }

    // Word wrap helper by words without breaking words unless a single word exceeds maxWidth
    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val clean = text.trim()
        if (clean.isEmpty()) return emptyList()

        val lines = mutableListOf<String>()
        val paragraphs = clean.split("\n")

        for (para in paragraphs) {
            val words = para.split(" ").filter { it.isNotEmpty() }
            if (words.isEmpty()) {
                lines.add("")
                continue
            }

            var currentLine = ""
            for (word in words) {
                val candidate = if (currentLine.isEmpty()) word else "$currentLine $word"
                if (paint.measureText(candidate) <= maxWidth) {
                    currentLine = candidate
                } else {
                    if (currentLine.isNotEmpty()) {
                        lines.add(currentLine)
                        currentLine = ""
                    }
                    if (paint.measureText(word) > maxWidth) {
                        var remaining = word
                        while (remaining.isNotEmpty()) {
                            val count = paint.breakText(remaining, true, maxWidth, null)
                            if (count > 0) {
                                lines.add(remaining.substring(0, count))
                                remaining = remaining.substring(count)
                            } else {
                                lines.add(remaining)
                                break
                            }
                        }
                    } else {
                        currentLine = word
                    }
                }
            }
            if (currentLine.isNotEmpty()) {
                lines.add(currentLine)
            }
        }
        return lines
    }

    private fun drawLines(
        canvas: Canvas,
        lines: List<String>,
        x: Float,
        startY: Float,
        lineSpacing: Float,
        paint: Paint
    ) {
        lines.forEachIndexed { idx, line ->
            canvas.drawText(line, x, startY + idx * lineSpacing, paint)
        }
    }

    private fun formatRiesgoConNumero(riesgo: String): String {
        val clean = riesgo.trim()
        val map = mapOf(
            "Choque eléctrico" to "1 Choque eléctrico",
            "Arco eléctrico" to "2 Arco eléctrico",
            "Incendio" to "3 Incendio",
            "Ruido" to "4 Ruido",
            "Explosión" to "5 Explosión",
            "Corte" to "6 Corte",
            "Tropiezos" to "7 Tropiezos",
            "Contacto con partes activas" to "8 Contacto con partes activas",
            "Quemaduras" to "9 Quemaduras",
            "Fallas en aislamiento" to "10 Fallas en aislamiento",
            "Equipos defectuosos" to "11 Equipos defectuosos",
            "Sobrecargas" to "12 Sobrecargas",
            "Exposición al polvo" to "13 Exposición al polvo",
            "Desniveles" to "14 Desniveles",
            "Caída de materiales" to "15 Caída de materiales",
            "Trabajos en altura" to "16 Trabajos en altura",
            "Intoxicación" to "17 Intoxicación",
            "Radiación electromagnética" to "18 Radiación electromagnética",
            "Caída igual o distinto nivel" to "19 Caída igual o distinto nivel",
            "Proyección de partículas" to "20 Proyección de partículas",
            "Espacio confinado" to "21 Espacio confinado",
            "Golpeado por/contra/con" to "22 Golpeado por/contra/con",
            "Sustancias tóxicas" to "23 Sustancias tóxicas",
            "Ergonómico" to "24 Ergonómico",
            "Radiación láser" to "25 Radiación láser"
        )
        return map[clean] ?: clean
    }

    // =========================================================================
    // CHARLA - EXCEL (CSV)
    // =========================================================================
    suspend fun exportarCharlaCsv(context: Context, item: HistorialItem): File = withContext(Dispatchers.IO) {
        val fileName = "charla_${getTimestampString()}.csv"
        val file = File(getExportsDir(context), fileName)

        FileOutputStream(file).use { fos ->
            fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
            val sb = StringBuilder()

            sb.append("REGISTRO DE CHARLA DE SEGURIDAD INICIAL;;;\n")
            sb.append("INACAP Sede San Pedro de la Paz;;;\n\n")
            sb.append("Docente;").append(escapeCsv(item.docente)).append("\n")
            sb.append("Asignatura;").append(escapeCsv(item.asignatura)).append("\n")
            sb.append("Sección;").append(escapeCsv(item.seccion)).append("\n")
            sb.append("Fecha;").append(escapeCsv(item.fecha)).append("\n")
            sb.append("Tipo de Charla;").append(escapeCsv(item.tipo)).append("\n")
            sb.append("Tema;").append(escapeCsv(item.tema)).append("\n\n")

            sb.append("N°;Nombre;RUT;Firma;Hora Firma;Tipo Participante;Procedencia;Observación\n")

            val lista = if (item.asistentes.isNotEmpty()) {
                item.asistentes
            } else {
                item.personas.map { nombre ->
                    com.example.model.Asistente(nombre = nombre, firmado = true)
                }
            }

            lista.forEachIndexed { index, a ->
                val estadoFirma = if (a.firmado) "Firmado" else "Sin firma"
                val tipo = if (a.esExterno) "Externo" else "Interno"
                sb.append("${index + 1};")
                    .append(escapeCsv(a.nombre)).append(";")
                    .append(escapeCsv(a.rut)).append(";")
                    .append(escapeCsv(estadoFirma)).append(";")
                    .append(escapeCsv(a.horaFirma)).append(";")
                    .append(escapeCsv(tipo)).append(";")
                    .append(escapeCsv(a.procedencia)).append(";")
                    .append(escapeCsv(a.observacion)).append("\n")
            }

            fos.write(sb.toString().toByteArray(Charsets.UTF_8))
        }

        shareFile(context, file, "text/csv", fileName)
        file
    }

    // =========================================================================
    // ART - EXCEL (CSV)
    // =========================================================================
    suspend fun exportarArtCsv(context: Context, artConEtapas: ArtConEtapas): File = withContext(Dispatchers.IO) {
        val fileName = "art_${getTimestampString()}.csv"
        val file = File(getExportsDir(context), fileName)
        val art = artConEtapas.art
        val etapas = artConEtapas.etapas.sortedBy { it.orden }
        val personal = artConEtapas.personalEjecutante.sortedBy { it.orden }

        FileOutputStream(file).use { fos ->
            fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
            val sb = StringBuilder()

            sb.append("ANÁLISIS DE RIESGOS DEL TRABAJO (ART);;;;\n")
            sb.append("INACAP Sede San Pedro de la Paz;;;;\n\n")

            // 1. DATOS GENERALES
            sb.append("1. DATOS GENERALES;;;;\n")
            sb.append("Trabajo o Actividad;").append(escapeCsv(art.trabajoActividad)).append("\n")
            sb.append("Especialidad;").append(escapeCsv(art.especialidad)).append("\n")
            sb.append("Fecha;").append(escapeCsv(art.fecha)).append("\n")
            sb.append("Tipo de Actividad;").append(escapeCsv(art.tipoActividad)).append("\n")
            sb.append("Docente;").append(escapeCsv(art.docente)).append("\n")
            sb.append("Sala / Taller / Laboratorio / Terreno;").append(escapeCsv(art.lugar)).append("\n")
            sb.append("Nivel Técnico;").append(escapeCsv(art.nivelTecnico)).append("\n")
            sb.append("Charla de Seguridad Inicial;").append(if (art.charlaSeguridadInicial) "Sí" else "No").append("\n")
            val charlaVinculadaInfo = if (art.charlaVinculadaTema != null) "${art.charlaVinculadaFecha ?: ""}, ${art.charlaVinculadaTema}" else ""
            sb.append("Charla Vinculada;").append(escapeCsv(charlaVinculadaInfo)).append("\n\n")

            // 2. RECURSOS Y EPP
            sb.append("2. RECURSOS Y EPP;;;;\n")
            val equipoApoyoStr = buildString {
                if (art.equipoApoyo.isNotBlank()) append(art.equipoApoyo.replace("|", " | "))
                if (art.equipoApoyoOtros.isNotBlank()) {
                    if (isNotEmpty()) append(" | Otros: ") else append("Otros: ")
                    append(art.equipoApoyoOtros)
                }
            }
            sb.append("Equipo de Apoyo;").append(escapeCsv(equipoApoyoStr)).append("\n")

            val eppStr = buildString {
                if (art.epp.isNotBlank()) append(art.epp.replace("|", " | "))
                if (art.eppOtros.isNotBlank()) {
                    if (isNotEmpty()) append(" | Otros: ") else append("Otros: ")
                    append(art.eppOtros)
                }
            }
            sb.append("Equipo de Protección Personal;").append(escapeCsv(eppStr)).append("\n")
            sb.append("Comando de Voz;").append(escapeCsv(art.comandoVoz.replace("|", " | "))).append("\n")

            val condicionesStr = buildString {
                if (art.condicionesAmbientales.isNotBlank()) append(art.condicionesAmbientales.replace("|", " | "))
                if (art.condicionesAmbientalesOtros.isNotBlank()) {
                    if (isNotEmpty()) append(" | Otros: ") else append("Otros: ")
                    append(art.condicionesAmbientalesOtros)
                }
            }
            sb.append("Condiciones Ambientales;").append(escapeCsv(condicionesStr)).append("\n\n")

            // 3. PERSONAL EJECUTANTE
            sb.append("3. PERSONAL EJECUTANTE;;;;\n")
            sb.append("N°;Nombre;RUT;Firma;Hora\n")
            if (personal.isEmpty()) {
                sb.append(";;;;\n")
            } else {
                personal.forEachIndexed { idx, p ->
                    val firmaEstado = if (p.firmado) "Firmado" else "Sin firma"
                    sb.append("${idx + 1};")
                        .append(escapeCsv(p.nombre)).append(";")
                        .append(escapeCsv(p.rut)).append(";")
                        .append(escapeCsv(firmaEstado)).append(";")
                        .append(escapeCsv(p.horaFirma)).append("\n")
                }
            }
            sb.append("\n")

            // 4. ETAPAS DEL TRABAJO
            sb.append("4. ETAPAS DEL TRABAJO;;;;\n")
            sb.append("Ítem;Etapa;Riesgo asociado;Medida de control\n")
            etapas.forEachIndexed { index, e ->
                sb.append("${index + 1};")
                    .append(escapeCsv(e.etapa)).append(";")
                    .append(escapeCsv(formatRiesgoConNumero(e.riesgoAsociado))).append(";")
                    .append(escapeCsv(e.medidaControl)).append("\n")
            }
            sb.append("\n")

            // 5. CIERRE
            sb.append("5. CIERRE;;;;\n")
            sb.append("Responsable de Trabajos;").append(escapeCsv(art.docente)).append("\n")
            sb.append("Firma;").append(if (art.responsableFirmado) "Firmado digitalmente" else "Pendiente").append("\n")
            sb.append("Observaciones;\n")

            fos.write(sb.toString().toByteArray(Charsets.UTF_8))
        }

        shareFile(context, file, "text/csv", fileName)
        file
    }

    // =========================================================================
    // CHARLA - PDF (A4 Vertical: 595 x 842 puntos)
    // =========================================================================
    suspend fun exportarCharlaPdf(context: Context, item: HistorialItem): File = withContext(Dispatchers.IO) {
        val fileName = "charla_${getTimestampString()}.pdf"
        val file = File(getExportsDir(context), fileName)

        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 36f
        val maxContentY = pageHeight - margin - 15f
        val footerH = 76f

        val strokePaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 0.8f
            isAntiAlias = true
        }

        val fillPaint = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val boldPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val titlePaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            textSize = 12.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val headerPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val listaAsistentes = if (item.asistentes.isNotEmpty()) {
            item.asistentes
        } else {
            item.personas.map { nombre ->
                com.example.model.Asistente(nombre = nombre, firmado = true)
            }
        }

        val colNWidth = 24f
        val colNombreWidth = 195f
        val colRutWidth = 90f
        val colFirmaWidth = 90f
        val colObsWidth = (pageWidth - 2 * margin) - (colNWidth + colNombreWidth + colRutWidth + colFirmaWidth)

        val xN = margin
        val xNombre = xN + colNWidth
        val xRut = xNombre + colNombreWidth
        val xFirma = xRut + colRutWidth
        val xObs = xFirma + colFirmaWidth
        val xEnd = pageWidth - margin

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        fun drawCharlaHeader(c: Canvas, isFirstPage: Boolean): Float {
            var y = margin
            val headerBoxHeight = 58f
            c.drawRect(margin, y, pageWidth - margin, y + headerBoxHeight, strokePaint)

            val logoWidth = 115f
            c.drawLine(margin + logoWidth, y, margin + logoWidth, y + headerBoxHeight, strokePaint)

            fillPaint.color = Color.rgb(194, 0, 0)
            c.drawRect(margin + 12f, y + 12f, margin + 22f, y + 22f, fillPaint)
            fillPaint.color = Color.BLACK

            boldPaint.textSize = 12f
            c.drawText("INACAP", margin + 26f, y + 22f, boldPaint)
            boldPaint.textSize = 9.5f

            textPaint.textSize = 7.5f
            c.drawText("SAN PEDRO DE LA PAZ", margin + 12f, y + 36f, textPaint)
            textPaint.textSize = 9f

            val titleCenterX = margin + logoWidth + (pageWidth - margin - (margin + logoWidth)) / 2f
            c.drawText("REGISTRO DE CHARLA DE SEGURIDAD", titleCenterX, y + 25f, titlePaint)
            c.drawText("INICIAL", titleCenterX, y + 43f, titlePaint)

            y += headerBoxHeight + 8f

            if (isFirstPage) {
                val docenteLines = wrapText(item.docente, textPaint, pageWidth - 2 * margin - 75f)
                val docenteH = maxOf(20f, maxOf(docenteLines.size, 1) * 11.5f + 7f)
                c.drawRect(margin, y, pageWidth - margin, y + docenteH, strokePaint)
                c.drawText("DOCENTE: ", margin + 6f, y + 13.5f, boldPaint)
                drawLines(c, docenteLines, margin + 64f, y + 13.5f, 11.5f, textPaint)

                y += docenteH

                val tipoCharlaWidth = 145f
                val infoWidth = (pageWidth - 2 * margin) - tipoCharlaWidth
                val splitX = margin + infoWidth

                val temaLines = wrapText(item.tema, textPaint, infoWidth - 55f)
                val temaH = maxOf(18f, maxOf(temaLines.size, 1) * 11.5f + 7f)
                val blockHeight = 4 * 18f + temaH

                c.drawRect(margin, y, pageWidth - margin, y + blockHeight, strokePaint)
                c.drawLine(splitX, y, splitX, y + blockHeight, strokePaint)

                val rowH = 18f
                for (i in 1..4) {
                    c.drawLine(margin, y + i * rowH, splitX, y + i * rowH, strokePaint)
                }

                val asigLines = wrapText(item.asignatura, textPaint, infoWidth - 85f)
                c.drawText("ASIGNATURA: ", margin + 6f, y + 13f, boldPaint)
                drawLines(c, asigLines.take(1), margin + 80f, y + 13f, 11f, textPaint)

                c.drawText("SECCIÓN: ", margin + 6f, y + rowH + 13f, boldPaint)
                c.drawText(item.seccion, margin + 80f, y + rowH + 13f, textPaint)

                c.drawText("HORA INICIO: ", margin + 6f, y + 2 * rowH + 13f, boldPaint)
                c.drawText(if (item.fecha.isNotBlank()) "08:30" else "", margin + 75f, y + 2 * rowH + 13f, textPaint)
                c.drawText("HORA TÉRMINO: ", margin + 140f, y + 2 * rowH + 13f, boldPaint)
                c.drawText(if (item.fecha.isNotBlank()) "09:15" else "", margin + 225f, y + 2 * rowH + 13f, textPaint)

                c.drawText("FECHA: ", margin + 6f, y + 3 * rowH + 13f, boldPaint)
                c.drawText(item.fecha, margin + 80f, y + 3 * rowH + 13f, textPaint)

                val temaY = y + 4 * rowH
                c.drawText("TEMA: ", margin + 6f, temaY + 13f, boldPaint)
                drawLines(c, temaLines, margin + 50f, temaY + 13f, 11.5f, textPaint)

                val tipoHeaderH = 18f
                c.drawLine(splitX, y + tipoHeaderH, pageWidth - margin, y + tipoHeaderH, strokePaint)
                val tipoTitleCenterX = splitX + tipoCharlaWidth / 2f
                c.drawText("TIPO DE CHARLA", tipoTitleCenterX, y + 13f, headerPaint)

                val tiposLista = listOf(
                    "CHARLA 5 MINUTOS",
                    "CHARLA INTEGRAL",
                    "CHARLA EXTERNA",
                    "CHARLA INTERNA",
                    "REINSTRUCCIÓN"
                )
                val tipoRowH = (blockHeight - tipoHeaderH) / 5f
                val boxSize = 9f
                val boxMarginRight = 8f

                tiposLista.forEachIndexed { idx, tipoNombre ->
                    val yLine = y + tipoHeaderH + idx * tipoRowH
                    if (idx > 0) {
                        c.drawLine(splitX, yLine, pageWidth - margin, yLine, strokePaint)
                    }
                    val isChecked = item.tipo.contains(tipoNombre, ignoreCase = true) ||
                        (tipoNombre == "CHARLA 5 MINUTOS" && item.tipo.contains("5 min", ignoreCase = true))

                    c.drawText(tipoNombre, splitX + 6f, yLine + 10.5f, boldPaint)

                    val boxX = pageWidth - margin - boxMarginRight - boxSize
                    val boxY = yLine + (tipoRowH - boxSize) / 2f
                    c.drawRect(boxX, boxY, boxX + boxSize, boxY + boxSize, strokePaint)
                    if (isChecked) {
                        c.drawText("X", boxX + 2f, boxY + boxSize - 1.5f, boldPaint)
                    }
                }

                y += blockHeight + 10f
            }

            val tableTitleH = 16f
            c.drawRect(margin, y, pageWidth - margin, y + tableTitleH, strokePaint)
            val tableCenterX = margin + (pageWidth - 2 * margin) / 2f
            c.drawText("ASISTENCIA", tableCenterX, y + 12f, headerPaint)
            y += tableTitleH

            val tableHeaderH = 16f
            c.drawRect(margin, y, xEnd, y + tableHeaderH, strokePaint)
            c.drawLine(xNombre, y, xNombre, y + tableHeaderH, strokePaint)
            c.drawLine(xRut, y, xRut, y + tableHeaderH, strokePaint)
            c.drawLine(xFirma, y, xFirma, y + tableHeaderH, strokePaint)
            c.drawLine(xObs, y, xObs, y + tableHeaderH, strokePaint)

            c.drawText("N°", xN + 5f, y + 11.5f, boldPaint)
            c.drawText("NOMBRE", xNombre + 6f, y + 11.5f, boldPaint)
            c.drawText("RUT", xRut + 6f, y + 11.5f, boldPaint)
            c.drawText("FIRMA", xFirma + 6f, y + 11.5f, boldPaint)
            c.drawText("OBSERVACIÓN", xObs + 6f, y + 11.5f, boldPaint)

            y += tableHeaderH
            return y
        }

        var currentY = drawCharlaHeader(canvas, isFirstPage = true)

        for (i in listaAsistentes.indices) {
            val asistente = listaAsistentes[i]
            val isLast = (i == listaAsistentes.lastIndex)

            val nombreLines: List<String> = if (asistente.esExterno && asistente.procedencia.isNotBlank()) {
                val l1 = wrapText(asistente.nombre, textPaint, colNombreWidth - 12f)
                val l2 = wrapText("Externo · ${asistente.procedencia}", textPaint, colNombreWidth - 12f)
                l1 + l2
            } else {
                wrapText(asistente.nombre, textPaint, colNombreWidth - 12f)
            }

            val obsLines = wrapText(asistente.observacion, textPaint, colObsWidth - 12f)
            val maxLines = maxOf(nombreLines.size, obsLines.size, 1)
            val rowHeight = maxOf(18f, maxLines * 11.5f + 7f)

            val neededHeight = rowHeight + (if (isLast) footerH + 10f else 0f)
            if (currentY + neededHeight > maxContentY) {
                canvas.drawText("Página $pageNumber", pageWidth - margin - 50f, pageHeight - margin / 2f, textPaint)
                document.finishPage(page)

                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas

                currentY = drawCharlaHeader(canvas, isFirstPage = false)
            }

            canvas.drawRect(margin, currentY, xEnd, currentY + rowHeight, strokePaint)
            canvas.drawLine(xNombre, currentY, xNombre, currentY + rowHeight, strokePaint)
            canvas.drawLine(xRut, currentY, xRut, currentY + rowHeight, strokePaint)
            canvas.drawLine(xFirma, currentY, xFirma, currentY + rowHeight, strokePaint)
            canvas.drawLine(xObs, currentY, xObs, currentY + rowHeight, strokePaint)

            canvas.drawText("${i + 1}", xN + 5f, currentY + 12f, textPaint)
            drawLines(canvas, nombreLines, xNombre + 6f, currentY + 12f, 11.5f, textPaint)
            canvas.drawText(asistente.rut, xRut + 6f, currentY + 12f, textPaint)

            val firmaTexto = if (asistente.firmado) {
                if (asistente.horaFirma.isNotBlank()) "Firmado ${asistente.horaFirma}" else "Firmado"
            } else {
                "Sin firma"
            }
            canvas.drawText(firmaTexto, xFirma + 6f, currentY + 12f, textPaint)
            drawLines(canvas, obsLines, xObs + 6f, currentY + 12f, 11.5f, textPaint)

            currentY += rowHeight
        }

        currentY += 12f

        if (currentY + footerH > maxContentY) {
            canvas.drawText("Página $pageNumber", pageWidth - margin - 50f, pageHeight - margin / 2f, textPaint)
            document.finishPage(page)

            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(pageInfo)
            canvas = page.canvas
            currentY = margin + 20f
        }

        val splitFooterX = margin + 300f
        canvas.drawRect(margin, currentY, xEnd, currentY + 65f, strokePaint)
        canvas.drawLine(splitFooterX, currentY, splitFooterX, currentY + 65f, strokePaint)

        canvas.drawText("COMENTARIOS/ OBSERVACIONES/ COMPROMISOS:", margin + 6f, currentY + 13f, boldPaint)
        canvas.drawLine(margin + 6f, currentY + 28f, splitFooterX - 6f, currentY + 28f, strokePaint)
        canvas.drawLine(margin + 6f, currentY + 44f, splitFooterX - 6f, currentY + 44f, strokePaint)
        canvas.drawLine(margin + 6f, currentY + 58f, splitFooterX - 6f, currentY + 58f, strokePaint)

        canvas.drawText("DOCENTE/ RELATOR/ RESPONSABLE", splitFooterX + 16f, currentY + 13f, boldPaint)
        canvas.drawLine(splitFooterX, currentY + 18f, xEnd, currentY + 18f, strokePaint)

        canvas.drawText("NOMBRE:", splitFooterX + 6f, currentY + 34f, boldPaint)
        val docenteNameLines = wrapText(item.docente, textPaint, xEnd - splitFooterX - 65f)
        drawLines(canvas, docenteNameLines.take(1), splitFooterX + 60f, currentY + 34f, 11f, textPaint)
        canvas.drawLine(splitFooterX, currentY + 40f, xEnd, currentY + 40f, strokePaint)

        canvas.drawText("FIRMA:", splitFooterX + 6f, currentY + 54f, boldPaint)
        canvas.drawText("Firmado digitalmente", splitFooterX + 60f, currentY + 54f, textPaint)

        canvas.drawText("Página $pageNumber", pageWidth - margin - 50f, pageHeight - margin / 2f, textPaint)
        document.finishPage(page)

        FileOutputStream(file).use { fos ->
            document.writeTo(fos)
        }
        document.close()

        shareFile(context, file, "application/pdf", fileName)
        file
    }

    // =========================================================================
    // ART - PDF (A4 Horizontal: 842 x 595 puntos - Oficial INACAP Completo)
    // =========================================================================
    suspend fun exportarArtPdf(context: Context, artConEtapas: ArtConEtapas): File = withContext(Dispatchers.IO) {
        val fileName = "art_${getTimestampString()}.pdf"
        val file = File(getExportsDir(context), fileName)
        val art = artConEtapas.art
        val etapas = artConEtapas.etapas.sortedBy { it.orden }
        val personal = artConEtapas.personalEjecutante.sortedBy { it.orden }

        val document = PdfDocument()
        val pageWidth = 842
        val pageHeight = 595
        val marginLeft = 20f
        val marginRight = 822f
        val usableWidth = marginRight - marginLeft // 802f
        val maxContentY = 562f

        val strokePaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 0.5f
            isAntiAlias = true
        }

        val fillPaint = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val titlePaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            textSize = 11.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val inacapTitlePaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val inacapSubPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            textSize = 6.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }

        val headerSectionPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            textSize = 7.2f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val boldPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            textSize = 6.6f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val textPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            textSize = 6.0f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val smallTextPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            textSize = 4.8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        // Listas predefinidas del formulario oficial
        val equipoApoyoCol1 = listOf(
            "Red eléctrica trifásica",
            "Red eléctrica monofásica",
            "Fuentes de poder bajo y alto voltaje",
            "Cable de Alimentación Eléctrica",
            "Bornes de Conexión",
            "Dispositivos de maniobra eléctrica",
            "Tablero o Gabinete eléctrico",
            "Cable Banana/Caimán",
            "Transformador Eléctrico",
            "Variador de Frecuencia",
            "Partidor Suave",
            "Temporizador"
        )
        val equipoApoyoCol2 = listOf(
            "Motor eléctrico",
            "Instrumento de medida",
            "Herramientas mecánicas",
            "Máquinas de presión neumática",
            "Máquinas de presión hidráulica",
            "Herramientas manuales de golpe",
            "Taladro eléctrico",
            "Pistola de Calor Industrial",
            "Morsa de Banco Giratoria Con Yunque",
            "Sierra Manual",
            "Estación de Soldadura"
        )

        val eppCol1 = listOf(
            "Guante dieléctrico",
            "Guante de cabritilla",
            "Guante Hy-Flex",
            "Zapatos de Seguridad dieléctrico",
            "Delantal Blanco",
            "Antiparra de Seguridad",
            "Casco dieléctrico",
            "Chaleco Reflectante",
            "Cubre Calzado"
        )
        val eppCol2 = listOf(
            "Arnés de Seguridad con dos Colas",
            "Absorbedor de caídas",
            "Gorro legionario para casco",
            "Protector Auditivo",
            "Lápiz detector de voltaje",
            "Protector Solar",
            "Ropa de PVC"
        )

        val condicionesMatriz = listOf(
            listOf("Falta de Luz", "Calor", "Radiación UV", "Materiales inflamables"),
            listOf("Ruido", "Gases", "Vibraciones", "Superficies resbaladizas"),
            listOf("Humedad", "Polvo/Humo", "Sin Ventilación")
        )

        val riesgosMatriz = listOf(
            listOf("1 Choque eléctrico", "4 Ruido", "7 Tropiezos", "10 Fallas en aislamiento", "13 Exposición al polvo", "16 Trabajos en altura", "19 Caída igual o distinto nivel", "22 Golpeado por/contra/con", "25 Radiación láser"),
            listOf("2 Arco eléctrico", "5 Explosión", "8 Contacto con partes activas", "11 Equipos defectuosos", "14 Desniveles", "17 Intoxicación", "20 Proyección de partículas", "23 Sustancias tóxicas", "26 Otros"),
            listOf("3 Incendio", "6 Corte", "9 Quemaduras", "12 Sobrecargas", "15 Caída de materiales", "18 Radiación electromagnética", "21 Espacio confinado", "24 Ergonómico", "27 Otros")
        )

        val equiposSeleccionados = art.equipoApoyo.split("|").filter { it.isNotBlank() }.toSet()
        val eppSeleccionados = art.epp.split("|").filter { it.isNotBlank() }.toSet()
        val comandosSeleccionados = art.comandoVoz.split("|").filter { it.isNotBlank() }.toSet()
        val condicionesSeleccionadas = art.condicionesAmbientales.split("|").filter { it.isNotBlank() }.toSet()

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        fun drawCheckbox(c: Canvas, x: Float, y: Float, isChecked: Boolean, size: Float = 5.5f) {
            c.drawRect(x, y, x + size, y + size, strokePaint)
            if (isChecked) {
                c.drawText("X", x + 1.1f, y + size - 1.1f, boldPaint)
            }
        }

        fun drawArtFullHeader(c: Canvas, isFirstPage: Boolean): Float {
            var y = 14f

            // 1. Encabezado principal
            val headerBoxHeight = 28f
            c.drawRect(marginLeft, y, marginRight, y + headerBoxHeight, strokePaint)
            val leftBoxW = 140f
            c.drawLine(marginLeft + leftBoxW, y, marginLeft + leftBoxW, y + headerBoxHeight, strokePaint)

            // Recuadro INACAP · San Pedro de la Paz sin logo copiado
            c.drawText("INACAP", marginLeft + leftBoxW / 2f, y + 11.5f, inacapTitlePaint)
            c.drawText("San Pedro de la Paz", marginLeft + leftBoxW / 2f, y + 21.5f, inacapSubPaint)

            val titleCenterX = marginLeft + leftBoxW + (usableWidth - leftBoxW) / 2f
            val headerTitleText = if (isFirstPage) {
                "ANÁLISIS DE RIESGOS DEL TRABAJO (ART)"
            } else {
                "ANÁLISIS DE RIESGOS DEL TRABAJO (ART) - CONTINUACIÓN"
            }
            c.drawText(headerTitleText, titleCenterX, y + 17.5f, titlePaint)

            y += headerBoxHeight + 2f

            if (isFirstPage) {
                // 2. Fila 1: TRABAJO O ACTIVIDAD / ESPECIALIDAD / FECHA / TIPO DE ACTIVIDAD
                val row1Height = 13.5f
                val xEsp = marginLeft + 300f
                val xFec = marginLeft + 490f
                val xTip = marginLeft + 610f

                c.drawRect(marginLeft, y, marginRight, y + row1Height, strokePaint)
                c.drawLine(xEsp, y, xEsp, y + row1Height, strokePaint)
                c.drawLine(xFec, y, xFec, y + row1Height, strokePaint)
                c.drawLine(xTip, y, xTip, y + row1Height, strokePaint)

                c.drawText("TRABAJO O ACTIVIDAD:", marginLeft + 4f, y + 9.5f, boldPaint)
                val actLines = wrapText(art.trabajoActividad, textPaint, xEsp - marginLeft - 100f)
                if (actLines.size > 1) {
                    c.drawText(actLines[0], marginLeft + 98f, y + 5.5f, smallTextPaint)
                    c.drawText(actLines[1], marginLeft + 98f, y + 11.5f, smallTextPaint)
                } else {
                    c.drawText(actLines.firstOrNull() ?: "", marginLeft + 98f, y + 9.5f, textPaint)
                }

                c.drawText("ESPECIALIDAD:", xEsp + 4f, y + 9.5f, boldPaint)
                val espLines = wrapText(art.especialidad, textPaint, xFec - xEsp - 68f)
                c.drawText(espLines.firstOrNull() ?: "", xEsp + 66f, y + 9.5f, textPaint)

                c.drawText("FECHA:", xFec + 4f, y + 9.5f, boldPaint)
                c.drawText(art.fecha, xFec + 36f, y + 9.5f, textPaint)

                c.drawText("TIPO DE ACTIVIDAD:", xTip + 4f, y + 9.5f, boldPaint)
                val isTeorica = art.tipoActividad.contains("Teórica", ignoreCase = true)
                val isPractica = art.tipoActividad.contains("Práctica", ignoreCase = true) || (!isTeorica && art.tipoActividad.isNotBlank())

                c.drawText("TEÓRICA", xTip + 88f, y + 9.5f, textPaint)
                drawCheckbox(c, xTip + 122f, y + 4f, isTeorica, size = 5.5f)
                c.drawText("PRÁCTICA", xTip + 136f, y + 9.5f, textPaint)
                drawCheckbox(c, xTip + 176f, y + 4f, isPractica, size = 5.5f)

                y += row1Height

                // 3. Fila 2: DOCENTE / SALA / TALLER / LABORATORIO / TERRENO
                val row2Height = 13.5f
                val xDocenteSplit = marginLeft + 420f
                c.drawRect(marginLeft, y, marginRight, y + row2Height, strokePaint)
                c.drawLine(xDocenteSplit, y, xDocenteSplit, y + row2Height, strokePaint)

                c.drawText("DOCENTE:", marginLeft + 4f, y + 9.5f, boldPaint)
                val docLines = wrapText(art.docente, textPaint, xDocenteSplit - marginLeft - 55f)
                c.drawText(docLines.firstOrNull() ?: "", marginLeft + 52f, y + 9.5f, textPaint)

                c.drawText("SALA / TALLER / LABORATORIO / TERRENO:", xDocenteSplit + 4f, y + 9.5f, boldPaint)
                val lugarLines = wrapText(art.lugar, textPaint, marginRight - xDocenteSplit - 170f)
                c.drawText(lugarLines.firstOrNull() ?: "", xDocenteSplit + 168f, y + 9.5f, textPaint)

                y += row2Height + 2f

                // 4. Bloque de tres columnas (Equipo Apoyo / EPP / Personal Ejecutante)
                val block3Height = 114f
                val col1Width = 250f
                val col2Width = 285f
                val col3Width = usableWidth - col1Width - col2Width // 267f

                val xCol1 = marginLeft
                val xCol2 = xCol1 + col1Width
                val xCol3 = xCol2 + col2Width
                val xColEnd = marginRight

                c.drawRect(xCol1, y, xColEnd, y + block3Height, strokePaint)
                c.drawLine(xCol2, y, xCol2, y + block3Height, strokePaint)
                c.drawLine(xCol3, y, xCol3, y + block3Height, strokePaint)

                // 4.1 Columna Izquierda: Equipo de Apoyo
                val titleH = 10f
                c.drawRect(xCol1, y, xCol2, y + titleH, strokePaint)
                c.drawText("Equipo de Apoyo", xCol1 + col1Width / 2f, y + 7.5f, headerSectionPaint)

                val subColW = col1Width / 2f
                c.drawLine(xCol1 + subColW, y + titleH, xCol1 + subColW, y + block3Height, strokePaint)

                val itemH = (block3Height - titleH) / 12f
                for (row in 0 until 12) {
                    val yRow = y + titleH + row * itemH
                    if (row > 0) {
                        c.drawLine(xCol1, yRow, xCol2, yRow, strokePaint)
                    }

                    // Subcol 1
                    if (row < equipoApoyoCol1.size) {
                        val item = equipoApoyoCol1[row]
                        drawCheckbox(c, xCol1 + 2f, yRow + 1.6f, equiposSeleccionados.contains(item), size = 5.2f)
                        c.drawText(item, xCol1 + 9f, yRow + 6.2f, smallTextPaint)
                    }

                    // Subcol 2
                    if (row < equipoApoyoCol2.size) {
                        val item = equipoApoyoCol2[row]
                        drawCheckbox(c, xCol1 + subColW + 2f, yRow + 1.6f, equiposSeleccionados.contains(item), size = 5.2f)
                        c.drawText(item, xCol1 + subColW + 9f, yRow + 6.2f, smallTextPaint)
                    } else if (row == 11) {
                        // Otros en la última fila
                        val isOtrosChecked = art.equipoApoyoOtros.isNotBlank()
                        drawCheckbox(c, xCol1 + subColW + 2f, yRow + 1.6f, isOtrosChecked, size = 5.2f)
                        val otrosText = if (isOtrosChecked) "Otros: ${art.equipoApoyoOtros}" else "Otros:.................."
                        c.drawText(otrosText, xCol1 + subColW + 9f, yRow + 6.2f, smallTextPaint)
                    }
                }

                // 4.2 Columna Centro: Nivel técnico + EPP
                val nivelH = 18f
                c.drawRect(xCol2, y, xCol3, y + nivelH, strokePaint)
                c.drawText("Nivel técnico de la Actividad", xCol2 + col2Width / 2f, y + 7f, headerSectionPaint)

                val nivelItemW = col2Width / 3f
                c.drawLine(xCol2, y + 9f, xCol3, y + 9f, strokePaint)
                c.drawLine(xCol2 + nivelItemW, y + 9f, xCol2 + nivelItemW, y + nivelH, strokePaint)
                c.drawLine(xCol2 + 2 * nivelItemW, y + 9f, xCol2 + 2 * nivelItemW, y + nivelH, strokePaint)

                val isInicial = art.nivelTecnico.contains("Inicial", ignoreCase = true)
                val isIntermedio = art.nivelTecnico.contains("Intermedio", ignoreCase = true)
                val isAvanzado = art.nivelTecnico.contains("Avanzado", ignoreCase = true)

                c.drawText("Nivel Inicial", xCol2 + 4f, y + 15.5f, textPaint)
                drawCheckbox(c, xCol2 + nivelItemW - 9f, y + 10.5f, isInicial, size = 5.2f)

                c.drawText("Nivel Intermedio", xCol2 + nivelItemW + 4f, y + 15.5f, textPaint)
                drawCheckbox(c, xCol2 + 2 * nivelItemW - 9f, y + 10.5f, isIntermedio, size = 5.2f)

                c.drawText("Nivel Avanzado", xCol2 + 2 * nivelItemW + 4f, y + 15.5f, textPaint)
                drawCheckbox(c, xCol3 - 9f, y + 10.5f, isAvanzado, size = 5.2f)

                // EPP
                val eppTop = y + nivelH
                val eppTitleH = 9f
                c.drawRect(xCol2, eppTop, xCol3, eppTop + eppTitleH, strokePaint)
                c.drawText("Equipo de Protección Personal", xCol2 + col2Width / 2f, eppTop + 6.8f, headerSectionPaint)

                val subColEppW = col2Width / 2f
                c.drawLine(xCol2 + subColEppW, eppTop + eppTitleH, xCol2 + subColEppW, y + block3Height, strokePaint)

                val eppItemH = (block3Height - nivelH - eppTitleH) / 9f
                val eppOtrosLines = if (art.eppOtros.isNotBlank()) wrapText(art.eppOtros, smallTextPaint, subColEppW - 12f) else emptyList()

                for (row in 0 until 9) {
                    val yRow = eppTop + eppTitleH + row * eppItemH
                    if (row > 0) {
                        c.drawLine(xCol2, yRow, xCol3, yRow, strokePaint)
                    }

                    // EPP Col 1
                    if (row < eppCol1.size) {
                        val item = eppCol1[row]
                        drawCheckbox(c, xCol2 + 2f, yRow + 1.6f, eppSeleccionados.contains(item), size = 5.2f)
                        c.drawText(item, xCol2 + 9f, yRow + 6.4f, smallTextPaint)
                    }

                    // EPP Col 2
                    if (row < eppCol2.size) {
                        val item = eppCol2[row]
                        drawCheckbox(c, xCol2 + subColEppW + 2f, yRow + 1.6f, eppSeleccionados.contains(item), size = 5.2f)
                        c.drawText(item, xCol2 + subColEppW + 9f, yRow + 6.4f, smallTextPaint)
                    } else if (row == 7) {
                        val isOtrosChecked = art.eppOtros.isNotBlank()
                        drawCheckbox(c, xCol2 + subColEppW + 2f, yRow + 1.6f, isOtrosChecked, size = 5.2f)
                        val otrosText1 = if (isOtrosChecked) {
                            "Otros: " + (eppOtrosLines.firstOrNull() ?: "")
                        } else {
                            "Otros:.................."
                        }
                        c.drawText(otrosText1, xCol2 + subColEppW + 9f, yRow + 6.4f, smallTextPaint)
                    } else if (row == 8) {
                        val otrosText2 = if (eppOtrosLines.size > 1) {
                            "Otros: " + eppOtrosLines[1]
                        } else {
                            "Otros:.................."
                        }
                        c.drawText(otrosText2, xCol2 + subColEppW + 9f, yRow + 6.4f, smallTextPaint)
                    }
                }

                // 4.3 Columna Derecha: Personal Ejecutante (13 filas)
                val personalTitleH = 10f
                val firmaColW = 55f
                val xFirmaSplit = xColEnd - firmaColW

                c.drawRect(xCol3, y, xColEnd, y + personalTitleH, strokePaint)
                c.drawLine(xFirmaSplit, y, xFirmaSplit, y + block3Height, strokePaint)

                c.drawText("Personal Ejecutante", xCol3 + (col3Width - firmaColW) / 2f, y + 7.5f, headerSectionPaint)
                c.drawText("Firma", xFirmaSplit + firmaColW / 2f, y + 7.5f, headerSectionPaint)

                val personalRowH = (block3Height - personalTitleH) / 13f
                for (row in 0 until 13) {
                    val yRow = y + personalTitleH + row * personalRowH
                    if (row > 0) {
                        c.drawLine(xCol3, yRow, xColEnd, yRow, strokePaint)
                    }

                    val num = row + 1
                    c.drawText("$num", xCol3 + 2f, yRow + 5.8f, smallTextPaint)

                    val p = personal.getOrNull(row)
                    if (p != null) {
                        val pName = if (p.rut.isNotBlank()) "${p.nombre} (${p.rut})" else p.nombre
                        val nameLines = wrapText(pName, smallTextPaint, xFirmaSplit - xCol3 - 16f)
                        c.drawText(nameLines.firstOrNull() ?: "", xCol3 + 12f, yRow + 5.8f, smallTextPaint)

                        if (p.firmado) {
                            val fText = if (p.horaFirma.isNotBlank()) "Firmado ${p.horaFirma}" else "Firmado"
                            c.drawText(fText, xFirmaSplit + 3f, yRow + 5.8f, smallTextPaint)
                        }
                    }
                }

                y += block3Height + 2f

                // 5. Fila de tres bloques: Comando de Voz / Condiciones Ambientales / Charla Inicial
                val block3BHeight = 28f
                val colCVWidth = 240f
                val colCAWidth = 340f
                val colCIWidth = usableWidth - colCVWidth - colCAWidth // 222f

                val xCV = marginLeft
                val xCA = xCV + colCVWidth
                val xCI = xCA + colCAWidth

                c.drawRect(xCV, y, xColEnd, y + block3BHeight, strokePaint)
                c.drawLine(xCA, y, xCA, y + block3BHeight, strokePaint)
                c.drawLine(xCI, y, xCI, y + block3BHeight, strokePaint)

                // 5.1 Comando de Voz
                val cvTitleH = 9f
                c.drawRect(xCV, y, xCA, y + cvTitleH, strokePaint)
                c.drawText("Comando de Voz", xCV + colCVWidth / 2f, y + 6.8f, headerSectionPaint)

                val cvSubW = colCVWidth / 3f
                c.drawLine(xCV + cvSubW, y + cvTitleH, xCV + cvSubW, y + block3BHeight, strokePaint)
                c.drawLine(xCV + 2 * cvSubW, y + cvTitleH, xCV + 2 * cvSubW, y + block3BHeight, strokePaint)

                val yCvContent = y + cvTitleH + 11.5f
                c.drawText("Alto", xCV + 14f, yCvContent, boldPaint)
                drawCheckbox(c, xCV + cvSubW - 14f, yCvContent - 5.5f, comandosSeleccionados.contains("Alto"), size = 5.5f)

                c.drawText("Atención", xCV + cvSubW + 12f, yCvContent, boldPaint)
                drawCheckbox(c, xCV + 2 * cvSubW - 14f, yCvContent - 5.5f, comandosSeleccionados.contains("Atención"), size = 5.5f)

                c.drawText("Precaución", xCV + 2 * cvSubW + 10f, yCvContent, boldPaint)
                drawCheckbox(c, xCA - 14f, yCvContent - 5.5f, comandosSeleccionados.contains("Precaución"), size = 5.5f)

                // 5.2 Condiciones Ambientales
                val caTitleH = 9f
                c.drawRect(xCA, y, xCI, y + caTitleH, strokePaint)
                c.drawText("Condiciones Ambientales", xCA + colCAWidth / 2f, y + 6.8f, headerSectionPaint)

                val caColW = colCAWidth / 4f
                val caRowH = (block3BHeight - caTitleH) / 3f

                for (r in 0 until 3) {
                    val yRow = y + caTitleH + r * caRowH
                    if (r > 0) c.drawLine(xCA, yRow, xCI, yRow, strokePaint)

                    val filaItems = condicionesMatriz.getOrNull(r) ?: emptyList()
                    for (cIdx in 0 until 4) {
                        val xCell = xCA + cIdx * caColW
                        if (cIdx > 0 && r == 0) c.drawLine(xCell, y + caTitleH, xCell, y + block3BHeight, strokePaint)

                        if (r == 2 && cIdx == 3) {
                            val isOtrosChecked = art.condicionesAmbientalesOtros.isNotBlank()
                            drawCheckbox(c, xCell + 2f, yRow + 1.0f, isOtrosChecked, size = 4.8f)
                            val oText = if (isOtrosChecked) "Otros: ${art.condicionesAmbientalesOtros}" else "Otros:........"
                            c.drawText(oText, xCell + 8f, yRow + 5.0f, smallTextPaint)
                        } else if (cIdx < filaItems.size) {
                            val itm = filaItems[cIdx]
                            drawCheckbox(c, xCell + 2f, yRow + 1.0f, condicionesSeleccionadas.contains(itm), size = 4.8f)
                            c.drawText(itm, xCell + 8f, yRow + 5.0f, smallTextPaint)
                        }
                    }
                }

                // 5.3 Charla de seguridad Inicial
                val yCiText = y + 10f
                c.drawText("Charla de seguridad Inicial", xCI + 8f, yCiText, boldPaint)

                c.drawText("Sí", xCI + 130f, yCiText, boldPaint)
                drawCheckbox(c, xCI + 142f, yCiText - 5.5f, art.charlaSeguridadInicial, size = 5.5f)

                c.drawText("No", xCI + 165f, yCiText, boldPaint)
                drawCheckbox(c, xCI + 180f, yCiText - 5.5f, !art.charlaSeguridadInicial, size = 5.5f)

                if (art.charlaVinculadaTema != null) {
                    val vinculadaStr = "Charla vinculada: ${art.charlaVinculadaFecha ?: ""}, ${art.charlaVinculadaTema}"
                    val vLines = wrapText(vinculadaStr, smallTextPaint, colCIWidth - 16f)
                    drawLines(c, vLines.take(2), xCI + 8f, y + 18.5f, 6.2f, smallTextPaint)
                }

                y += block3BHeight + 2f

                // 6. Recuadro RIESGOS ASOCIADOS (Catálogo en 9 columnas x 3 filas numeradas completas)
                val riesgosH = 32f
                c.drawRect(marginLeft, y, xColEnd, y + riesgosH, strokePaint)
                val rTitleH = 8.5f
                c.drawRect(marginLeft, y, xColEnd, y + rTitleH, strokePaint)
                c.drawText("RIESGOS ASOCIADOS", marginLeft + usableWidth / 2f, y + 6.8f, headerSectionPaint)

                val colRiesgoW = usableWidth / 9f
                val rRowH = (riesgosH - rTitleH) / 3f

                for (cIdx in 1 until 9) {
                    val xGridLine = marginLeft + cIdx * colRiesgoW
                    c.drawLine(xGridLine, y + rTitleH, xGridLine, y + riesgosH, strokePaint)
                }

                for (r in 0 until 3) {
                    val yRow = y + rTitleH + r * rRowH
                    if (r > 0) c.drawLine(marginLeft, yRow, xColEnd, yRow, strokePaint)
                    val listaR = riesgosMatriz[r]
                    for (col in 0 until 9) {
                        val itemR = listaR[col]
                        c.drawText(itemR, marginLeft + col * colRiesgoW + 2f, yRow + 5.8f, smallTextPaint)
                    }
                }

                y += riesgosH + 3f
            }

            // 7. Encabezado de Tabla de Etapas
            val tableHeaderH = 13f
            val xItem = marginLeft
            val xEtapa = xItem + 32f
            val xRiesgo = xEtapa + 230f
            val xControl = xRiesgo + 220f
            val xEnd = marginRight

            c.drawRect(marginLeft, y, xEnd, y + tableHeaderH, strokePaint)
            c.drawLine(xEtapa, y, xEtapa, y + tableHeaderH, strokePaint)
            c.drawLine(xRiesgo, y, xRiesgo, y + tableHeaderH, strokePaint)
            c.drawLine(xControl, y, xControl, y + tableHeaderH, strokePaint)

            c.drawText("ÍTEM", xItem + 5f, y + 9.5f, boldPaint)
            c.drawText("ETAPAS DEL TRABAJO", xEtapa + 6f, y + 9.5f, boldPaint)
            c.drawText("RIESGOS ASOCIADOS", xRiesgo + 6f, y + 9.5f, boldPaint)
            c.drawText("MEDIDAS DE CONTROL DE RIESGOS", xControl + 6f, y + 9.5f, boldPaint)

            y += tableHeaderH
            return y
        }

        var currentY = drawArtFullHeader(canvas, isFirstPage = true)

        val xItem = marginLeft
        val xEtapa = xItem + 32f
        val xRiesgo = xEtapa + 230f
        val xControl = xRiesgo + 220f
        val xEnd = marginRight

        val colEtapaW = xRiesgo - xEtapa
        val colRiesgoW = xControl - xRiesgo
        val colControlW = xEnd - xControl

        val etapasCount = maxOf(etapas.size, 6)
        val footerBlockH = 44f // Responsable (14) + Observaciones (24) + Spacings (6)

        for (i in 0 until etapasCount) {
            val isLast = (i == etapasCount - 1)
            val etapa = etapas.getOrNull(i)

            val etapaLines = if (etapa != null) wrapText(etapa.etapa, textPaint, colEtapaW - 10f) else emptyList()
            val riesgoFormat = if (etapa != null) formatRiesgoConNumero(etapa.riesgoAsociado) else ""
            val riesgoLines = if (etapa != null) wrapText(riesgoFormat, textPaint, colRiesgoW - 10f) else emptyList()
            val controlLines = if (etapa != null) wrapText(etapa.medidaControl, textPaint, colControlW - 10f) else emptyList()

            val maxLines = maxOf(etapaLines.size, riesgoLines.size, controlLines.size, 1)
            val rowHeight = maxOf(16f, maxLines * 8.2f + 4.5f)

            val neededHeight = rowHeight + (if (isLast) footerBlockH + 6f else 0f)
            if (currentY + neededHeight > maxContentY) {
                // Pie de página
                val footerY = pageHeight - 10f
                canvas.drawText("Electricidad y Electrónica · Automatización y Robótica · Tecnologías de Información y Ciberseguridad", marginLeft, footerY, smallTextPaint)
                canvas.drawText("Página $pageNumber", xEnd - 45f, footerY, smallTextPaint)

                document.finishPage(page)

                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas

                currentY = drawArtFullHeader(canvas, isFirstPage = false)
            }

            canvas.drawRect(marginLeft, currentY, xEnd, currentY + rowHeight, strokePaint)
            canvas.drawLine(xEtapa, currentY, xEtapa, currentY + rowHeight, strokePaint)
            canvas.drawLine(xRiesgo, currentY, xRiesgo, currentY + rowHeight, strokePaint)
            canvas.drawLine(xControl, currentY, xControl, currentY + rowHeight, strokePaint)

            canvas.drawText("${i + 1}.", xItem + 5f, currentY + 9.5f, textPaint)
            drawLines(canvas, etapaLines, xEtapa + 5f, currentY + 9.5f, 8.2f, textPaint)
            drawLines(canvas, riesgoLines, xRiesgo + 5f, currentY + 9.5f, 8.2f, textPaint)
            drawLines(canvas, controlLines, xControl + 5f, currentY + 9.5f, 8.2f, textPaint)

            currentY += rowHeight
        }

        // Si hay más de 13 ejecutantes, incluir tabla anexa de ejecutantes adicionales
        if (personal.size > 13) {
            val extraPersonal = personal.subList(13, personal.size)
            val extraTableH = 10f + extraPersonal.size * 9f
            if (currentY + extraTableH + footerBlockH > maxContentY) {
                val footerY = pageHeight - 10f
                canvas.drawText("Electricidad y Electrónica · Automatización y Robótica · Tecnologías de Información y Ciberseguridad", marginLeft, footerY, smallTextPaint)
                canvas.drawText("Página $pageNumber", xEnd - 45f, footerY, smallTextPaint)

                document.finishPage(page)

                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas

                currentY = drawArtFullHeader(canvas, isFirstPage = false)
            }

            currentY += 4f
            val xExEnd = marginLeft + 350f
            val xExFirma = xExEnd - 70f
            canvas.drawRect(marginLeft, currentY, xExEnd, currentY + 10f, strokePaint)
            canvas.drawText("Personal Ejecutante (Continuación)", marginLeft + 10f, currentY + 7.5f, boldPaint)
            canvas.drawText("Firma", xExFirma + 10f, currentY + 7.5f, boldPaint)
            currentY += 10f

            for ((idx, p) in extraPersonal.withIndex()) {
                val rH = 9f
                canvas.drawRect(marginLeft, currentY, xExEnd, currentY + rH, strokePaint)
                canvas.drawLine(xExFirma, currentY, xExFirma, currentY + rH, strokePaint)
                val pName = if (p.rut.isNotBlank()) "${p.nombre} (${p.rut})" else p.nombre
                canvas.drawText("${idx + 14}. $pName", marginLeft + 5f, currentY + 6.5f, smallTextPaint)
                if (p.firmado) {
                    val fText = if (p.horaFirma.isNotBlank()) "Firmado ${p.horaFirma}" else "Firmado"
                    canvas.drawText(fText, xExFirma + 4f, currentY + 6.5f, smallTextPaint)
                }
                currentY += rH
            }
        }

        currentY += 3f

        if (currentY + footerBlockH > maxContentY) {
            val footerY = pageHeight - 10f
            canvas.drawText("Electricidad y Electrónica · Automatización y Robótica · Tecnologías de Información y Ciberseguridad", marginLeft, footerY, smallTextPaint)
            canvas.drawText("Página $pageNumber", xEnd - 45f, footerY, smallTextPaint)

            document.finishPage(page)

            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(pageInfo)
            canvas = page.canvas
            currentY = 20f
        }

        // 8. Fila RESPONSABLE DE TRABAJOS: y FIRMA:
        val respH = 14f
        val xFirmaResp = marginLeft + 540f
        canvas.drawRect(marginLeft, currentY, xEnd, currentY + respH, strokePaint)
        canvas.drawLine(xFirmaResp, currentY, xFirmaResp, currentY + respH, strokePaint)

        canvas.drawText("RESPONSABLE DE TRABAJOS:", marginLeft + 4f, currentY + 9.5f, boldPaint)
        val respLines = wrapText(art.docente, textPaint, xFirmaResp - marginLeft - 160f)
        canvas.drawText(respLines.firstOrNull() ?: "", marginLeft + 155f, currentY + 9.5f, textPaint)

        canvas.drawText("FIRMA:", xFirmaResp + 4f, currentY + 9.5f, boldPaint)
        val firmaRespText = if (art.responsableFirmado) "Firmado digitalmente" else ""
        canvas.drawText(firmaRespText, xFirmaResp + 42f, currentY + 9.5f, textPaint)

        currentY += respH + 2f

        // 9. Recuadro OBSERVACIONES:
        val obsH = 24f
        canvas.drawRect(marginLeft, currentY, xEnd, currentY + obsH, strokePaint)
        canvas.drawText("OBSERVACIONES:", marginLeft + 4f, currentY + 7.5f, boldPaint)
        canvas.drawLine(marginLeft + 4f, currentY + 14f, xEnd - 4f, currentY + 14f, strokePaint)
        canvas.drawLine(marginLeft + 4f, currentY + 20f, xEnd - 4f, currentY + 20f, strokePaint)

        // 10. Pie de página
        val footerY = pageHeight - 10f
        canvas.drawText("Electricidad y Electrónica · Automatización y Robótica · Tecnologías de Información y Ciberseguridad", marginLeft, footerY, smallTextPaint)
        canvas.drawText("Versión 1.0 — Sede San Pedro de la Paz", xEnd - 150f, footerY, smallTextPaint)

        document.finishPage(page)

        FileOutputStream(file).use { fos ->
            document.writeTo(fos)
        }
        document.close()

        shareFile(context, file, "application/pdf", fileName)
        file
    }
}

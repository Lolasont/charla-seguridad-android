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
                    // If a single word is wider than maxWidth, break it using breakText
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

        FileOutputStream(file).use { fos ->
            fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
            val sb = StringBuilder()

            sb.append("ANÁLISIS DE RIESGOS DEL TRABAJO (ART);;;\n")
            sb.append("INACAP Sede San Pedro de la Paz;;;\n\n")
            sb.append("Trabajo o Actividad;").append(escapeCsv(art.trabajoActividad)).append("\n")
            sb.append("Especialidad;").append(escapeCsv(art.especialidad)).append("\n")
            sb.append("Fecha;").append(escapeCsv(art.fecha)).append("\n")
            sb.append("Docente;").append(escapeCsv(art.docente)).append("\n")
            sb.append("Lugar;").append(escapeCsv(art.lugar)).append("\n")
            sb.append("Responsable de Trabajos;").append(escapeCsv(if (art.responsableFirmado) "Firmado" else "Pendiente")).append("\n\n")

            sb.append("Ítem;Etapas del trabajo;Riesgos asociados;Medidas de control de riesgos\n")

            etapas.forEachIndexed { index, e ->
                sb.append("${index + 1};")
                    .append(escapeCsv(e.etapa)).append(";")
                    .append(escapeCsv(e.riesgoAsociado)).append(";")
                    .append(escapeCsv(e.medidaControl)).append("\n")
            }

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
        val footerH = 76f // Altura del bloque final Comentarios + Docente

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

        // Columnas de la tabla de asistencia
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

        // Función interna para dibujar el encabezado superior
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
                // Docente con word-wrap
                val docenteLines = wrapText(item.docente, textPaint, pageWidth - 2 * margin - 75f)
                val docenteH = maxOf(20f, maxOf(docenteLines.size, 1) * 11.5f + 7f)
                c.drawRect(margin, y, pageWidth - margin, y + docenteH, strokePaint)
                c.drawText("DOCENTE: ", margin + 6f, y + 13.5f, boldPaint)
                drawLines(c, docenteLines, margin + 64f, y + 13.5f, 11.5f, textPaint)

                y += docenteH

                // Bloque Asignatura, Sección, Hora, Fecha y TEMA (dinámico) + TIPO DE CHARLA
                val tipoCharlaWidth = 145f
                val infoWidth = (pageWidth - 2 * margin) - tipoCharlaWidth
                val splitX = margin + infoWidth

                // Calcular líneas de Tema y Asignatura sin truncar
                val temaLines = wrapText(item.tema, textPaint, infoWidth - 55f)
                val temaH = maxOf(18f, maxOf(temaLines.size, 1) * 11.5f + 7f)
                val blockHeight = 4 * 18f + temaH

                c.drawRect(margin, y, pageWidth - margin, y + blockHeight, strokePaint)
                c.drawLine(splitX, y, splitX, y + blockHeight, strokePaint)

                // Sub-filas de la izquierda
                val rowH = 18f
                for (i in 1..4) {
                    c.drawLine(margin, y + i * rowH, splitX, y + i * rowH, strokePaint)
                }

                // Asignatura
                val asigLines = wrapText(item.asignatura, textPaint, infoWidth - 85f)
                c.drawText("ASIGNATURA: ", margin + 6f, y + 13f, boldPaint)
                drawLines(c, asigLines.take(1), margin + 80f, y + 13f, 11f, textPaint)

                // Sección
                c.drawText("SECCIÓN: ", margin + 6f, y + rowH + 13f, boldPaint)
                c.drawText(item.seccion, margin + 80f, y + rowH + 13f, textPaint)

                // Hora inicio / Hora término
                c.drawText("HORA INICIO: ", margin + 6f, y + 2 * rowH + 13f, boldPaint)
                c.drawText(if (item.fecha.isNotBlank()) "08:30" else "", margin + 75f, y + 2 * rowH + 13f, textPaint)
                c.drawText("HORA TÉRMINO: ", margin + 140f, y + 2 * rowH + 13f, boldPaint)
                c.drawText(if (item.fecha.isNotBlank()) "09:15" else "", margin + 225f, y + 2 * rowH + 13f, textPaint)

                // Fecha
                c.drawText("FECHA: ", margin + 6f, y + 3 * rowH + 13f, boldPaint)
                c.drawText(item.fecha, margin + 80f, y + 3 * rowH + 13f, textPaint)

                // Tema con altura dinámica
                val temaY = y + 4 * rowH
                c.drawText("TEMA: ", margin + 6f, temaY + 13f, boldPaint)
                drawLines(c, temaLines, margin + 50f, temaY + 13f, 11.5f, textPaint)

                // Lado Derecho: Tipos de Charla con casilleros
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

            // Título de la Tabla de Asistencia
            val tableTitleH = 16f
            c.drawRect(margin, y, pageWidth - margin, y + tableTitleH, strokePaint)
            val tableCenterX = margin + (pageWidth - 2 * margin) / 2f
            c.drawText("ASISTENCIA", tableCenterX, y + 12f, headerPaint)
            y += tableTitleH

            // Encabezado de Columnas
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

        // Dibujar asistentes con cálculo de altura dinámico y paginación
        for (i in listaAsistentes.indices) {
            val asistente = listaAsistentes[i]
            val isLast = (i == listaAsistentes.lastIndex)

            // Nombre y Procedencia externa en segunda línea
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

            // Verificar si cabe en la página actual
            val neededHeight = rowHeight + (if (isLast) footerH + 10f else 0f)
            if (currentY + neededHeight > maxContentY) {
                // Pie de página con número
                canvas.drawText("Página $pageNumber", pageWidth - margin - 50f, pageHeight - margin / 2f, textPaint)
                document.finishPage(page)

                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas

                // Iniciar nueva página con encabezado repetido
                currentY = drawCharlaHeader(canvas, isFirstPage = false)
            }

            // Dibujar fila
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

        // Dibujar Bloque Final (Comentarios y Docente) en la última página
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
    // ART - PDF (A4 Horizontal: 842 x 595 puntos)
    // =========================================================================
    suspend fun exportarArtPdf(context: Context, artConEtapas: ArtConEtapas): File = withContext(Dispatchers.IO) {
        val fileName = "art_${getTimestampString()}.pdf"
        val file = File(getExportsDir(context), fileName)
        val art = artConEtapas.art
        val etapas = artConEtapas.etapas.sortedBy { it.orden }

        val document = PdfDocument()
        val pageWidth = 842
        val pageHeight = 595
        val margin = 36f
        val maxContentY = pageHeight - margin - 15f
        val footerH = 75f

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
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        // Dimensiones de columnas de la tabla ART
        val colItemW = 42f
        val colEtapaW = 230f
        val colRiesgoW = 210f
        val colControlW = (pageWidth - 2 * margin) - (colItemW + colEtapaW + colRiesgoW)

        val xItem = margin
        val xEtapa = xItem + colItemW
        val xRiesgo = xEtapa + colEtapaW
        val xControl = xRiesgo + colRiesgoW
        val xEnd = pageWidth - margin

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        fun drawArtHeader(c: Canvas, isFirstPage: Boolean): Float {
            var y = margin

            val headerBoxHeight = 52f
            c.drawRect(margin, y, pageWidth - margin, y + headerBoxHeight, strokePaint)

            val logoWidth = 140f
            c.drawLine(margin + logoWidth, y, margin + logoWidth, y + headerBoxHeight, strokePaint)

            fillPaint.color = Color.rgb(194, 0, 0)
            c.drawRect(margin + 14f, y + 12f, margin + 24f, y + 22f, fillPaint)
            fillPaint.color = Color.BLACK

            boldPaint.textSize = 13f
            c.drawText("INACAP", margin + 28f, y + 22f, boldPaint)
            boldPaint.textSize = 9.5f

            textPaint.textSize = 7.5f
            c.drawText("SAN PEDRO DE LA PAZ", margin + 14f, y + 36f, textPaint)
            textPaint.textSize = 9f

            val titleCenterX = margin + logoWidth + (pageWidth - margin - (margin + logoWidth)) / 2f
            c.drawText("ANÁLISIS DE RIESGOS DEL TRABAJO (ART)", titleCenterX, y + 32f, titlePaint)

            y += headerBoxHeight + 8f

            if (isFirstPage) {
                // Fila 1: Trabajo o Actividad, Especialidad, Fecha con word-wrap
                val xCol2 = margin + 350f
                val xCol3 = margin + 580f

                val trabajoLines = wrapText(art.trabajoActividad, textPaint, xCol2 - margin - 135f)
                val espLines = wrapText(art.especialidad, textPaint, xCol3 - xCol2 - 90f)
                val fechaLines = wrapText(art.fecha, textPaint, xEnd - xCol3 - 50f)

                val row1Lines = maxOf(trabajoLines.size, espLines.size, fechaLines.size, 1)
                val row1Height = maxOf(22f, row1Lines * 11.5f + 8f)

                c.drawRect(margin, y, xEnd, y + row1Height, strokePaint)
                c.drawLine(xCol2, y, xCol2, y + row1Height, strokePaint)
                c.drawLine(xCol3, y, xCol3, y + row1Height, strokePaint)

                c.drawText("TRABAJO O ACTIVIDAD:", margin + 6f, y + 14f, boldPaint)
                drawLines(c, trabajoLines, margin + 130f, y + 14f, 11.5f, textPaint)

                c.drawText("ESPECIALIDAD:", xCol2 + 6f, y + 14f, boldPaint)
                drawLines(c, espLines, xCol2 + 85f, y + 14f, 11.5f, textPaint)

                c.drawText("FECHA:", xCol3 + 6f, y + 14f, boldPaint)
                drawLines(c, fechaLines, xCol3 + 45f, y + 14f, 11.5f, textPaint)

                y += row1Height

                // Fila 2: Docente, Sala / Taller / Laboratorio con word-wrap
                val xSplit2 = margin + 400f
                val docenteLines = wrapText(art.docente, textPaint, xSplit2 - margin - 70f)
                val lugarLines = wrapText(art.lugar, textPaint, xEnd - xSplit2 - 230f)

                val row2Lines = maxOf(docenteLines.size, lugarLines.size, 1)
                val row2Height = maxOf(22f, row2Lines * 11.5f + 8f)

                c.drawRect(margin, y, xEnd, y + row2Height, strokePaint)
                c.drawLine(xSplit2, y, xSplit2, y + row2Height, strokePaint)

                c.drawText("DOCENTE:", margin + 6f, y + 14f, boldPaint)
                drawLines(c, docenteLines, margin + 65f, y + 14f, 11.5f, textPaint)

                c.drawText("SALA / TALLER / LABORATORIO / TERRENO:", xSplit2 + 6f, y + 14f, boldPaint)
                drawLines(c, lugarLines, xSplit2 + 225f, y + 14f, 11.5f, textPaint)

                y += row2Height + 10f
            }

            // Encabezado de Tabla
            val tableHeaderH = 18f
            c.drawRect(margin, y, xEnd, y + tableHeaderH, strokePaint)
            c.drawLine(xEtapa, y, xEtapa, y + tableHeaderH, strokePaint)
            c.drawLine(xRiesgo, y, xRiesgo, y + tableHeaderH, strokePaint)
            c.drawLine(xControl, y, xControl, y + tableHeaderH, strokePaint)

            c.drawText("ÍTEM", xItem + 8f, y + 12.5f, boldPaint)
            c.drawText("ETAPAS DEL TRABAJO", xEtapa + 8f, y + 12.5f, boldPaint)
            c.drawText("RIESGOS ASOCIADOS", xRiesgo + 8f, y + 12.5f, boldPaint)
            c.drawText("MEDIDAS DE CONTROL DE RIESGOS", xControl + 8f, y + 12.5f, boldPaint)

            y += tableHeaderH
            return y
        }

        var currentY = drawArtHeader(canvas, isFirstPage = true)

        // Dibujar filas de etapas dinámicas
        for (i in etapas.indices) {
            val etapa = etapas[i]
            val isLast = (i == etapas.lastIndex)

            val etapaLines = wrapText(etapa.etapa, textPaint, colEtapaW - 14f)
            val riesgoLines = wrapText(etapa.riesgoAsociado, textPaint, colRiesgoW - 14f)
            val controlLines = wrapText(etapa.medidaControl, textPaint, colControlW - 14f)

            val maxLines = maxOf(etapaLines.size, riesgoLines.size, controlLines.size, 1)
            val rowHeight = maxOf(30f, maxLines * 12f + 8f)

            // Paginación si no cabe
            val neededHeight = rowHeight + (if (isLast) footerH + 10f else 0f)
            if (currentY + neededHeight > maxContentY) {
                // Pie institucional
                val footerY = pageHeight - margin / 2f
                textPaint.textSize = 7.5f
                canvas.drawText("Electricidad y Electrónica • Automatización y Robótica • Tecnologías de Información y Ciberseguridad", margin, footerY, textPaint)
                canvas.drawText("Página $pageNumber", xEnd - 50f, footerY, textPaint)
                textPaint.textSize = 9f

                document.finishPage(page)

                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas

                currentY = drawArtHeader(canvas, isFirstPage = false)
            }

            canvas.drawRect(margin, currentY, xEnd, currentY + rowHeight, strokePaint)
            canvas.drawLine(xEtapa, currentY, xEtapa, currentY + rowHeight, strokePaint)
            canvas.drawLine(xRiesgo, currentY, xRiesgo, currentY + rowHeight, strokePaint)
            canvas.drawLine(xControl, currentY, xControl, currentY + rowHeight, strokePaint)

            canvas.drawText("${i + 1}.", xItem + 12f, currentY + 14f, textPaint)
            drawLines(canvas, etapaLines, xEtapa + 8f, currentY + 14f, 12f, textPaint)
            drawLines(canvas, riesgoLines, xRiesgo + 8f, currentY + 14f, 12f, textPaint)
            drawLines(canvas, controlLines, xControl + 8f, currentY + 14f, 12f, textPaint)

            currentY += rowHeight
        }

        currentY += 10f

        // Bloque Final (Responsable y Observaciones) en la última página
        if (currentY + footerH > maxContentY) {
            val footerY = pageHeight - margin / 2f
            textPaint.textSize = 7.5f
            canvas.drawText("Electricidad y Electrónica • Automatización y Robótica • Tecnologías de Información y Ciberseguridad", margin, footerY, textPaint)
            canvas.drawText("Página $pageNumber", xEnd - 50f, footerY, textPaint)
            textPaint.textSize = 9f

            document.finishPage(page)

            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(pageInfo)
            canvas = page.canvas
            currentY = margin + 20f
        }

        // Responsable de Trabajos y Firma
        val respH = 22f
        val xRespSplit = margin + 500f
        canvas.drawRect(margin, currentY, xEnd, currentY + respH, strokePaint)
        canvas.drawLine(xRespSplit, currentY, xRespSplit, currentY + respH, strokePaint)

        canvas.drawText("RESPONSABLE DE TRABAJOS:", margin + 6f, currentY + 14f, boldPaint)
        val respDocenteLines = wrapText(art.docente, textPaint, xRespSplit - margin - 180f)
        drawLines(canvas, respDocenteLines.take(1), margin + 175f, currentY + 14f, 11f, textPaint)

        canvas.drawText("FIRMA:", xRespSplit + 6f, currentY + 14f, boldPaint)
        val firmaRespText = if (art.responsableFirmado) "Firmado digitalmente" else "Pendiente"
        canvas.drawText(firmaRespText, xRespSplit + 55f, currentY + 14f, textPaint)

        currentY += respH + 8f

        // Observaciones
        val obsH = 38f
        canvas.drawRect(margin, currentY, xEnd, currentY + obsH, strokePaint)
        canvas.drawText("OBSERVACIONES:", margin + 6f, currentY + 13f, boldPaint)
        canvas.drawLine(margin + 6f, currentY + 23f, xEnd - 6f, currentY + 23f, strokePaint)
        canvas.drawLine(margin + 6f, currentY + 33f, xEnd - 6f, currentY + 33f, strokePaint)

        // Pie de página institucional
        val footerY = pageHeight - margin / 2f
        textPaint.textSize = 7.5f
        canvas.drawText("Electricidad y Electrónica • Automatización y Robótica • Tecnologías de Información y Ciberseguridad", margin, footerY, textPaint)
        canvas.drawText("Versión 1.0 — Sede San Pedro de la Paz (Página $pageNumber)", xEnd - 220f, footerY, textPaint)

        document.finishPage(page)

        FileOutputStream(file).use { fos ->
            document.writeTo(fos)
        }
        document.close()

        shareFile(context, file, "application/pdf", fileName)
        file
    }
}

package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entities.AsaaConEtapas
import com.example.util.export.ExportHelper
import com.example.ui.theme.*
import com.example.viewmodel.AsaaViewModel
import kotlinx.coroutines.launch

@Composable
fun DetalleASAA(
    asaaConEtapas: AsaaConEtapas,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    asaaViewModel: AsaaViewModel = viewModel()
) {
    BackHandler {
        onVolver()
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    var isExporting by remember { mutableStateOf(false) }

    val art = asaaConEtapas.asaa
    val etapas = asaaConEtapas.etapas.sortedBy { it.orden }
    val personal = asaaConEtapas.personalEjecutante.sortedBy { it.orden }
    val firmadosCount = personal.count { it.firmado }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Navigation & Action Row (Botón Volver + Título + Sincronización)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("detalle_asaa_top_bar"),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onVolver,
                        modifier = Modifier.testTag("btn_volver_historial_asaa")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al Historial",
                            tint = InacapRed
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Detalle de ASAA / ART",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("detalle_asaa_screen_title")
                        )
                        Text(
                            text = "Análisis de Seguridad en el Ámbito de Aprendizaje",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    // Sincronización
                    if (art.sincronizado) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SyncSuccessContainer,
                            modifier = Modifier.border(1.dp, SyncSuccessBorder, RoundedCornerShape(8.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Sincronizado",
                                    tint = SyncSuccessText,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Sincronizado",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SyncSuccessText
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SyncPendingContainer,
                            modifier = Modifier.border(1.dp, SyncPendingBorder, RoundedCornerShape(8.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassEmpty,
                                    contentDescription = "Pendiente",
                                    tint = SyncPendingText,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Pendiente",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SyncPendingText
                                )
                            }
                        }
                    }
                }
            }

            // Card 1: Datos Generales
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Datos Generales",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = InacapRed
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = InacapRed.copy(alpha = 0.08f)
                        ) {
                            Text(
                                text = art.tipoActividad,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = InacapRed,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    // Trabajo o Actividad
                    DetalleItemRow(icon = Icons.Default.Assignment, label = "Trabajo o Actividad", value = art.trabajoActividad)
                    // Especialidad
                    DetalleItemRow(icon = Icons.Default.School, label = "Especialidad", value = art.especialidad)
                    // Docente
                    DetalleItemRow(icon = Icons.Default.Person, label = "Docente / Instructor", value = art.docente)
                    // Asignatura y Sección
                    if (art.asignatura.isNotBlank()) {
                        DetalleItemRow(icon = Icons.Default.MenuBook, label = "Asignatura y Sección", value = "${art.asignatura}${if (art.seccion.isNotBlank()) " — Sección ${art.seccion}" else ""}")
                    }
                    // Lugar
                    DetalleItemRow(icon = Icons.Default.LocationOn, label = "Sala / Taller / Lugar", value = art.lugar)
                    // Fecha y Horas
                    val fechaHoraStr = buildString {
                        append(art.fecha)
                        if (art.horaInicio.isNotBlank() && art.horaTermino.isNotBlank()) {
                            append(" (${art.horaInicio} - ${art.horaTermino} hrs)")
                        }
                    }
                    DetalleItemRow(icon = Icons.Default.CalendarToday, label = "Fecha y Horario", value = fechaHoraStr)
                    // Tipo de Charla
                    if (art.tipoCharla.isNotBlank()) {
                        DetalleItemRow(icon = Icons.Default.Info, label = "Tipo de Charla", value = art.tipoCharla)
                    }
                    // Tema
                    if (art.tema.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "Tema Tratado:", style = MaterialTheme.typography.labelSmall, color = InacapRed, fontWeight = FontWeight.Bold)
                                Text(text = art.tema, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                    // Nivel Técnico
                    DetalleItemRow(icon = Icons.Default.Security, label = "Nivel Técnico", value = art.nivelTecnico)
                }
            }

            // Card 2: Seguridad, Equipos y EPP
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Medidas de Seguridad y EPP",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    if (art.equipoApoyo.isNotBlank()) {
                        DetalleItemRow(icon = Icons.Default.Check, label = "Equipo de Apoyo", value = art.equipoApoyo.replace("|", ", "))
                    }
                    if (art.epp.isNotBlank()) {
                        DetalleItemRow(icon = Icons.Default.Check, label = "Equipos de Protección Personal (EPP)", value = art.epp.replace("|", ", "))
                    }
                    if (art.condicionesAmbientales.isNotBlank()) {
                        DetalleItemRow(icon = Icons.Default.Check, label = "Condiciones Ambientales", value = art.condicionesAmbientales.replace("|", ", "))
                    }
                    if (art.medidasControl.isNotBlank()) {
                        DetalleItemRow(icon = Icons.Default.Check, label = "Medidas de Control Propuestas", value = art.medidasControl.replace("|", ", "))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Bloqueo LOTO", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(text = if (art.loto) "Sí" else "No", fontWeight = FontWeight.SemiBold, color = if (art.loto) InacapRed else MaterialTheme.colorScheme.onSurface)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "ASAA Comprendido", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(text = if (art.asaaComprendido) "Sí" else "No", fontWeight = FontWeight.SemiBold, color = if (art.asaaComprendido) SuccessGreen else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // Card 3: Etapas del Trabajo
            if (etapas.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Etapas del Trabajo (${etapas.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = InacapRed
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                        etapas.forEachIndexed { idx, etapa ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = "${idx + 1}. ${etapa.etapa}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    if (etapa.riesgoAsociado.isNotBlank()) {
                                        Text(text = "Riesgo: ${etapa.riesgoAsociado}", style = MaterialTheme.typography.bodySmall, color = InacapRed)
                                    }
                                    if (etapa.medidaControl.isNotBlank()) {
                                        Text(text = "Medida de Control: ${etapa.medidaControl}", style = MaterialTheme.typography.bodySmall, color = SuccessGreen)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Card 4: Personal Ejecutante & Firmas
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Personal Ejecutante y Firmas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InacapRed
                            )
                            Text(
                                text = "$firmadosCount de ${personal.size} firmados",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (firmadosCount > 0) SuccessGreen else TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SuccessGreenContainer
                        ) {
                            Text(
                                text = "$firmadosCount/${personal.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        personal.forEachIndexed { index, p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Surface(
                                        modifier = Modifier.size(28.dp),
                                        shape = CircleShape,
                                        color = InacapRed.copy(alpha = 0.1f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = "${index + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InacapRed)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = if (p.nombre.isNotBlank()) p.nombre else "Participante ${index + 1}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (p.rut.isNotBlank()) {
                                            Text(text = "RUT: ${p.rut}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                        }
                                    }
                                }

                                if (p.firmado) {
                                    Surface(shape = RoundedCornerShape(8.dp), color = SuccessGreenContainer) {
                                        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(text = "Firmado", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                            }
                                            if (p.horaFirma.isNotBlank()) {
                                                Text(text = "${p.horaFirma} hrs", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = SuccessGreen)
                                            }
                                        }
                                    }
                                } else {
                                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Outlined.Close, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "Sin firmar", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Card 5: Observaciones
            if (art.observaciones.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Observaciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = InacapRed)
                        Text(text = art.observaciones, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            // Card 6: Botones de Exportación (PDF y Excel)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Exportar Informe", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = InacapRed)
                    Text(text = "Genera el documento oficial ASAA / ART en el formato que requieras:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (isExporting) return@Button
                                coroutineScope.launch {
                                    try {
                                        isExporting = true
                                        ExportHelper.exportarAsaaPdf(context, asaaConEtapas)
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Error al generar PDF: ${e.localizedMessage ?: "Error desconocido"}")
                                    } finally {
                                        isExporting = false
                                    }
                                }
                            },
                            enabled = !isExporting,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = InacapRed, contentColor = Color.White)
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Exportar PDF", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                if (isExporting) return@OutlinedButton
                                coroutineScope.launch {
                                    try {
                                        isExporting = true
                                        ExportHelper.exportarAsaaCsv(context, asaaConEtapas)
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Error al generar Excel: ${e.localizedMessage ?: "Error desconocido"}")
                                    } finally {
                                        isExporting = false
                                    }
                                }
                            },
                            enabled = !isExporting,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.TableChart, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Exportar Excel", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun DetalleItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = InacapRed,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(text = value.ifBlank { "No especificado" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TableChart
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
import com.example.data.local.entities.ArtConEtapas
import com.example.model.CONDICIONES_AMBIENTALES_PARES
import com.example.model.MEDIDAS_CONTROL_PROPUESTAS
import com.example.model.OPCIONES_EPP
import com.example.model.OPCIONES_EQUIPO_APOYO
import com.example.model.OPCIONES_NIVEL_TECNICO
import com.example.ui.components.ResponsiveLayout
import com.example.ui.theme.InacapRed
import com.example.ui.theme.SecondaryCharcoal
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SyncPendingBorder
import com.example.ui.theme.SyncPendingContainer
import com.example.ui.theme.SyncPendingText
import com.example.ui.theme.SyncSuccessBorder
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entities.EtapaTrabajoEntity
import com.example.data.local.entities.PersonalEjecutanteEntity
import com.example.ui.theme.SyncSuccessContainer
import com.example.ui.theme.SyncSuccessText
import com.example.ui.theme.TextSecondary
import com.example.util.export.ExportHelper
import com.example.viewmodel.ArtViewModel
import kotlinx.coroutines.launch

private fun sanitizeTag(text: String): String {
    return text.lowercase()
        .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
        .replace("ñ", "n")
        .replace(Regex("[^a-z0-9]"), "_")
        .trim('_')
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetalleASAA(
    artConEtapas: ArtConEtapas,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    artViewModel: ArtViewModel = viewModel()
) {
    BackHandler {
        onVolver()
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    var isExporting by remember { mutableStateOf(false) }

    val art = artConEtapas.art
    val etapasOriginales = remember(artConEtapas.etapas) {
        artConEtapas.etapas.sortedBy { it.orden }
    }
    val personalOriginal = remember(artConEtapas.personalEjecutante) {
        artConEtapas.personalEjecutante.sortedBy { it.orden }
    }

    // ── ESTADOS EDITABLES INICIALIZADOS CON DATOS DEL ASAA ──
    // 2. Datos generales (6 campos)
    var trabajoActividad by remember { mutableStateOf(art.trabajoActividad) }
    var especialidad by remember { mutableStateOf(art.especialidad) }
    var fecha by remember { mutableStateOf(art.fecha) }
    var tipoActividad by remember { mutableStateOf(art.tipoActividad) }
    var docenteExpositor by remember { mutableStateOf(art.docente) }
    var lugar by remember { mutableStateOf(art.lugar) }

    // 3. Equipo de Apoyo (24 checkboxes + Otros)
    val equiposSeleccionados = remember {
        mutableStateMapOf<String, Boolean>().apply {
            val existentes = art.equipoApoyo.split("|").filter { it.isNotBlank() }.toSet()
            OPCIONES_EQUIPO_APOYO.forEach { opcion ->
                this[opcion] = existentes.contains(opcion)
            }
        }
    }
    var equipoApoyoOtros by remember { mutableStateOf(art.equipoApoyoOtros) }

    // 4. Nivel Técnico (3 checkboxes: Inicial, Intermedio, Avanzado)
    var nivelTecnicoSeleccionado by remember { mutableStateOf(art.nivelTecnico.ifBlank { "Inicial" }) }

    // 5. Equipo de Protección Personal (23 checkboxes + Otros)
    val eppSeleccionados = remember {
        mutableStateMapOf<String, Boolean>().apply {
            val existentes = art.epp.split("|").filter { it.isNotBlank() }.toSet()
            OPCIONES_EPP.forEach { opcion ->
                this[opcion] = existentes.contains(opcion)
            }
        }
    }
    var eppOtros by remember { mutableStateOf(art.eppOtros) }

    // 6. Personal Ejecutante (12 filas: nombre + estado/botón firmado)
    val personalNombres = remember {
        mutableStateListOf<String>().apply {
            for (i in 0 until 12) {
                val p = personalOriginal.getOrNull(i)
                add(p?.nombre ?: "")
            }
        }
    }
    val personalFirmados = remember {
        mutableStateListOf<Boolean>().apply {
            for (i in 0 until 12) {
                val p = personalOriginal.getOrNull(i)
                add(p?.firmado ?: false)
            }
        }
    }

    // 7. Condiciones Ambientales (6 pares + Otros)
    val condicionesSeleccionadas = remember {
        mutableStateMapOf<String, Boolean>().apply {
            val existentes = art.condicionesAmbientales.split("|").filter { it.isNotBlank() }.toSet()
            CONDICIONES_AMBIENTALES_PARES.forEach { par ->
                this[par.opcion1] = existentes.contains(par.opcion1)
                this[par.opcion2] = existentes.contains(par.opcion2)
            }
        }
    }
    var condicionesOtros by remember { mutableStateOf("") }

    // 8. Medidas de Control Propuestas (8 checkboxes + Otro)
    val medidasControlSeleccionadas = remember {
        mutableStateMapOf<String, Boolean>().apply {
            MEDIDAS_CONTROL_PROPUESTAS.forEachIndexed { idx, medida ->
                this[medida] = (idx < 4) // primeras medidas pre-activadas o según contexto
            }
        }
    }
    var medidasControlOtro by remember { mutableStateOf("") }

    // 9, 10, 11. Radios Sí/No
    var charlaSeguridadSi by remember { mutableStateOf(art.charlaSeguridadInicial) }
    var valoracionAsaaSi by remember { mutableStateOf(true) }
    var bloqueoLotoSi by remember { mutableStateOf(false) }

    // 12. Tabla de Etapas del Trabajo (máx 6 filas, cada una con Etapa, Riesgo, Control)
    val etapasNombres = remember {
        mutableStateListOf<String>().apply {
            for (i in 0 until 6) {
                val e = etapasOriginales.getOrNull(i)
                add(e?.etapa ?: "")
            }
        }
    }
    val etapasRiesgos = remember {
        mutableStateListOf<String>().apply {
            for (i in 0 until 6) {
                val e = etapasOriginales.getOrNull(i)
                add(e?.riesgoAsociado ?: "")
            }
        }
    }
    val etapasControles = remember {
        mutableStateListOf<String>().apply {
            for (i in 0 until 6) {
                val e = etapasOriginales.getOrNull(i)
                add(e?.medidaControl ?: "")
            }
        }
    }

    // 13. Observaciones y firma del responsable
    var docenteResponsable by remember { mutableStateOf(art.docente) }
    var responsableFirmado by remember { mutableStateOf(art.responsableFirmado) }
    var observaciones by remember { mutableStateOf("Actividad académica ejecutada conforme a protocolos de seguridad INACAP San Pedro de la Paz.") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ResponsiveLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalSpacing = 16.dp,
            horizontalSpacing = 16.dp
        ) {
            // ── TOP NAVIGATION BAR ──
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
                            text = "Detalle de ASAA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("detalle_asaa_screen_title")
                        )
                        Text(
                            text = "Análisis Seguro de la Actividad Académica",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    if (art.sincronizado) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SyncSuccessContainer,
                            modifier = Modifier
                                .border(1.dp, SyncSuccessBorder, RoundedCornerShape(8.dp))
                                .testTag("tag_sincronizado_detalle_asaa")
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
                            modifier = Modifier
                                .border(1.dp, SyncPendingBorder, RoundedCornerShape(8.dp))
                                .testTag("tag_pendiente_sincronizar_detalle_asaa")
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

            // ════════════════════════════════════════════════════════════════════
            // 1. CABECERA – Logo + título “Análisis Seguro de la Actividad Académica (ASAA)”
            // ════════════════════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tag_card_cabecera"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo INACAP estilizado
                    Surface(
                        modifier = Modifier
                            .size(54.dp)
                            .testTag("tag_cabecera_logo"),
                        shape = RoundedCornerShape(12.dp),
                        color = InacapRed
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "INACAP",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Análisis Seguro de la Actividad Académica (ASAA)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = InacapRed,
                            modifier = Modifier.testTag("tag_cabecera_titulo")
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Sede San Pedro de la Paz · Prevención de Riesgos",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // ════════════════════════════════════════════════════════════════════
            // 2. DATOS GENERALES – 6 OutlinedTextField
            // ════════════════════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tag_card_datos_generales"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Datos Generales",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )

                    // 1. Trabajo/Actividad
                    OutlinedTextField(
                        value = trabajoActividad,
                        onValueChange = { trabajoActividad = it },
                        label = { Text("Trabajo / Actividad") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tag_datos_generales_trabajo_actividad"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed
                        )
                    )

                    // 2. Especialidad
                    OutlinedTextField(
                        value = especialidad,
                        onValueChange = { especialidad = it },
                        label = { Text("Especialidad") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tag_datos_generales_especialidad"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed
                        )
                    )

                    // 3. Fecha
                    OutlinedTextField(
                        value = fecha,
                        onValueChange = { fecha = it },
                        label = { Text("Fecha") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tag_datos_generales_fecha"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed
                        )
                    )

                    // 4. Tipo de Actividad
                    OutlinedTextField(
                        value = tipoActividad,
                        onValueChange = { tipoActividad = it },
                        label = { Text("Tipo de Actividad (Teórica / Práctica)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tag_datos_generales_tipo_actividad"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed
                        )
                    )

                    // 5. Docente/Expositor
                    OutlinedTextField(
                        value = docenteExpositor,
                        onValueChange = { docenteExpositor = it },
                        label = { Text("Docente / Expositor") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tag_datos_generales_docente_expositor"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed
                        )
                    )

                    // 6. Sala/Taller/Laboratorio/Terreno
                    OutlinedTextField(
                        value = lugar,
                        onValueChange = { lugar = it },
                        label = { Text("Sala / Taller / Laboratorio / Terreno") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tag_datos_generales_lugar"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed
                        )
                    )
                }
            }

            // ════════════════════════════════════════════════════════════════════
            // 3. EQUIPO DE APOYO – 24 Checkboxes + campo "Otros"
            // ════════════════════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tag_card_equipo_apoyo"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = InacapRed, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Equipo de Apoyo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = InacapRed
                        )
                    }
                    Text(
                        text = "Seleccione los equipos, redes y herramientas requeridas (${OPCIONES_EQUIPO_APOYO.size} opciones):",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    // 24 checkboxes organizados en filas
                    OPCIONES_EQUIPO_APOYO.forEach { opcion ->
                        val checked = equiposSeleccionados[opcion] ?: false
                        val tagSlug = sanitizeTag(opcion)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { equiposSeleccionados[opcion] = !checked }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { equiposSeleccionados[opcion] = it },
                                modifier = Modifier.testTag("tag_equipo_apoyo_$tagSlug"),
                                colors = CheckboxDefaults.colors(checkedColor = InacapRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = opcion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Campo "Otros"
                    OutlinedTextField(
                        value = equipoApoyoOtros,
                        onValueChange = { equipoApoyoOtros = it },
                        label = { Text("Otros equipos de apoyo") },
                        placeholder = { Text("Especifique otros equipos...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tag_equipo_apoyo_otros"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed
                        )
                    )
                }
            }

            // ════════════════════════════════════════════════════════════════════
            // 4. NIVEL TÉCNICO – 3 Checkboxes (Inicial, Intermedio, Avanzado)
            // ════════════════════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tag_card_nivel_tecnico"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Nivel Técnico",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )
                    Text(
                        text = "Seleccione el nivel de competencia exigido:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OPCIONES_NIVEL_TECNICO.forEach { nivel ->
                            val isChecked = nivelTecnicoSeleccionado.equals(nivel, ignoreCase = true)
                            val tag = when (nivel) {
                                "Inicial" -> "tag_nivel_tecnico_inicial"
                                "Intermedio" -> "tag_nivel_tecnico_intermedio"
                                else -> "tag_nivel_tecnico_avanzado"
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { nivelTecnicoSeleccionado = nivel }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { if (it) nivelTecnicoSeleccionado = nivel },
                                    modifier = Modifier.testTag(tag),
                                    colors = CheckboxDefaults.colors(checkedColor = InacapRed)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = nivel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // ════════════════════════════════════════════════════════════════════
            // 5. EQUIPO DE PROTECCIÓN PERSONAL (EPP) – 23 Checkboxes + campo "Otros"
            // ════════════════════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tag_card_epp"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = InacapRed, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Equipo de Protección Personal (EPP)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = InacapRed
                        )
                    }
                    Text(
                        text = "Seleccione los elementos de protección personal (${OPCIONES_EPP.size} opciones):",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    // 23 checkboxes
                    OPCIONES_EPP.forEach { epp ->
                        val checked = eppSeleccionados[epp] ?: false
                        val tagSlug = sanitizeTag(epp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { eppSeleccionados[epp] = !checked }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { eppSeleccionados[epp] = it },
                                modifier = Modifier.testTag("tag_epp_$tagSlug"),
                                colors = CheckboxDefaults.colors(checkedColor = InacapRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = epp,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Campo "Otros" EPP
                    OutlinedTextField(
                        value = eppOtros,
                        onValueChange = { eppOtros = it },
                        label = { Text("Otros elementos de EPP") },
                        placeholder = { Text("Especifique otros EPP...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tag_epp_otros"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed
                        )
                    )
                }
            }

            // ════════════════════════════════════════════════════════════════════
            // 6. PERSONAL EJECUTANTE – 12 filas (número, OutlinedTextField, Botón/Surface)
            // ════════════════════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tag_card_personal_ejecutante"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = InacapRed, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Personal Ejecutante",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = InacapRed
                        )
                    }
                    Text(
                        text = "12 cupos para alumnos/participantes y confirmación de firma:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    for (i in 1..12) {
                        val index = i - 1
                        val nombre = personalNombres[index]
                        val firmado = personalFirmados[index]

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "$i.",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(24.dp)
                            )

                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { personalNombres[index] = it },
                                placeholder = { Text("Nombre alumno $i") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("tag_personal_ejecutante_nombre_$i"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = InacapRed,
                                    focusedLabelColor = InacapRed
                                )
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (firmado) SuccessGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clickable { personalFirmados[index] = !firmado }
                                    .border(
                                        1.dp,
                                        if (firmado) SuccessGreen else MaterialTheme.colorScheme.outlineVariant,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .testTag("tag_personal_ejecutante_firma_$i")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (firmado) Icons.Outlined.CheckCircle else Icons.Outlined.Close,
                                        contentDescription = if (firmado) "Firmado" else "Sin firmar",
                                        tint = if (firmado) SuccessGreen else TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (firmado) "Firmado" else "Sin firmar",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (firmado) SuccessGreen else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ════════════════════════════════════════════════════════════════════
            // 7. CONDICIONES AMBIENTALES – Pares de checkboxes (6 pares) + campo "Otros"
            // ════════════════════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tag_card_condiciones_ambientales"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Condiciones Ambientales",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )
                    Text(
                        text = "Factores de entorno laboral clasificados en 6 pares:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    CONDICIONES_AMBIENTALES_PARES.forEach { par ->
                        val check1 = condicionesSeleccionadas[par.opcion1] ?: false
                        val check2 = condicionesSeleccionadas[par.opcion2] ?: false
                        val slug1 = sanitizeTag(par.opcion1)
                        val slug2 = sanitizeTag(par.opcion2)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { condicionesSeleccionadas[par.opcion1] = !check1 },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = check1,
                                    onCheckedChange = { condicionesSeleccionadas[par.opcion1] = it },
                                    modifier = Modifier.testTag("tag_condicion_ambiental_$slug1"),
                                    colors = CheckboxDefaults.colors(checkedColor = InacapRed)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(par.opcion1, style = MaterialTheme.typography.bodySmall)
                            }

                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { condicionesSeleccionadas[par.opcion2] = !check2 },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = check2,
                                    onCheckedChange = { condicionesSeleccionadas[par.opcion2] = it },
                                    modifier = Modifier.testTag("tag_condicion_ambiental_$slug2"),
                                    colors = CheckboxDefaults.colors(checkedColor = InacapRed)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(par.opcion2, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    // Campo "Otros"
                    OutlinedTextField(
                        value = condicionesOtros,
                        onValueChange = { condicionesOtros = it },
                        label = { Text("Otras condiciones ambientales") },
                        placeholder = { Text("Especifique...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tag_condiciones_ambientales_otros"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed
                        )
                    )
                }
            }

            // ════════════════════════════════════════════════════════════════════
            // 8. MEDIDAS DE CONTROL PROPUESTAS – Checklist de 8 items + "Otro"
            // ════════════════════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tag_card_medidas_control"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Medidas de Control Propuestas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )
                    Text(
                        text = "Seleccione las medidas de prevención implementadas:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    MEDIDAS_CONTROL_PROPUESTAS.forEachIndexed { idx, medida ->
                        val checked = medidasControlSeleccionadas[medida] ?: false
                        val tagNumber = idx + 1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { medidasControlSeleccionadas[medida] = !checked }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { medidasControlSeleccionadas[medida] = it },
                                modifier = Modifier.testTag("tag_medida_control_$tagNumber"),
                                colors = CheckboxDefaults.colors(checkedColor = InacapRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$tagNumber. $medida",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Campo "Otro"
                    OutlinedTextField(
                        value = medidasControlOtro,
                        onValueChange = { medidasControlOtro = it },
                        label = { Text("Otras medidas de control") },
                        placeholder = { Text("Especifique otra medida...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tag_medida_control_otro"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed
                        )
                    )
                }
            }

            // ════════════════════════════════════════════════════════════════════
            // 9. CHARLA DE SEGURIDAD INICIAL – Radio-buttons Sí / No
            // ════════════════════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tag_card_charla_seguridad_inicial"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "9. Charla de Seguridad Inicial",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )
                    Text(
                        text = "¿Se dictó la charla de seguridad previa al inicio de actividades?",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { charlaSeguridadSi = true }
                        ) {
                            RadioButton(
                                selected = charlaSeguridadSi,
                                onClick = { charlaSeguridadSi = true },
                                modifier = Modifier.testTag("tag_charla_seguridad_si"),
                                colors = RadioButtonDefaults.colors(selectedColor = InacapRed)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sí", fontWeight = FontWeight.SemiBold)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { charlaSeguridadSi = false }
                        ) {
                            RadioButton(
                                selected = !charlaSeguridadSi,
                                onClick = { charlaSeguridadSi = false },
                                modifier = Modifier.testTag("tag_charla_seguridad_no"),
                                colors = RadioButtonDefaults.colors(selectedColor = InacapRed)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("No", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // ════════════════════════════════════════════════════════════════════
            // 10. VALORACIÓN DEL ASAA – Radio-buttons Sí / No
            // ════════════════════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tag_card_valoracion_asaa"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "10. Valoración del ASAA",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )
                    Text(
                        text = "¿Los participantes comprendieron y valoraron los riesgos del ASAA?",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { valoracionAsaaSi = true }
                        ) {
                            RadioButton(
                                selected = valoracionAsaaSi,
                                onClick = { valoracionAsaaSi = true },
                                modifier = Modifier.testTag("tag_valoracion_asaa_si"),
                                colors = RadioButtonDefaults.colors(selectedColor = InacapRed)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sí", fontWeight = FontWeight.SemiBold)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { valoracionAsaaSi = false }
                        ) {
                            RadioButton(
                                selected = !valoracionAsaaSi,
                                onClick = { valoracionAsaaSi = false },
                                modifier = Modifier.testTag("tag_valoracion_asaa_no"),
                                colors = RadioButtonDefaults.colors(selectedColor = InacapRed)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("No", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // ════════════════════════════════════════════════════════════════════
            // 11. BLOQUEO / ETIQUETADO (LOTO) – Radio-buttons Sí / No
            // ════════════════════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tag_card_bloqueo_loto"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "11. Bloqueo / Etiquetado (LOTO)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )
                    Text(
                        text = "¿Se requiere o aplicó procedimiento de bloqueo y etiquetado (LOTO)?",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { bloqueoLotoSi = true }
                        ) {
                            RadioButton(
                                selected = bloqueoLotoSi,
                                onClick = { bloqueoLotoSi = true },
                                modifier = Modifier.testTag("tag_bloqueo_loto_si"),
                                colors = RadioButtonDefaults.colors(selectedColor = InacapRed)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sí", fontWeight = FontWeight.SemiBold)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { bloqueoLotoSi = false }
                        ) {
                            RadioButton(
                                selected = !bloqueoLotoSi,
                                onClick = { bloqueoLotoSi = false },
                                modifier = Modifier.testTag("tag_bloqueo_loto_no"),
                                colors = RadioButtonDefaults.colors(selectedColor = InacapRed)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("No", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // ════════════════════════════════════════════════════════════════════
            // 12. TABLA DE ETAPAS DEL TRABAJO – Máx 6 filas (Etapa, Riesgo, Control)
            // ════════════════════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tag_card_etapas_trabajo"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "12. Etapas del Trabajo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )
                    Text(
                        text = "Secuencia de tareas (máximo 6 etapas con riesgo y medidas de control):",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    for (i in 1..6) {
                        val index = i - 1
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Etapa #$i",
                                fontWeight = FontWeight.Bold,
                                color = InacapRed,
                                style = MaterialTheme.typography.labelLarge
                            )

                            // Etapa
                            OutlinedTextField(
                                value = etapasNombres[index],
                                onValueChange = { etapasNombres[index] = it },
                                label = { Text("Etapa $i") },
                                placeholder = { Text("Descripción del paso") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("tag_etapa_trabajo_nombre_$i"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = InacapRed,
                                    focusedLabelColor = InacapRed
                                )
                            )

                            // Riesgo asociado
                            OutlinedTextField(
                                value = etapasRiesgos[index],
                                onValueChange = { etapasRiesgos[index] = it },
                                label = { Text("Riesgo asociado") },
                                placeholder = { Text("Peligros identificados") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("tag_etapa_trabajo_riesgo_$i"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = InacapRed,
                                    focusedLabelColor = InacapRed
                                )
                            )

                            // Medida de control
                            OutlinedTextField(
                                value = etapasControles[index],
                                onValueChange = { etapasControles[index] = it },
                                label = { Text("Medida de control") },
                                placeholder = { Text("Acción preventiva obligatoria") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("tag_etapa_trabajo_control_$i"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = InacapRed,
                                    focusedLabelColor = InacapRed
                                )
                            )
                        }
                    }
                }
            }

            // ════════════════════════════════════════════════════════════════════
            // 13. OBSERVACIONES Y FIRMA DEL RESPONSABLE
            // ════════════════════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tag_card_observaciones_firma_responsable"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "13. Observaciones y Firma del Responsable",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )

                    // Docente responsable
                    OutlinedTextField(
                        value = docenteResponsable,
                        onValueChange = { docenteResponsable = it },
                        label = { Text("Docente Responsable del Trabajo") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tag_responsable_docente"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed
                        )
                    )

                    // Botón / Surface para firmar al responsable
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (responsableFirmado) SuccessGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { responsableFirmado = !responsableFirmado }
                            .border(
                                1.dp,
                                if (responsableFirmado) SuccessGreen else MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .testTag("tag_responsable_firma_btn")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (responsableFirmado) Icons.Outlined.CheckCircle else Icons.Default.Lock,
                                    contentDescription = "Firma docente",
                                    tint = if (responsableFirmado) SuccessGreen else InacapRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (responsableFirmado) "Docente Responsable Firmó" else "Firmar como Docente Responsable",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (responsableFirmado) SuccessGreen else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (responsableFirmado) "Firma digital validada" else "Toque para alternar o registrar la firma",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Text(
                                text = if (responsableFirmado) "FIRMADO" else "PENDIENTE",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = if (responsableFirmado) SuccessGreen else TextSecondary
                            )
                        }
                    }

                    // OutlinedTextField multilínea para observaciones
                    OutlinedTextField(
                        value = observaciones,
                        onValueChange = { observaciones = it },
                        label = { Text("Observaciones") },
                        placeholder = { Text("Ingrese observaciones o compromisos del trabajo...") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tag_responsable_observaciones"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed
                        )
                    )
                }
            }

            // ── GUARDAR CAMBIOS EN DETALLE ASAA ──
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_detalle_asaa_guardar"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Guardar Cambios del ASAA",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )
                    Text(
                        text = "Guarda cualquier actualización realizada en los campos, checkboxes, etapas o firmas del registro.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Button(
                        onClick = {
                            val equipoStr = equiposSeleccionados.filter { it.value }.keys.joinToString("|")
                            val eppStr = eppSeleccionados.filter { it.value }.keys.joinToString("|")
                            val condStr = condicionesSeleccionadas.filter { it.value }.keys.joinToString("|")

                            val etapasEntities = etapasNombres.mapIndexed { index, nombre ->
                                EtapaTrabajoEntity(
                                    id = etapasOriginales.getOrNull(index)?.id ?: java.util.UUID.randomUUID().toString(),
                                    artId = art.id,
                                    etapa = nombre.trim(),
                                    riesgoAsociado = etapasRiesgos.getOrNull(index)?.trim() ?: "",
                                    medidaControl = etapasControles.getOrNull(index)?.trim() ?: "",
                                    orden = index + 1
                                )
                            }.filter { it.etapa.isNotBlank() || it.riesgoAsociado.isNotBlank() || it.medidaControl.isNotBlank() }

                            val personalEntities = personalNombres.mapIndexed { index, nombre ->
                                PersonalEjecutanteEntity(
                                    id = personalOriginal.getOrNull(index)?.id ?: java.util.UUID.randomUUID().toString(),
                                    artId = art.id,
                                    nombre = nombre.trim(),
                                    rut = personalOriginal.getOrNull(index)?.rut ?: "",
                                    firmado = personalFirmados.getOrNull(index) ?: false,
                                    horaFirma = personalOriginal.getOrNull(index)?.horaFirma ?: "",
                                    orden = index + 1
                                )
                            }.filter { it.nombre.isNotBlank() }

                            artViewModel.actualizarArtExistente(
                                artId = art.id,
                                trabajoActividad = trabajoActividad,
                                especialidad = especialidad,
                                fecha = fecha,
                                docente = docenteExpositor,
                                lugar = lugar,
                                tipoActividad = tipoActividad,
                                nivelTecnico = nivelTecnicoSeleccionado,
                                equipoApoyo = equipoStr,
                                equipoApoyoOtros = equipoApoyoOtros,
                                epp = eppStr,
                                eppOtros = eppOtros,
                                condicionesAmbientales = condStr,
                                condicionesAmbientalesOtros = condicionesOtros,
                                charlaSeguridadInicial = charlaSeguridadSi,
                                responsableFirmado = responsableFirmado,
                                etapas = etapasEntities,
                                personal = personalEntities
                            )

                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Cambios guardados con éxito en ASAA")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_guardar_cambios_detalle_asaa"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = InacapRed,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Guardar Cambios",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guardar Cambios",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // ── EXPORTACIÓN (PDF / Excel) ──
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_detalle_asaa_acciones_exportar"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Exportar ASAA",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )
                    Text(
                        text = "Genera el documento oficial de Análisis Seguro de la Actividad Académica:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

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
                                        ExportHelper.exportarAsaaPdf(context, artConEtapas)
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
                                .height(50.dp)
                                .testTag("btn_exportar_pdf_asaa"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = InacapRed,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "Exportar a PDF",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Exportar PDF",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                if (isExporting) return@OutlinedButton
                                coroutineScope.launch {
                                    try {
                                        isExporting = true
                                        ExportHelper.exportarAsaaCsv(context, artConEtapas)
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
                                .height(50.dp)
                                .testTag("btn_exportar_excel_asaa"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SecondaryCharcoal
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = "Exportar a Excel",
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Exportar Excel",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
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

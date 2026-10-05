package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.EtapaTrabajo
import com.example.model.OPCIONES_COMANDO_VOZ
import com.example.model.OPCIONES_CONDICIONES_AMBIENTALES
import com.example.model.OPCIONES_EPP
import com.example.model.OPCIONES_EQUIPO_APOYO
import com.example.model.OPCIONES_RIESGOS
import com.example.model.PersonalEjecutante
import com.example.ui.theme.InacapRed
import com.example.ui.theme.InacapRedContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ArtViewModel
import com.example.viewmodel.CharlasViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArtScreen(
    modifier: Modifier = Modifier,
    viewModel: ArtViewModel = viewModel(),
    charlasViewModel: CharlasViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val isCerrado = formState.isCerrado

    val charlasDisponibles by charlasViewModel.historial.collectAsStateWithLifecycle()

    val calendar = remember { Calendar.getInstance() }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val nuevaFecha = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year)
                viewModel.updateFecha(nuevaFecha)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    var showSummaryDialog by rememberSaveable { mutableStateOf(false) }
    var showVincularCharlaDialog by remember { mutableStateOf(false) }

    // Estados plegables para secciones con casilleros (parten cerradas)
    var expandEquipoApoyo by rememberSaveable { mutableStateOf(false) }
    var expandEpp by rememberSaveable { mutableStateOf(false) }
    var expandComandoVoz by rememberSaveable { mutableStateOf(false) }
    var expandCondicionesAmbientales by rememberSaveable { mutableStateOf(false) }

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
            // Banner ART Cerrado
            AnimatedVisibility(visible = isCerrado) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("banner_art_cerrado"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = InacapRedContainer.copy(alpha = 0.6f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(42.dp),
                            shape = CircleShape,
                            color = InacapRed
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Bloqueado",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ART cerrado — ya no se puede modificar",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = InacapRed
                            )
                            Text(
                                text = "Los datos han sido incorporados al Historial.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Top Header Card con Botón "Duplicar ART anterior"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("art_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(50.dp),
                            shape = CircleShape,
                            color = InacapRed.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "ART",
                                    tint = InacapRed,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ART",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.testTag("art_title")
                            )
                            Text(
                                text = if (isCerrado) "Estado: Cerrado y Guardado" else "Análisis de Riesgo de la Tarea",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isCerrado) InacapRed else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isCerrado) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }

                        if (isCerrado) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = InacapRed.copy(alpha = 0.12f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = InacapRed,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Bloqueado",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = InacapRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Botón "Duplicar ART anterior" (disponible cuando no está cerrado)
                    if (!isCerrado) {
                        OutlinedButton(
                            onClick = {
                                val duplicado = viewModel.duplicarArtAnterior()
                                coroutineScope.launch {
                                    if (duplicado) {
                                        snackbarHostState.showSnackbar("Campos del último ART copiados con éxito (fecha actualizada a hoy)")
                                    } else {
                                        snackbarHostState.showSnackbar("No hay un ART previo registrado para duplicar")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_duplicar_art_anterior"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = InacapRed
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Duplicar ART anterior",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Section 1: Datos Generales
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_art_datos_generales"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Datos Generales",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )

                    // Trabajo o Actividad
                    OutlinedTextField(
                        value = formState.trabajoActividad,
                        onValueChange = { viewModel.updateTrabajoActividad(it) },
                        enabled = !isCerrado,
                        label = { Text("Trabajo o Actividad *") },
                        placeholder = { Text("Ej: Desarme y montaje de culata de motor Diésel") },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed,
                            cursorColor = InacapRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_trabajo_actividad")
                    )

                    // Especialidad
                    OutlinedTextField(
                        value = formState.especialidad,
                        onValueChange = { viewModel.updateEspecialidad(it) },
                        enabled = !isCerrado,
                        label = { Text("Especialidad *") },
                        placeholder = { Text("Ej: Mecánica Automotriz y Autotrónica") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed,
                            cursorColor = InacapRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_especialidad")
                    )

                    // Fecha (Native DatePicker)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (!isCerrado) Modifier.clickable { datePickerDialog.show() }
                                else Modifier
                            )
                    ) {
                        OutlinedTextField(
                            value = formState.fecha,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Fecha") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Seleccionar fecha",
                                    tint = if (!isCerrado) InacapRed else TextSecondary
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = if (!isCerrado) InacapRed else TextSecondary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("picker_fecha_art")
                        )
                    }

                    // Docente
                    OutlinedTextField(
                        value = formState.docente,
                        onValueChange = { viewModel.updateDocente(it) },
                        enabled = !isCerrado,
                        label = { Text("Docente / Instructor *") },
                        placeholder = { Text("Ej: Rodrigo Morales Castillo") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed,
                            cursorColor = InacapRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_docente_art")
                    )

                    // Sala / Taller / Laboratorio / Terreno
                    OutlinedTextField(
                        value = formState.lugar,
                        onValueChange = { viewModel.updateLugar(it) },
                        enabled = !isCerrado,
                        label = { Text("Sala / Taller / Laboratorio / Terreno *") },
                        placeholder = { Text("Ej: Taller Mecánico N° 2") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed,
                            cursorColor = InacapRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_lugar_art")
                    )
                }
            }

            // Section 2: Tipo de Actividad & Nivel Técnico (Siempre Visibles)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_tipo_nivel_actividad"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Tipo de Actividad
                    Text(
                        text = "Tipo de Actividad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Teórica", "Práctica").forEach { tipo ->
                            val isSelected = formState.tipoActividad == tipo
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (!isCerrado) viewModel.updateTipoActividad(tipo)
                                },
                                label = {
                                    Text(
                                        text = tipo,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = InacapRed,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chip_tipo_actividad_${tipo.lowercase()}")
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    // 2. Nivel Técnico de la Actividad
                    Text(
                        text = "Nivel Técnico de la Actividad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Inicial", "Intermedio", "Avanzado").forEach { nivel ->
                            val isSelected = formState.nivelTecnico == nivel
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (!isCerrado) viewModel.updateNivelTecnico(nivel)
                                },
                                label = {
                                    Text(
                                        text = nivel,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = InacapRed,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chip_nivel_tecnico_${nivel.lowercase()}")
                            )
                        }
                    }
                }
            }

            // Section 3: Charla de Seguridad Inicial (Siempre Visible)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_charla_inicial_art"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Charla de Seguridad Inicial",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InacapRed
                            )
                            Text(
                                text = "¿Se realizó la charla previa al inicio de faena?",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = formState.charlaSeguridadInicial,
                                onClick = { if (!isCerrado) viewModel.updateCharlaSeguridadInicial(true) },
                                label = { Text("Sí") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = InacapRed,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("chip_charla_inicial_si")
                            )
                            FilterChip(
                                selected = !formState.charlaSeguridadInicial,
                                onClick = { if (!isCerrado) viewModel.updateCharlaSeguridadInicial(false) },
                                label = { Text("No") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = InacapRed,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("chip_charla_inicial_no")
                            )
                        }
                    }

                    // Vínculo opcional con charla del historial
                    if (formState.charlaSeguridadInicial) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (formState.charlaVinculadaTema != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "✓ Charla vinculada:",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = SuccessGreen
                                            )
                                            Text(
                                                text = "${formState.charlaVinculadaFecha} • ${formState.charlaVinculadaTema}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        if (!isCerrado) {
                                            IconButton(
                                                onClick = { viewModel.desvincularCharla() },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.LinkOff,
                                                    contentDescription = "Desvincular charla",
                                                    tint = InacapRed,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Puedes asociar el acta de la charla realizada hoy.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (!isCerrado) {
                                            Button(
                                                onClick = { showVincularCharlaDialog = true },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = InacapRed,
                                                    contentColor = Color.White
                                                ),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.testTag("btn_vincular_charla")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Link,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Vincular charla", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 4.1: Equipo de Apoyo (Plegable con contador)
            CollapsibleSelectorCard(
                title = "Equipo de Apoyo",
                count = formState.equipoApoyoSeleccionados.size,
                expanded = expandEquipoApoyo,
                onToggleExpand = { expandEquipoApoyo = !expandEquipoApoyo },
                testTag = "card_equipo_apoyo"
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OPCIONES_EQUIPO_APOYO.forEach { item ->
                        val isChecked = formState.equipoApoyoSeleccionados.contains(item)
                        FilterChip(
                            selected = isChecked,
                            onClick = {
                                if (!isCerrado) viewModel.toggleEquipoApoyo(item)
                            },
                            label = { Text(item, fontSize = 12.sp) },
                            leadingIcon = if (isChecked) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = InacapRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = InacapRed.copy(alpha = 0.12f),
                                selectedLabelColor = InacapRed
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isChecked,
                                selectedBorderColor = InacapRed
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = formState.equipoApoyoOtros,
                    onValueChange = { viewModel.updateEquipoApoyoOtros(it) },
                    enabled = !isCerrado,
                    label = { Text("Otros equipos de apoyo") },
                    placeholder = { Text("Especificar otros equipos...") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Section 4.2: Equipo de Protección Personal (EPP) (Plegable con contador)
            CollapsibleSelectorCard(
                title = "Equipo de Protección Personal (EPP)",
                count = formState.eppSeleccionados.size,
                expanded = expandEpp,
                onToggleExpand = { expandEpp = !expandEpp },
                testTag = "card_epp"
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OPCIONES_EPP.forEach { item ->
                        val isChecked = formState.eppSeleccionados.contains(item)
                        FilterChip(
                            selected = isChecked,
                            onClick = {
                                if (!isCerrado) viewModel.toggleEpp(item)
                            },
                            label = { Text(item, fontSize = 12.sp) },
                            leadingIcon = if (isChecked) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = InacapRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = InacapRed.copy(alpha = 0.12f),
                                selectedLabelColor = InacapRed
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isChecked,
                                selectedBorderColor = InacapRed
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = formState.eppOtros,
                    onValueChange = { viewModel.updateEppOtros(it) },
                    enabled = !isCerrado,
                    label = { Text("Otros EPP") },
                    placeholder = { Text("Especificar otros elementos de protección...") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Section 4.3: Comando de Voz (Plegable con contador)
            CollapsibleSelectorCard(
                title = "Comando de Voz",
                count = formState.comandoVozSeleccionados.size,
                expanded = expandComandoVoz,
                onToggleExpand = { expandComandoVoz = !expandComandoVoz },
                testTag = "card_comando_voz"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OPCIONES_COMANDO_VOZ.forEach { item ->
                        val isChecked = formState.comandoVozSeleccionados.contains(item)
                        FilterChip(
                            selected = isChecked,
                            onClick = {
                                if (!isCerrado) viewModel.toggleComandoVoz(item)
                            },
                            label = {
                                Text(
                                    text = item,
                                    fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = if (isChecked) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = InacapRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = InacapRed.copy(alpha = 0.12f),
                                selectedLabelColor = InacapRed
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isChecked,
                                selectedBorderColor = InacapRed
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Section 4.4: Condiciones Ambientales (Plegable con contador)
            CollapsibleSelectorCard(
                title = "Condiciones Ambientales",
                count = formState.condicionesAmbientalesSeleccionadas.size,
                expanded = expandCondicionesAmbientales,
                onToggleExpand = { expandCondicionesAmbientales = !expandCondicionesAmbientales },
                testTag = "card_condiciones_ambientales"
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OPCIONES_CONDICIONES_AMBIENTALES.forEach { item ->
                        val isChecked = formState.condicionesAmbientalesSeleccionadas.contains(item)
                        FilterChip(
                            selected = isChecked,
                            onClick = {
                                if (!isCerrado) viewModel.toggleCondicionAmbiental(item)
                            },
                            label = { Text(item, fontSize = 12.sp) },
                            leadingIcon = if (isChecked) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = InacapRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = InacapRed.copy(alpha = 0.12f),
                                selectedLabelColor = InacapRed
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isChecked,
                                selectedBorderColor = InacapRed
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = formState.condicionesAmbientalesOtros,
                    onValueChange = { viewModel.updateCondicionesAmbientalesOtros(it) },
                    enabled = !isCerrado,
                    label = { Text("Otras condiciones ambientales") },
                    placeholder = { Text("Especificar condiciones...") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Section 5: Etapas del Trabajo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_etapas_trabajo"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Etapas del Trabajo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InacapRed
                            )
                            Text(
                                text = "${formState.etapas.size} etapa(s) analizada(s)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = InacapRed.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "${formState.etapas.size} Etapas",
                                style = MaterialTheme.typography.labelMedium,
                                color = InacapRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    formState.etapas.forEachIndexed { index, etapa ->
                        EtapaTrabajoRowItem(
                            numero = index + 1,
                            etapa = etapa,
                            isReadOnly = isCerrado,
                            onUpdate = { updated ->
                                viewModel.updateEtapa(index, updated)
                            },
                            onDelete = if (!isCerrado && formState.etapas.size > 1) {
                                { viewModel.eliminarEtapa(index) }
                            } else null
                        )

                        if (index < formState.etapas.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    if (!isCerrado) {
                        OutlinedButton(
                            onClick = { viewModel.agregarEtapa() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_agregar_etapa"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = InacapRed
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar etapa",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Agregar etapa", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Section 6: Personal Ejecutante (hasta 13 personas)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_personal_ejecutante"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val firmadosEjecutantes = formState.personalEjecutante.count { it.firmado }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Personal Ejecutante",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InacapRed
                            )
                            Text(
                                text = "$firmadosEjecutantes de ${formState.personalEjecutante.size} ejecutantes firmados (máx 13)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (firmadosEjecutantes > 0 && firmadosEjecutantes == formState.personalEjecutante.size) SuccessGreenContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "$firmadosEjecutantes/${formState.personalEjecutante.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (firmadosEjecutantes > 0 && firmadosEjecutantes == formState.personalEjecutante.size) SuccessGreen else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    formState.personalEjecutante.forEachIndexed { index, ejecutante ->
                        PersonalEjecutanteRowItem(
                            numero = index + 1,
                            ejecutante = ejecutante,
                            isReadOnly = isCerrado,
                            onUpdate = { updated ->
                                viewModel.updatePersonalEjecutante(index, updated)
                            },
                            onToggleFirma = {
                                viewModel.toggleFirmaPersonalEjecutante(index)
                            },
                            onDelete = if (!isCerrado && formState.personalEjecutante.size > 1) {
                                { viewModel.eliminarPersonalEjecutante(index) }
                            } else null
                        )

                        if (index < formState.personalEjecutante.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    if (!isCerrado && formState.personalEjecutante.size < 13) {
                        OutlinedButton(
                            onClick = { viewModel.agregarPersonalEjecutante() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_agregar_ejecutante"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = InacapRed
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar ejecutante",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Agregar ejecutante (${formState.personalEjecutante.size}/13)", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Section 7: Firma del Responsable Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_firma_responsable"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
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
                                text = "Firma del Responsable",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InacapRed
                            )
                            Text(
                                text = "Docente o supervisor a cargo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (formState.responsableFirmado) SuccessGreenContainer else InacapRedContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = if (formState.responsableFirmado) "Firmado" else "Pendiente",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (formState.responsableFirmado) SuccessGreen else InacapRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (formState.responsableFirmado) {
                        Button(
                            onClick = {
                                if (!isCerrado) viewModel.toggleResponsableFirma()
                            },
                            enabled = !isCerrado,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SuccessGreen,
                                contentColor = Color.White,
                                disabledContainerColor = SuccessGreen.copy(alpha = 0.7f),
                                disabledContentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_firma_responsable")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Responsable Firmado",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Firmado",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                if (!isCerrado) viewModel.toggleResponsableFirma()
                            },
                            enabled = !isCerrado,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = InacapRed
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_firma_responsable")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Draw,
                                    contentDescription = "Firmar responsable",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Firmar",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            // Section 8: Action Buttons (Guardar ART o Nuevo ART)
            if (isCerrado) {
                Button(
                    onClick = { viewModel.nuevoArt() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_nuevo_art"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = InacapRed,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Nuevo ART",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Nuevo ART",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = {
                        viewModel.guardarArt()
                        showSummaryDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_guardar_art"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = InacapRed,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Guardar ART",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Guardar ART",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
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

    // Diálogo para Vincular Charla del Historial
    if (showVincularCharlaDialog) {
        AlertDialog(
            onDismissRequest = { showVincularCharlaDialog = false },
            title = {
                Text(
                    text = "Vincular Charla de Seguridad",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                if (charlasDisponibles.isEmpty()) {
                    Text(
                        text = "No hay charlas previas registradas en el historial para vincular.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        charlasDisponibles.forEach { charla ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.vincularCharla(
                                            id = charla.id,
                                            fecha = charla.fecha,
                                            tema = charla.tema
                                        )
                                        showVincularCharlaDialog = false
                                    },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = charla.fecha,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = InacapRed
                                        )
                                        Text(
                                            text = charla.tipo,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                    }
                                    Text(
                                        text = charla.tema,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Docente: ${charla.docente}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                OutlinedButton(onClick = { showVincularCharlaDialog = false }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Summary Dialog for ART
    if (showSummaryDialog) {
        val totalEtapas = formState.etapas.size
        val totalEjecutantes = formState.personalEjecutante.count { it.nombre.isNotBlank() || it.rut.isNotBlank() }

        AlertDialog(
            onDismissRequest = { showSummaryDialog = false },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = if (formState.responsableFirmado) SuccessGreenContainer else InacapRedContainer,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (formState.responsableFirmado) Icons.Default.CheckCircle else Icons.Outlined.Info,
                            contentDescription = "Resumen ART",
                            tint = if (formState.responsableFirmado) SuccessGreen else InacapRed,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = "ART Guardado y Cerrado",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (formState.responsableFirmado) SuccessGreenContainer.copy(alpha = 0.5f) else InacapRed.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (formState.responsableFirmado) "✓ Responsable: Firmado" else "⚠ Firma del Responsable: Pendiente",
                                fontWeight = FontWeight.Bold,
                                color = if (formState.responsableFirmado) SuccessGreen else InacapRed,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = InacapRedContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "✓ Guardado en Historial como ⏳ Pendiente de sincronizar.\n✓ El formulario ha quedado bloqueado.",
                            style = MaterialTheme.typography.bodySmall,
                            color = InacapRed,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "• Actividad: ${formState.trabajoActividad.ifBlank { "Sin especificar" }}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Modalidad: ${formState.tipoActividad} (${formState.nivelTecnico})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Fecha: ${formState.fecha}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Etapas analizadas: $totalEtapas",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Personal ejecutante: $totalEjecutantes",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSummaryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = InacapRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// -----------------------------------------------------------------------------
// Componente Plegable con Título y Contador
// -----------------------------------------------------------------------------
@Composable
fun CollapsibleSelectorCard(
    title: String,
    count: Int,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    testTag: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (count > 0) InacapRed.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "$count seleccionados",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (count > 0) InacapRed else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Contraer" else "Expandir",
                        tint = InacapRed
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    content()
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Fila de Personal Ejecutante
// -----------------------------------------------------------------------------
@Composable
fun PersonalEjecutanteRowItem(
    numero: Int,
    ejecutante: PersonalEjecutante,
    isReadOnly: Boolean = false,
    onUpdate: (PersonalEjecutante) -> Unit,
    onToggleFirma: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("row_ejecutante_$numero"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = InacapRed,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$numero",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ejecutante N° $numero",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (onDelete != null && !isReadOnly) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar ejecutante $numero",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Nombre
            OutlinedTextField(
                value = ejecutante.nombre,
                onValueChange = { onUpdate(ejecutante.copy(nombre = it)) },
                enabled = !isReadOnly,
                label = { Text("Nombre y Apellido") },
                placeholder = { Text("Ej: Felipe Carrasco Vidal") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InacapRed,
                    focusedLabelColor = InacapRed,
                    cursorColor = InacapRed
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // RUT y Botón Firma con Hora
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = ejecutante.rut,
                    onValueChange = { onUpdate(ejecutante.copy(rut = it)) },
                    enabled = !isReadOnly,
                    label = { Text("RUT") },
                    placeholder = { Text("19.845.210-4") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InacapRed,
                        focusedLabelColor = InacapRed,
                        cursorColor = InacapRed
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.2f)
                )

                if (ejecutante.firmado) {
                    Button(
                        onClick = { if (!isReadOnly) onToggleFirma() },
                        enabled = !isReadOnly,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen,
                            contentColor = Color.White,
                            disabledContainerColor = SuccessGreen.copy(alpha = 0.7f),
                            disabledContentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Firmado",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Firmado",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            if (ejecutante.horaFirma.isNotBlank()) {
                                Text(
                                    text = "${ejecutante.horaFirma} hrs",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { if (!isReadOnly) onToggleFirma() },
                        enabled = !isReadOnly,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = InacapRed
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Draw,
                                contentDescription = "Firmar",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Firmar",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Fila de Etapa de Trabajo
// -----------------------------------------------------------------------------
@Composable
fun EtapaTrabajoRowItem(
    numero: Int,
    etapa: EtapaTrabajo,
    isReadOnly: Boolean = false,
    onUpdate: (EtapaTrabajo) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("row_etapa_$numero"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = InacapRed,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$numero",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Etapa N° $numero",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (onDelete != null && !isReadOnly) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar etapa $numero",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = etapa.etapa,
                onValueChange = { onUpdate(etapa.copy(etapa = it)) },
                enabled = !isReadOnly,
                label = { Text("Etapa del trabajo") },
                placeholder = { Text("Ej: Inspección visual y desconexión de batería") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InacapRed,
                    focusedLabelColor = InacapRed,
                    cursorColor = InacapRed
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_etapa_$numero")
            )

            RiesgoDropdownField(
                selectedRiesgo = etapa.riesgoAsociado,
                isReadOnly = isReadOnly,
                onRiesgoSelected = { selected ->
                    onUpdate(etapa.copy(riesgoAsociado = selected))
                },
                numero = numero
            )

            OutlinedTextField(
                value = etapa.medidaControl,
                onValueChange = { onUpdate(etapa.copy(medidaControl = it)) },
                enabled = !isReadOnly,
                label = { Text("Medida de control") },
                placeholder = { Text("Ej: Uso de guantes dieléctricos y bloqueo físico") },
                minLines = 2,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InacapRed,
                    focusedLabelColor = InacapRed,
                    cursorColor = InacapRed
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_medida_control_$numero")
            )
        }
    }
}

@Composable
fun RiesgoDropdownField(
    selectedRiesgo: String,
    isReadOnly: Boolean = false,
    onRiesgoSelected: (String) -> Unit,
    numero: Int
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dropdown_riesgo_container_$numero")
    ) {
        OutlinedTextField(
            value = selectedRiesgo,
            onValueChange = {},
            readOnly = true,
            enabled = !isReadOnly,
            label = { Text("Riesgo asociado") },
            placeholder = { Text("Seleccionar riesgo...") },
            trailingIcon = {
                if (!isReadOnly) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = "Opciones de riesgo",
                            tint = InacapRed
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = InacapRed,
                focusedLabelColor = InacapRed,
                cursorColor = InacapRed
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (!isReadOnly) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                OPCIONES_RIESGOS.forEach { riesgo ->
                    val isSelected = riesgo == selectedRiesgo
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = riesgo,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) InacapRed else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onRiesgoSelected(riesgo)
                            expanded = false
                        },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = InacapRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}

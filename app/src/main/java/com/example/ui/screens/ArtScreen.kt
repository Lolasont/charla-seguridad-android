package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.model.EtapaTrabajo
import com.example.ui.theme.InacapRed
import com.example.ui.theme.InacapRedContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.ui.theme.TextSecondary
import java.util.Calendar
import java.util.Locale

val OPCIONES_RIESGOS = listOf(
    "Choque eléctrico",
    "Arco eléctrico",
    "Incendio",
    "Ruido",
    "Explosión",
    "Corte",
    "Tropiezos",
    "Contacto con partes activas",
    "Quemaduras",
    "Fallas en aislamiento",
    "Equipos defectuosos",
    "Sobrecargas",
    "Exposición al polvo",
    "Desniveles",
    "Caída de materiales",
    "Trabajos en altura",
    "Intoxicación",
    "Radiación electromagnética",
    "Caída igual o distinto nivel",
    "Proyección de partículas",
    "Espacio confinado",
    "Golpeado por/contra/con",
    "Sustancias tóxicas",
    "Ergonómico",
    "Radiación láser",
    "Otros"
)

@Composable
fun ArtScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Datos generales state
    var trabajoActividad by rememberSaveable { mutableStateOf("") }
    var especialidad by rememberSaveable { mutableStateOf("") }
    var docente by rememberSaveable { mutableStateOf("") }
    var lugar by rememberSaveable { mutableStateOf("") }

    // Fecha state with native DatePickerDialog
    val calendar = remember { Calendar.getInstance() }
    val initialYear = calendar.get(Calendar.YEAR)
    val initialMonth = calendar.get(Calendar.MONTH)
    val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

    var fecha by rememberSaveable {
        mutableStateOf(
            String.format(Locale.getDefault(), "%02d/%02d/%d", initialDay, initialMonth + 1, initialYear)
        )
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                fecha = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year)
            },
            initialYear,
            initialMonth,
            initialDay
        )
    }

    // Etapas del trabajo (Starts with 3 empty rows)
    val etapas = remember {
        mutableStateListOf(
            EtapaTrabajo(),
            EtapaTrabajo(),
            EtapaTrabajo()
        )
    }

    // Firma del responsable state
    var responsableFirmado by rememberSaveable { mutableStateOf(false) }

    // Dialog state
    var showSummaryDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Banner / Header Card
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
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
                Column {
                    Text(
                        text = "ART",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("art_title")
                    )
                    Text(
                        text = "Análisis de Riesgo de la Tarea",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                    value = trabajoActividad,
                    onValueChange = { trabajoActividad = it },
                    label = { Text("Trabajo o Actividad") },
                    placeholder = { Text("Ej: Desarme y montaje de motor térmico") },
                    singleLine = true,
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
                    value = especialidad,
                    onValueChange = { especialidad = it },
                    label = { Text("Especialidad") },
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
                        .clickable { datePickerDialog.show() }
                ) {
                    OutlinedTextField(
                        value = fecha,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Fecha") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Seleccionar fecha",
                                tint = InacapRed
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = InacapRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("picker_fecha_art")
                    )
                }

                // Docente
                OutlinedTextField(
                    value = docente,
                    onValueChange = { docente = it },
                    label = { Text("Docente / Instructor") },
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
                    value = lugar,
                    onValueChange = { lugar = it },
                    label = { Text("Sala / Taller / Laboratorio / Terreno") },
                    placeholder = { Text("Ej: Taller Mecánico N° 3") },
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

        // Section 2: Etapas del Trabajo
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
                            text = "${etapas.size} etapa(s) analizada(s)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = InacapRed.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "${etapas.size} Etapas",
                            style = MaterialTheme.typography.labelMedium,
                            color = InacapRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Editable Etapas
                etapas.forEachIndexed { index, etapa ->
                    EtapaTrabajoRowItem(
                        numero = index + 1,
                        etapa = etapa,
                        onUpdate = { updated ->
                            etapas[index] = updated
                        },
                        onDelete = if (etapas.size > 1) {
                            { etapas.removeAt(index) }
                        } else null
                    )

                    if (index < etapas.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                // Botón "+ Agregar etapa"
                OutlinedButton(
                    onClick = {
                        etapas.add(EtapaTrabajo())
                    },
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
                    Text(
                        text = "Agregar etapa",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Section 3: Firma del Responsable Card
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
                        color = if (responsableFirmado) SuccessGreenContainer else InacapRedContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = if (responsableFirmado) "Firmado" else "Pendiente",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (responsableFirmado) SuccessGreen else InacapRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Botón de firma del responsable (misma lógica interactiva: cambia a check verde con "Firmado")
                if (responsableFirmado) {
                    Button(
                        onClick = { responsableFirmado = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen,
                            contentColor = Color.White
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
                        onClick = { responsableFirmado = true },
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

        // Section 4: Botón "Guardar ART"
        Button(
            onClick = {
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

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Summary Dialog for ART
    if (showSummaryDialog) {
        val totalEtapas = etapas.size

        AlertDialog(
            onDismissRequest = { showSummaryDialog = false },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = if (responsableFirmado) SuccessGreenContainer else InacapRedContainer,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (responsableFirmado) Icons.Default.CheckCircle else Icons.Outlined.Info,
                            contentDescription = "Resumen ART",
                            tint = if (responsableFirmado) SuccessGreen else InacapRed,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = "Resumen de ART",
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
                        color = if (responsableFirmado) SuccessGreenContainer.copy(alpha = 0.5f) else InacapRed.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (responsableFirmado) "✓ Responsable: Firmado" else "⚠ Firma del Responsable: Pendiente",
                                fontWeight = FontWeight.Bold,
                                color = if (responsableFirmado) SuccessGreen else InacapRed,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Text(
                        text = "Datos Generales:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

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
                            text = "• Actividad: ${if (trabajoActividad.isNotBlank()) trabajoActividad else "Sin especificar"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Especialidad: ${if (especialidad.isNotBlank()) especialidad else "—"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Fecha: $fecha",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Docente: ${if (docente.isNotBlank()) docente else "—"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Lugar: ${if (lugar.isNotBlank()) lugar else "—"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Total etapas registradas: $totalEtapas",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (etapas.any { it.etapa.isNotBlank() || it.riesgoAsociado.isNotBlank() }) {
                        Text(
                            text = "Etapas destacadas:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            etapas.filter { it.etapa.isNotBlank() || it.riesgoAsociado.isNotBlank() }.take(3).forEachIndexed { i, e ->
                                val nombre = if (e.etapa.isNotBlank()) e.etapa else "Etapa ${i + 1}"
                                val riesgo = if (e.riesgoAsociado.isNotBlank()) " [${e.riesgoAsociado}]" else ""
                                Text(
                                    text = "${i + 1}. $nombre$riesgo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
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

@Composable
fun EtapaTrabajoRowItem(
    numero: Int,
    etapa: EtapaTrabajo,
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
            // Row Header: N° and Delete button
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

                if (onDelete != null) {
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

            // Etapa
            OutlinedTextField(
                value = etapa.etapa,
                onValueChange = { onUpdate(etapa.copy(etapa = it)) },
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

            // Riesgo asociado (Dropdown Menu)
            RiesgoDropdownField(
                selectedRiesgo = etapa.riesgoAsociado,
                onRiesgoSelected = { selected ->
                    onUpdate(etapa.copy(riesgoAsociado = selected))
                },
                numero = numero
            )

            // Medida de control
            OutlinedTextField(
                value = etapa.medidaControl,
                onValueChange = { onUpdate(etapa.copy(medidaControl = it)) },
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
            label = { Text("Riesgo asociado") },
            placeholder = { Text("Seleccionar riesgo...") },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Opciones de riesgo",
                        tint = InacapRed
                    )
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

        // Transparent overlay to trigger dropdown on entire field tap
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

package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.EtapaTrabajo
import com.example.ui.theme.InacapRed
import com.example.ui.theme.InacapRedContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ArtViewModel
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
    modifier: Modifier = Modifier,
    viewModel: ArtViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Observe ArtViewModel StateFlow
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val isCerrado = formState.isCerrado

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                    if (isCerrado) {
                        Text(
                            text = "Solo lectura",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                // Trabajo o Actividad
                OutlinedTextField(
                    value = formState.trabajoActividad,
                    onValueChange = { viewModel.updateTrabajoActividad(it) },
                    enabled = !isCerrado,
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
                    value = formState.especialidad,
                    onValueChange = { viewModel.updateEspecialidad(it) },
                    enabled = !isCerrado,
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
                    value = formState.lugar,
                    onValueChange = { viewModel.updateLugar(it) },
                    enabled = !isCerrado,
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

                // Editable Etapas
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

                // Botón "+ Agregar etapa" (solo si no está cerrado)
                if (!isCerrado) {
                    OutlinedButton(
                        onClick = {
                            viewModel.agregarEtapa()
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

                // Botón de firma del responsable
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

        // Section 4: Action Buttons (Guardar ART o Nuevo ART)
        if (isCerrado) {
            Button(
                onClick = {
                    viewModel.nuevoArt()
                },
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

    // Summary Dialog for ART
    if (showSummaryDialog) {
        val totalEtapas = formState.etapas.size

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
                            text = "• Actividad: ${if (formState.trabajoActividad.isNotBlank()) formState.trabajoActividad else "Sin especificar"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Especialidad: ${if (formState.especialidad.isNotBlank()) formState.especialidad else "—"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Fecha: ${formState.fecha}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Docente: ${if (formState.docente.isNotBlank()) formState.docente else "—"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Lugar: ${if (formState.lugar.isNotBlank()) formState.lugar else "—"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Total etapas: $totalEtapas",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
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

            // Etapa
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

            // Riesgo asociado (Dropdown Menu)
            RiesgoDropdownField(
                selectedRiesgo = etapa.riesgoAsociado,
                isReadOnly = isReadOnly,
                onRiesgoSelected = { selected ->
                    onUpdate(etapa.copy(riesgoAsociado = selected))
                },
                numero = numero
            )

            // Medida de control
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

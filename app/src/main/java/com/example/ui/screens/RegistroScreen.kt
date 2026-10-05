package com.example.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Asistente
import com.example.ui.theme.InacapRed
import com.example.ui.theme.InacapRedContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.ui.theme.TextSecondary
import java.util.Calendar
import java.util.Locale

@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form fields state
    var docente by rememberSaveable { mutableStateOf("") }
    var asignatura by rememberSaveable { mutableStateOf("") }
    var seccion by rememberSaveable { mutableStateOf("") }

    // Date & Time states with sensible defaults from calendar
    val calendar = remember { Calendar.getInstance() }
    val initialYear = calendar.get(Calendar.YEAR)
    val initialMonth = calendar.get(Calendar.MONTH)
    val initialDay = calendar.get(Calendar.DAY_OF_MONTH)
    val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
    val initialMinute = calendar.get(Calendar.MINUTE)

    var fecha by rememberSaveable {
        mutableStateOf(
            String.format(Locale.getDefault(), "%02d/%02d/%d", initialDay, initialMonth + 1, initialYear)
        )
    }
    var horaInicio by rememberSaveable {
        mutableStateOf(
            String.format(Locale.getDefault(), "%02d:%02d", initialHour, (initialMinute / 5) * 5)
        )
    }
    var horaTermino by rememberSaveable {
        mutableStateOf(
            String.format(Locale.getDefault(), "%02d:%02d", (initialHour + 1) % 24, (initialMinute / 5) * 5)
        )
    }

    var tema by rememberSaveable { mutableStateOf("") }

    // Tipo de charla radio buttons
    val tiposCharla = remember {
        listOf(
            "Charla 5 minutos",
            "Charla Integral",
            "Charla Externa",
            "Charla Interna",
            "Reinstrucción"
        )
    }
    var tipoCharlaSeleccionado by rememberSaveable { mutableStateOf(tiposCharla[0]) }

    // Attendance list starting with 3 empty rows
    val asistentes = remember {
        mutableStateListOf(
            Asistente(),
            Asistente(),
            Asistente()
        )
    }

    // Native DatePickerDialog
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

    // Native TimePickerDialog for Start Time
    val timePickerInicio = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                horaInicio = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            },
            initialHour,
            initialMinute,
            true
        )
    }

    // Native TimePickerDialog for End Time
    val timePickerTermino = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                horaTermino = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            },
            (initialHour + 1) % 24,
            initialMinute,
            true
        )
    }

    // Summary dialog state
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
                .testTag("registro_header_card"),
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
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "Registro de Charla",
                            tint = InacapRed,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Registro de Charla",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("registro_title")
                    )
                    Text(
                        text = "Prevención de Riesgos y Seguridad",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section 1: Datos de la Charla Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_datos_charla"),
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
                    text = "Datos de la Charla",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InacapRed
                )

                // Docente
                OutlinedTextField(
                    value = docente,
                    onValueChange = { docente = it },
                    label = { Text("Docente / Expositor") },
                    placeholder = { Text("Ej: Carlos Silva Rojas") },
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
                        .testTag("input_docente")
                )

                // Asignatura & Sección in row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = asignatura,
                        onValueChange = { asignatura = it },
                        label = { Text("Asignatura") },
                        placeholder = { Text("Ej: Taller Mecánico") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed,
                            cursorColor = InacapRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.8f)
                            .testTag("input_asignatura")
                    )

                    OutlinedTextField(
                        value = seccion,
                        onValueChange = { seccion = it },
                        label = { Text("Sección") },
                        placeholder = { Text("Ej: 002D") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed,
                            cursorColor = InacapRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("input_seccion")
                    )
                }

                // Native Date Picker Field
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
                            .testTag("picker_fecha")
                    )
                }

                // Native Time Pickers (Hora inicio & Hora término)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Hora inicio
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { timePickerInicio.show() }
                    ) {
                        OutlinedTextField(
                            value = horaInicio,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Hora Inicio") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Seleccionar hora inicio",
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
                                .testTag("picker_hora_inicio")
                        )
                    }

                    // Hora término
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { timePickerTermino.show() }
                    ) {
                        OutlinedTextField(
                            value = horaTermino,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Hora Término") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Seleccionar hora término",
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
                                .testTag("picker_hora_termino")
                        )
                    }
                }

                // Tema
                OutlinedTextField(
                    value = tema,
                    onValueChange = { tema = it },
                    label = { Text("Tema de la Charla") },
                    placeholder = { Text("Ej: Uso obligatorio de EPP y bloqueo de fuentes energéticas") },
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InacapRed,
                        focusedLabelColor = InacapRed,
                        cursorColor = InacapRed
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tema")
                )
            }
        }

        // Section 2: Tipo de Charla Card (Radio buttons group)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_tipo_charla"),
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Tipo de Charla",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InacapRed
                )
                Text(
                    text = "Seleccione la modalidad correspondiente:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                tiposCharla.forEach { tipo ->
                    val isSelected = tipoCharlaSeleccionado == tipo
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .selectable(
                                selected = isSelected,
                                onClick = { tipoCharlaSeleccionado = tipo },
                                role = Role.RadioButton
                            )
                            .background(
                                if (isSelected) InacapRed.copy(alpha = 0.08f) else Color.Transparent
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = InacapRed,
                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = tipo,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) InacapRed else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Section 3: Lista de Asistencia Editable Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_asistencia"),
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
                val firmadosCount = asistentes.count { it.firmado }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Lista de Asistencia",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = InacapRed
                        )
                        Text(
                            text = "$firmadosCount de ${asistentes.size} firmados",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (firmadosCount > 0) SuccessGreen else TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (firmadosCount > 0) SuccessGreenContainer else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (firmadosCount == asistentes.size && asistentes.isNotEmpty()) "Completa" else "$firmadosCount/${asistentes.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (firmadosCount > 0) SuccessGreen else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Editable rows
                asistentes.forEachIndexed { index, asistente ->
                    AsistenteRowItem(
                        numero = index + 1,
                        asistente = asistente,
                        onUpdate = { updated ->
                            asistentes[index] = updated
                        },
                        onDelete = if (asistentes.size > 1) {
                            { asistentes.removeAt(index) }
                        } else null
                    )

                    if (index < asistentes.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                // Botón "+ Agregar asistente"
                OutlinedButton(
                    onClick = {
                        asistentes.add(Asistente())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_agregar_asistente"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = InacapRed
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar asistente",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Agregar asistente",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Section 4: Botón "Guardar Registro"
        Button(
            onClick = {
                showSummaryDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_guardar_registro"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = InacapRed,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Guardar Registro",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Guardar Registro",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Resumen Dialog
    if (showSummaryDialog) {
        val totalAsistentes = asistentes.size
        val firmados = asistentes.filter { it.firmado }
        val firmadosCount = firmados.size

        AlertDialog(
            onDismissRequest = { showSummaryDialog = false },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = if (firmadosCount > 0) SuccessGreenContainer else InacapRedContainer,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (firmadosCount > 0) Icons.Default.CheckCircle else Icons.Outlined.Info,
                            contentDescription = "Resumen",
                            tint = if (firmadosCount > 0) SuccessGreen else InacapRed,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = "Resumen de Registro",
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
                        color = if (firmadosCount > 0) SuccessGreenContainer.copy(alpha = 0.5f) else InacapRed.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Asistentes firmados: $firmadosCount de $totalAsistentes",
                                fontWeight = FontWeight.Bold,
                                color = if (firmadosCount > 0) SuccessGreen else InacapRed,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Text(
                        text = "Detalles de la sesión:",
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
                            text = "• Docente: ${if (docente.isNotBlank()) docente else "Sin especificar"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Asignatura: ${if (asignatura.isNotBlank()) asignatura else "—"} (${if (seccion.isNotBlank()) seccion else "—"})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Fecha y Horario: $fecha ($horaInicio - $horaTermino)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Tipo: $tipoCharlaSeleccionado",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Tema: ${if (tema.isNotBlank()) tema else "Sin tema registrado"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (firmados.isNotEmpty()) {
                        Text(
                            text = "Firmantes confirmados:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            firmados.forEach { asis ->
                                val name = if (asis.nombre.isNotBlank()) asis.nombre else "Asistente sin nombre"
                                val rut = if (asis.rut.isNotBlank()) " (${asis.rut})" else ""
                                Text(
                                    text = "✓ $name$rut",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SuccessGreen,
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
fun AsistenteRowItem(
    numero: Int,
    asistente: Asistente,
    onUpdate: (Asistente) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("row_asistente_$numero"),
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
            // Row Header: N° and Status
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
                        text = "Asistente N° $numero",
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
                            contentDescription = "Eliminar fila $numero",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Nombre
            OutlinedTextField(
                value = asistente.nombre,
                onValueChange = { onUpdate(asistente.copy(nombre = it)) },
                label = { Text("Nombre") },
                placeholder = { Text("Nombre y Apellido") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InacapRed,
                    focusedLabelColor = InacapRed,
                    cursorColor = InacapRed
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_nombre_$numero")
            )

            // RUT and Firma Button in Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = asistente.rut,
                    onValueChange = { onUpdate(asistente.copy(rut = it)) },
                    label = { Text("RUT") },
                    placeholder = { Text("12.345.678-9") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InacapRed,
                        focusedLabelColor = InacapRed,
                        cursorColor = InacapRed
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("input_rut_$numero")
                )

                // Botón de firma (al tocarlo, simula una firma cambiando a un ícono de check verde con el texto "Firmado")
                if (asistente.firmado) {
                    Button(
                        onClick = { onUpdate(asistente.copy(firmado = false)) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("btn_firma_$numero")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Firmado",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Firmado",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { onUpdate(asistente.copy(firmado = true)) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = InacapRed
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("btn_firma_$numero")
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

            // Observación
            OutlinedTextField(
                value = asistente.observacion,
                onValueChange = { onUpdate(asistente.copy(observacion = it)) },
                label = { Text("Observación") },
                placeholder = { Text("Ej: Sin novedades / EPP completo") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InacapRed,
                    focusedLabelColor = InacapRed,
                    cursorColor = InacapRed
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_observacion_$numero")
            )
        }
    }
}

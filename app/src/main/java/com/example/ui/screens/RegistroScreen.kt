package com.example.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Asistente
import com.example.ui.theme.InacapRed
import com.example.ui.theme.InacapRedContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CharlasViewModel
import com.example.viewmodel.ValidacionCharlaResultado
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    viewModel: CharlasViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val isCerrado = formState.isCerrado

    val tiposCharla = remember {
        listOf(
            "Charla 5 minutos",
            "Charla Integral",
            "Charla Externa",
            "Charla Interna",
            "Reinstrucción"
        )
    }

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

    val timePickerInicio = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val nuevaHora = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                viewModel.updateHoraInicio(nuevaHora)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    val timePickerTermino = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val nuevaHora = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                viewModel.updateHoraTermino(nuevaHora)
            },
            (calendar.get(Calendar.HOUR_OF_DAY) + 1) % 24,
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    // Dialog state for pending signatures
    var pendingFirmasDialogData by remember { mutableStateOf<ValidacionCharlaResultado.FirmasPendientes?>(null) }
    var showSummaryDialog by rememberSaveable { mutableStateOf(false) }

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
            // Banner Registro Cerrado
            AnimatedVisibility(visible = isCerrado) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("banner_registro_cerrado"),
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
                                text = "Registro cerrado — ya no se puede modificar",
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Registro de Charla",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("registro_title")
                        )
                        Text(
                            text = if (isCerrado) "Estado: Cerrado y Guardado" else "Prevención de Riesgos y Seguridad",
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Datos de la Charla",
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

                    // Docente con Validación
                    OutlinedTextField(
                        value = formState.docente,
                        onValueChange = { viewModel.updateDocente(it) },
                        enabled = !isCerrado,
                        isError = formState.docenteError != null,
                        supportingText = {
                            if (formState.docenteError != null) {
                                Text(
                                    text = formState.docenteError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        label = { Text("Docente / Expositor *") },
                        placeholder = { Text("Ej: Carlos Silva Rojas") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed,
                            cursorColor = InacapRed,
                            errorBorderColor = MaterialTheme.colorScheme.error
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
                            value = formState.asignatura,
                            onValueChange = { viewModel.updateAsignatura(it) },
                            enabled = !isCerrado,
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
                            value = formState.seccion,
                            onValueChange = { viewModel.updateSeccion(it) },
                            enabled = !isCerrado,
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
                                .testTag("picker_fecha")
                        )
                    }

                    // Native Time Pickers (Hora inicio & Hora término)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .then(
                                    if (!isCerrado) Modifier.clickable { timePickerInicio.show() }
                                    else Modifier
                                )
                        ) {
                            OutlinedTextField(
                                value = formState.horaInicio,
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                label = { Text("Hora Inicio") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Seleccionar hora inicio",
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
                                    .testTag("picker_hora_inicio")
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .then(
                                    if (!isCerrado) Modifier.clickable { timePickerTermino.show() }
                                    else Modifier
                                )
                        ) {
                            OutlinedTextField(
                                value = formState.horaTermino,
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                label = { Text("Hora Término") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Seleccionar hora término",
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
                                    .testTag("picker_hora_termino")
                            )
                        }
                    }

                    // Tema con Validación
                    OutlinedTextField(
                        value = formState.tema,
                        onValueChange = { viewModel.updateTema(it) },
                        enabled = !isCerrado,
                        isError = formState.temaError != null,
                        supportingText = {
                            if (formState.temaError != null) {
                                Text(
                                    text = formState.temaError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        label = { Text("Tema de la Charla *") },
                        placeholder = { Text("Ej: Uso obligatorio de EPP y bloqueo de fuentes energéticas") },
                        minLines = 2,
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed,
                            cursorColor = InacapRed,
                            errorBorderColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_tema")
                    )
                }
            }

            // Section 2: Tipo de Charla Card
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
                        text = "Modalidad seleccionada:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    tiposCharla.forEach { tipo ->
                        val isSelected = formState.tipoCharla == tipo
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .then(
                                    if (!isCerrado) {
                                        Modifier.selectable(
                                            selected = isSelected,
                                            onClick = { viewModel.updateTipoCharla(tipo) },
                                            role = Role.RadioButton
                                        )
                                    } else Modifier
                                )
                                .background(
                                    if (isSelected) InacapRed.copy(alpha = 0.08f) else Color.Transparent
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = if (!isCerrado) { { viewModel.updateTipoCharla(tipo) } } else null,
                                enabled = !isCerrado,
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
                    val validos = formState.asistentes.filter { it.nombre.isNotBlank() || it.rut.isNotBlank() }
                    val firmadosCount = validos.count { it.firmado }

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
                                text = "$firmadosCount de ${validos.size} asistentes firmados",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (firmadosCount > 0 && firmadosCount == validos.size) SuccessGreen else TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (firmadosCount > 0 && firmadosCount == validos.size) SuccessGreenContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (validos.isNotEmpty() && firmadosCount == validos.size) "Completa" else "$firmadosCount/${validos.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (firmadosCount > 0 && firmadosCount == validos.size) SuccessGreen else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Rows
                    formState.asistentes.forEachIndexed { index, asistente ->
                        AsistenteRowItem(
                            numero = index + 1,
                            asistente = asistente,
                            isReadOnly = isCerrado,
                            onUpdate = { updated ->
                                viewModel.updateAsistente(index, updated)
                            },
                            onToggleFirma = {
                                viewModel.toggleFirmaAsistente(index)
                            },
                            onDelete = if (!isCerrado && formState.asistentes.size > 1) {
                                { viewModel.eliminarAsistente(index) }
                            } else null
                        )

                        if (index < formState.asistentes.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    if (!isCerrado) {
                        OutlinedButton(
                            onClick = {
                                viewModel.agregarAsistente()
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
            }

            // Section 4: Action Buttons
            if (isCerrado) {
                Button(
                    onClick = {
                        viewModel.nuevoRegistro()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_nuevo_registro"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = InacapRed,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Nuevo registro",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Nuevo registro",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = {
                        val resultado = viewModel.validarParaGuardar()
                        when (resultado) {
                            is ValidacionCharlaResultado.Error -> {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(resultado.mensaje)
                                }
                            }
                            is ValidacionCharlaResultado.FirmasPendientes -> {
                                pendingFirmasDialogData = resultado
                            }
                            is ValidacionCharlaResultado.ValidoParaGuardar -> {
                                viewModel.ejecutarGuardadoDirecto()
                                showSummaryDialog = true
                            }
                        }
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
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Snackbar Host for validation and notifications
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }

    // Dialog: Confirmación de cierre con firmas pendientes
    pendingFirmasDialogData?.let { data ->
        AlertDialog(
            onDismissRequest = { pendingFirmasDialogData = null },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = InacapRedContainer,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alerta firmas",
                            tint = InacapRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = "¿Cerrar acta con firmas pendientes?",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${data.sinFirma} de ${data.total} asistentes no han firmado.",
                        fontWeight = FontWeight.Bold,
                        color = InacapRed
                    )
                    Text(
                        text = "Si decides cerrar el acta ahora, los asistentes sin firma quedarán marcados automáticamente con la observación 'Sin firma'.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚠ Importante: Un acta cerrada no se puede editar después.",
                            style = MaterialTheme.typography.labelSmall,
                            color = InacapRed,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        pendingFirmasDialogData = null
                        viewModel.ejecutarCierreConFirmasPendientes()
                        showSummaryDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = InacapRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_confirmar_cerrar_igual")
                ) {
                    Text("Cerrar igual", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { pendingFirmasDialogData = null },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_volver_dialogo_firmas")
                ) {
                    Text("Volver", fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Resumen Dialog
    if (showSummaryDialog) {
        val validos = formState.asistentes.filter { it.nombre.isNotBlank() || it.rut.isNotBlank() }
        val firmados = validos.filter { it.firmado }
        val firmadosCount = firmados.size
        val totalAsistentes = validos.size

        AlertDialog(
            onDismissRequest = { showSummaryDialog = false },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = if (firmadosCount == totalAsistentes && totalAsistentes > 0) SuccessGreenContainer else InacapRedContainer,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (firmadosCount == totalAsistentes && totalAsistentes > 0) Icons.Default.CheckCircle else Icons.Outlined.Info,
                            contentDescription = "Resumen",
                            tint = if (firmadosCount == totalAsistentes && totalAsistentes > 0) SuccessGreen else InacapRed,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = "Registro Guardado y Cerrado",
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
                        color = if (firmadosCount == totalAsistentes) SuccessGreenContainer.copy(alpha = 0.5f) else InacapRed.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Asistentes firmados: $firmadosCount de $totalAsistentes",
                                fontWeight = FontWeight.Bold,
                                color = if (firmadosCount == totalAsistentes) SuccessGreen else InacapRed,
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
                            text = "✓ Guardado localmente como ⏳ Pendiente de sincronizar.\n✓ El formulario ha quedado bloqueado.",
                            style = MaterialTheme.typography.bodySmall,
                            color = InacapRed,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Text(
                        text = "Detalles de la sesión guardada:",
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
                            text = "• Docente: ${formState.docente}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "• Asignatura: ${if (formState.asignatura.isNotBlank()) formState.asignatura else "General"} (${if (formState.seccion.isNotBlank()) formState.seccion else "S/S"})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Fecha y Horario: ${formState.fecha} (${formState.horaInicio} - ${formState.horaTermino})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Tipo: ${formState.tipoCharla}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Tema: ${formState.tema}",
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

@Composable
fun AsistenteRowItem(
    numero: Int,
    asistente: Asistente,
    isReadOnly: Boolean = false,
    onUpdate: (Asistente) -> Unit,
    onToggleFirma: () -> Unit,
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

                if (onDelete != null && !isReadOnly) {
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
                enabled = !isReadOnly,
                label = { Text("Nombre y Apellido") },
                placeholder = { Text("Ej: Felipe Carrasco") },
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
                    enabled = !isReadOnly,
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

                // Botón de firma (con registro de hora exacta)
                if (asistente.firmado) {
                    Button(
                        onClick = {
                            if (!isReadOnly) onToggleFirma()
                        },
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
                            .testTag("btn_firma_$numero")
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
                            if (asistente.horaFirma.isNotBlank()) {
                                Text(
                                    text = "${asistente.horaFirma} hrs",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            if (!isReadOnly) onToggleFirma()
                        },
                        enabled = !isReadOnly,
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

            // Interruptor "Externo" (Personas externas, ej. estudiantes de liceo)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Participante Externo",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Ej: Estudiantes de liceo en pasantía",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }

                        Switch(
                            checked = asistente.esExterno,
                            onCheckedChange = { isChecked ->
                                if (!isReadOnly) {
                                    onUpdate(asistente.copy(esExterno = isChecked))
                                }
                            },
                            enabled = !isReadOnly,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = InacapRed
                            ),
                            modifier = Modifier.testTag("switch_externo_$numero")
                        )
                    }

                    // Campo obligatorio Procedencia cuando esExterno está activo
                    if (asistente.esExterno) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = asistente.procedencia,
                            onValueChange = { onUpdate(asistente.copy(procedencia = it)) },
                            enabled = !isReadOnly,
                            label = { Text("Procedencia *") },
                            placeholder = { Text("Ej: Liceo Industrial A-23") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = InacapRed,
                                focusedLabelColor = InacapRed,
                                cursorColor = InacapRed
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_procedencia_$numero")
                        )
                    }
                }
            }

            // Observación
            OutlinedTextField(
                value = asistente.observacion,
                onValueChange = { onUpdate(asistente.copy(observacion = it)) },
                enabled = !isReadOnly,
                label = { Text("Observación") },
                placeholder = { Text("Ej: EPP completo / Sin novedades") },
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

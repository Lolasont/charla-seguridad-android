package com.example.charlasinacap.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.charlasinacap.viewmodel.CharlaViewModel
import com.example.charlasinacap.viewmodel.CharlaTipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharlaScreenContent(viewModel: CharlaViewModel, onBack: () -> Unit) {
    val docente by viewModel.docente.collectAsState()
    val asignatura by viewModel.asignatura.collectAsState()
    val seccion by viewModel.seccion.collectAsState()
    val horaInicio by viewModel.horaInicio.collectAsState()
    val horaTermino by viewModel.horaTermino.collectAsState()
    val fecha by viewModel.fecha.collectAsState()
    val tema by viewModel.tema.collectAsState()
    val tipoCharla by viewModel.tipoCharla.collectAsState()
    val asistencias = viewModel.asistencias.collectAsState()
    val comentarios by viewModel.comentarios.collectAsState()
    val responsableNombre by viewModel.responsableNombre.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Charla de Seguridad Inicial") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { HeaderBoxCharla() }
            item {
                Card(
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(8.dp)) {
                        OutlinedTextField(
                            value = docente,
                            onValueChange = { viewModel.docente.value = it },
                            label = { Text("DOCENTE") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = asignatura,
                            onValueChange = { viewModel.asignatura.value = it },
                            label = { Text("ASIGNATURA") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = seccion,
                            onValueChange = { viewModel.seccion.value = it },
                            label = { Text("SECCIÓN") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = horaInicio,
                                onValueChange = { viewModel.horaInicio.value = it },
                                label = { Text("HORA INICIO") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = horaTermino,
                                onValueChange = { viewModel.horaTermino.value = it },
                                label = { Text("HORA TÉRMINO") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        OutlinedTextField(
                            value = fecha,
                            onValueChange = { viewModel.fecha.value = it },
                            label = { Text("FECHA") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = tema,
                            onValueChange = { viewModel.tema.value = it },
                            label = { Text("TEMA") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            item {
                SectionTitle("TIPO DE CHARLA")
                Column {
                    CharlaTipo.values().forEach { tipo ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .toggleable(
                                    value = tipoCharla == tipo,
                                    onValueChange = { if (it) viewModel.tipoCharla.value = tipo }
                                )
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = tipoCharla == tipo, onClick = { viewModel.tipoCharla.value = tipo })
                            Text(tipo.displayName, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item {
                SectionTitle("ASISTENCIA")
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF2F2F2))
                            .padding(4.dp)
                    ) {
                        Text("N°", Modifier.weight(0.1f), style = MaterialTheme.typography.bodySmall)
                        Text("NOMBRE", Modifier.weight(0.35f), style = MaterialTheme.typography.bodySmall)
                        Text("RUT", Modifier.weight(0.2f), style = MaterialTheme.typography.bodySmall)
                        Text("FIRMA", Modifier.weight(0.2f), style = MaterialTheme.typography.bodySmall)
                        Text("OBSERVACIÓN", Modifier.weight(0.2f), style = MaterialTheme.typography.bodySmall)
                    }
                    asistencias.value.forEachIndexed { idx, row ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${idx + 1}.", Modifier.weight(0.1f))
                            OutlinedTextField(
                                value = row.nombre,
                                onValueChange = { viewModel.asistencias.value[idx].nombre = it },
                                placeholder = { Text("Nombre") },
                                modifier = Modifier.weight(0.35f)
                            )
                            OutlinedTextField(
                                value = row.rut,
                                onValueChange = { viewModel.asistencias.value[idx].rut = it },
                                placeholder = { Text("RUT") },
                                modifier = Modifier.weight(0.2f)
                            )
                            OutlinedTextField(
                                value = row.firma,
                                onValueChange = { viewModel.asistencias.value[idx].firma = it },
                                placeholder = { Text("Firma") },
                                modifier = Modifier.weight(0.2f)
                            )
                            OutlinedTextField(
                                value = row.observacion,
                                onValueChange = { viewModel.asistencias.value[idx].observacion = it },
                                placeholder = { Text("Obs.") },
                                modifier = Modifier.weight(0.2f)
                            )
                        }
                    }
                    TextButton(onClick = { viewModel.asistencias.value = viewModel.asistencias.value + CharlaViewModel.AsistenciaRow() }) {
                        Text("Agregar fila")
                    }
                }
            }
            item {
                SectionTitle("COMENTARIOS / OBSERVACIONES / COMPROMISOS")
                OutlinedTextField(
                    value = comentarios,
                    onValueChange = { viewModel.comentarios.value = it },
                    placeholder = { Text("Escriba aquí...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            }
            item {
                SectionTitle("DOCENTE / RELATOR / RESPONSABLE")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = responsableNombre,
                        onValueChange = { viewModel.responsableNombre.value = it },
                        label = { Text("NOMBRE") },
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        Modifier
                            .weight(1f)
                            .height(30.dp)
                            .border(BorderStroke(1.dp, Color.Black))
                    )
                }
            }
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { viewModel.saveCharla() },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text("Guardar Charla")
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderBoxCharla() {
    Card(
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(8.dp)) {
            Box(Modifier.size(40.dp).background(Color(0xFFD32F2F)))
            Text("nacap", style = MaterialTheme.typography.titleLarge, color = Color(0xFFD32F2F))
            Text("SAN PEDRO DE LA PAZ", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text(
                "REGISTRO DE CHARLA DE SEGURIDAD INICIAL",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF2F2F2))
            .padding(vertical = 4.dp, horizontal = 8.dp),
        color = Color.Black
    )
}

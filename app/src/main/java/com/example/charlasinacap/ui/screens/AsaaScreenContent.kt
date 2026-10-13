package com.example.charlasinacap.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.charlasinacap.viewmodel.AsaaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsaaScreenContent(viewModel: AsaaViewModel, onBack: () -> Unit) {
    // Collect states
    val trabajoActividad by viewModel.trabajoActividad.collectAsState()
    val especialidad by viewModel.especialidad.collectAsState()
    val fecha by viewModel.fecha.collectAsState()
    val tipoActividad by viewModel.tipoActividad.collectAsState()
    val docente by viewModel.docente.collectAsState()
    val ubicacion by viewModel.ubicacion.collectAsState()
    val nivelInicial by viewModel.nivelInicial.collectAsState()
    val nivelIntermedio by viewModel.nivelIntermedio.collectAsState()
    val nivelAvanzado by viewModel.nivelAvanzado.collectAsState()
    val charlaInicial by viewModel.charlaInicial.collectAsState()
    val asaaComprendido by viewModel.asaaComprendido.collectAsState()
    val loto by viewModel.loto.collectAsState()
    val personal = viewModel.personal.collectAsState()
    val etapas = viewModel.etapas.collectAsState()
    val equipamientoChecked = viewModel.equipamientoChecked.collectAsState()
    val eppChecked = viewModel.eppChecked.collectAsState()
    val condicionesChecked = viewModel.condicionesChecked.collectAsState()
    val medidasChecked = viewModel.medidasChecked.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ASAA – Análisis Seguro de la Actividad Académica") },
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
            item { HeaderBox() }
            item {
                Card(
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = trabajoActividad,
                                onValueChange = { viewModel.trabajoActividad.value = it },
                                label = { Text("TRABAJO O ACTIVIDAD") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = especialidad,
                                onValueChange = { viewModel.especialidad.value = it },
                                label = { Text("ESPECIALIDAD") },
                                modifier = Modifier.weight(0.5f)
                            )
                            OutlinedTextField(
                                value = fecha,
                                onValueChange = { viewModel.fecha.value = it },
                                label = { Text("FECHA") },
                                modifier = Modifier.weight(0.5f)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) {
                            OutlinedTextField(
                                value = docente,
                                onValueChange = { viewModel.docente.value = it },
                                label = { Text("DOCENTE") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = ubicacion,
                                onValueChange = { viewModel.ubicacion.value = it },
                                label = { Text("SALA / TALLER / LABORATORIO / TERRENO") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Tipo de actividad radio buttons
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("TIPO DE ACTIVIDAD:")
                            Spacer(Modifier.width(8.dp))
                            Row(
                                Modifier.toggleable(
                                    value = tipoActividad == "Teórica",
                                    onValueChange = { if (it) viewModel.tipoActividad.value = "Teórica" }
                                )
                            ) {
                                RadioButton(selected = tipoActividad == "Teórica", onClick = { viewModel.tipoActividad.value = "Teórica" })
                                Text("Teórica")
                            }
                            Spacer(Modifier.width(16.dp))
                            Row(
                                Modifier.toggleable(
                                    value = tipoActividad == "Práctica",
                                    onValueChange = { if (it) viewModel.tipoActividad.value = "Práctica" }
                                )
                            ) {
                                RadioButton(selected = tipoActividad == "Práctica", onClick = { viewModel.tipoActividad.value = "Práctica" })
                                Text("Práctica")
                            }
                        }
                    }
                }
            }
            if (tipoActividad == "Práctica") {
            item {
                SectionTitle("Equipo de Apoyo (marque con una \"x\")")
                CheckboxGrid(items = com.example.charlasinacap.model.AsaaCatalogos.equiposApoyo, checked = equipamientoChecked.value)
            }
            // Nivel técnico y EPP
            item {
                SectionTitle("Nivel técnico de la Actividad")
                Row {
                    CheckboxItem("Inicial", nivelInicial) { viewModel.nivelInicial.value = it }
                    CheckboxItem("Intermedio", nivelIntermedio) { viewModel.nivelIntermedio.value = it }
                    CheckboxItem("Avanzado", nivelAvanzado) { viewModel.nivelAvanzado.value = it }
                }
                Spacer(Modifier.height(8.dp))
                SectionTitle("Equipo de Protección Personal (marque con una \"x\")")
                CheckboxGrid(items = com.example.charlasinacap.model.AsaaCatalogos.epp, checked = eppChecked.value)
            }
            // Personal ejecutante
            item {
                SectionTitle("Personal Ejecutante")
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    personal.value.forEachIndexed { idx, name ->
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                val mutable = personal.value.toMutableList()
                                mutable[idx] = it
                                viewModel.personal.value = mutable
                            },
                            label = { Text("${idx + 1}.") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                TextButton(onClick = { viewModel.personal.value = viewModel.personal.value + "" }) { Text("Agregar persona") }
            }
            // Condiciones ambientales
            item {
                SectionTitle("Condiciones Ambientales")
                CheckboxGrid(items = com.example.charlasinacap.model.AsaaCatalogos.condicionesAmbientales, checked = condicionesChecked.value)
            }
            // Medidas de control
            item {
                SectionTitle("MEDIDAS DE CONTROL PROPUESTAS")
                CheckboxGrid(items = com.example.charlasinacap.model.AsaaCatalogos.medidasControl, checked = medidasChecked.value, columns = 2)
            }
            // Preguntas Sí/No
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SimpleYesNo("Charla de seguridad Inicial", charlaInicial) { viewModel.charlaInicial.value = it }
                    SimpleYesNo("Este ASAA fue realizado y comprendido", asaaComprendido) { viewModel.asaaComprendido.value = it }
                    SimpleYesNo("Bloqueo/etiquetado (LOTO) si corresponde", loto) { viewModel.loto.value = it }
                }
            }
            // Etapas del trabajo
            item {
                SectionTitle("Etapas del Trabajo")
                Column {
                    EtapaHeader()
                    etapas.value.forEachIndexed { idx, etapa ->
                        EtapaRow(idx + 1, etapa) { updated ->
                            val mutable = etapas.value.toMutableList()
                            mutable[idx] = updated
                            viewModel.etapas.value = mutable
                        }
                    }
                    TextButton(onClick = { viewModel.etapas.value = viewModel.etapas.value + AsaaViewModel.Etapa() }) { Text("Agregar etapa") }
                }
            }
            }
            // Observaciones y firmas
            item {
                SectionTitle("Observaciones y Firmas")
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("DOCENTE RESPONSABLE DE TRABAJOS") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("OBSERVACIONES") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            }
            // Botones de acción
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = { viewModel.saveAsaa() }) {
                        Text("Guardar ASAA")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { viewModel.duplicateLastAsaa() }) {
                        Text("Duplicar último")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { viewModel.exportAsaa() }) {
                        Text("Exportar PDF/Excel")
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderBox() {
    Card(
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(40.dp).background(Color(0xFFD32F2F)))
            Text("nacap", style = MaterialTheme.typography.titleLarge, color = Color(0xFFD32F2F))
            Text("SAN PEDRO DE LA PAZ", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text(
                "Análisis Seguro de la Actividad Académica (ASAA)",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 14.sp,
                color = Color.Black
            )
            Text(
                "Elaborado en base a Ley N° 16.744, D.S. N° 44/2024 y D.S. N° 594/1999 (MINSAL)",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 9.sp,
                color = Color.DarkGray
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

@Composable
private fun CheckboxGrid(items: List<String>, checked: List<Boolean>, columns: Int = 2) {
    Column {
        items.chunked(columns).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { label ->
                    val idx = items.indexOf(label)
                    Row(
                        Modifier
                            .weight(1f)
                            .toggleable(
                                value = checked[idx],
                                onValueChange = { /* update via viewModel not shown */ }
                            )
                    ) {
                        Checkbox(checked = checked[idx], onCheckedChange = null)
                        Text(label, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckboxItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.toggleable(value = checked, onValueChange = onCheckedChange)) {
        Checkbox(checked = checked, onCheckedChange = null)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SimpleYesNo(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.toggleable(value = checked, onValueChange = onCheckedChange)
    ) {
        RadioButton(selected = checked, onClick = { onCheckedChange(true) })
        RadioButton(selected = !checked, onClick = { onCheckedChange(false) })
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun EtapaHeader() {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color(0xFFF2F2F2))
            .padding(4.dp)
    ) {
        Text("ÍTEM", Modifier.weight(0.1f), style = MaterialTheme.typography.bodySmall)
        Text("ETAPAS DEL TRABAJO", Modifier.weight(0.35f), style = MaterialTheme.typography.bodySmall)
        Text("RIESGOS ASOCIADOS", Modifier.weight(0.3f), style = MaterialTheme.typography.bodySmall)
        Text("MEDIDAS DE CONTROL DE RIESGOS", Modifier.weight(0.3f), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun EtapaRow(index: Int, etapa: AsaaViewModel.Etapa, onUpdate: (AsaaViewModel.Etapa) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text("$index.", Modifier.weight(0.1f), style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(
            value = etapa.descripcion,
            onValueChange = { onUpdate(etapa.copy(descripcion = it)) },
            placeholder = { Text("Descripción") },
            modifier = Modifier.weight(0.35f)
        )
        OutlinedTextField(
            value = etapa.riesgos,
            onValueChange = { onUpdate(etapa.copy(riesgos = it)) },
            placeholder = { Text("Riesgo(s)") },
            modifier = Modifier.weight(0.3f)
        )
        OutlinedTextField(
            value = etapa.medidas,
            onValueChange = { onUpdate(etapa.copy(medidas = it)) },
            placeholder = { Text("Medida(s)") },
            modifier = Modifier.weight(0.3f)
        )
    }
}

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HistorialItem
import com.example.ui.theme.InacapRed
import com.example.ui.theme.InacapRedContainer
import com.example.ui.theme.SecondaryCharcoal
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

enum class TipoFiltroHistorial(val label: String) {
    PERSONA("Por Persona"),
    FECHA("Por Fecha"),
    DOCENTE("Por Docente")
}

@Composable
fun HistorialScreen(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Search and filter state
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(TipoFiltroHistorial.DOCENTE) }

    // 4-5 Realistic historical sample data items (Sept - Oct 2026)
    val historialItems = remember {
        listOf(
            HistorialItem(
                id = "1",
                fecha = "02/10/2026",
                docente = "Carlos Silva Rojas",
                asignatura = "Taller de Mantenimiento Electromecánico",
                seccion = "002D",
                tipo = "Charla 5 minutos",
                numeroAsistentes = 16,
                asistentesFirmados = 16,
                tema = "Inspección previa de herramientas eléctricas y bloqueo LOTO",
                personas = listOf("Juan Morales", "Felipe Carrasco", "Daniela Vega", "Carlos Silva", "Ignacio Reyes")
            ),
            HistorialItem(
                id = "2",
                fecha = "29/09/2026",
                docente = "Marcela Pardo Fuentes",
                asignatura = "Automatización y Robótica Industrial",
                seccion = "001D",
                tipo = "Charla Integral",
                numeroAsistentes = 22,
                asistentesFirmados = 22,
                tema = "Paradas de emergencia y protocolos en celdas robotizadas",
                personas = listOf("Marcela Pardo", "Ignacio Soto", "Valentina Rivas", "Matías Alarcón", "Camila Torres")
            ),
            HistorialItem(
                id = "3",
                fecha = "25/09/2026",
                docente = "Héctor Garrido Vera",
                asignatura = "Sistemas de Frenos y Dirección",
                seccion = "003V",
                tipo = "Reinstrucción",
                numeroAsistentes = 14,
                asistentesFirmados = 14,
                tema = "Riesgos en el uso de elevadores hidráulicos y fosas de inspección",
                personas = listOf("Héctor Garrido", "Sebastián Muñoz", "Lucas Henríquez", "Javier Castro", "Nicolás Peña")
            ),
            HistorialItem(
                id = "4",
                fecha = "18/09/2026",
                docente = "Andrea Navarrete Soto",
                asignatura = "Prevención de Riesgos en Minería y Construcción",
                seccion = "001V",
                tipo = "Charla Externa",
                numeroAsistentes = 28,
                asistentesFirmados = 28,
                tema = "Procedimientos de trabajo seguro en excavaciones y taludes",
                personas = listOf("Andrea Navarrete", "Camilo Ortiz", "Sofía Valenzuela", "Pablo Pinto", "Constanza Morales")
            ),
            HistorialItem(
                id = "5",
                fecha = "11/09/2026",
                docente = "Rodrigo Morales Castillo",
                asignatura = "Laboratorio de Ensayos No Destructivos",
                seccion = "002D",
                tipo = "Charla Interna",
                numeroAsistentes = 12,
                asistentesFirmados = 12,
                tema = "Protección radiológica y manejo seguro de líquidos penetrantes",
                personas = listOf("Rodrigo Morales", "Constanza Bravo", "Diego Salgado", "Esteban Reyes", "Francisca Lara")
            )
        )
    }

    // Filtered list based on selected filter and query
    val filteredList by remember {
        derivedStateOf {
            val query = searchQuery.trim()
            if (query.isEmpty()) {
                historialItems
            } else {
                historialItems.filter { item ->
                    when (selectedFilter) {
                        TipoFiltroHistorial.DOCENTE ->
                            item.docente.contains(query, ignoreCase = true)
                        TipoFiltroHistorial.FECHA ->
                            item.fecha.contains(query, ignoreCase = true)
                        TipoFiltroHistorial.PERSONA ->
                            item.personas.any { it.contains(query, ignoreCase = true) } ||
                                item.docente.contains(query, ignoreCase = true)
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header / Search & Filter Controls (Top Section)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = when (selectedFilter) {
                                    TipoFiltroHistorial.DOCENTE -> "Buscar por nombre del docente..."
                                    TipoFiltroHistorial.FECHA -> "Buscar por fecha (ej: 29/09, oct)..."
                                    TipoFiltroHistorial.PERSONA -> "Buscar por nombre de participante..."
                                },
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = InacapRed
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Limpiar búsqueda",
                                        tint = TextSecondary
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InacapRed,
                            focusedLabelColor = InacapRed,
                            cursorColor = InacapRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_buscar_historial")
                    )

                    // Filter chips row: "Por Persona", "Por Fecha", "Por Docente"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TipoFiltroHistorial.values().forEach { filter ->
                            val isSelected = selectedFilter == filter
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilter = filter },
                                label = {
                                    Text(
                                        text = filter.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = InacapRed.copy(alpha = 0.12f),
                                    selectedLabelColor = InacapRed,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    selectedBorderColor = InacapRed,
                                    borderColor = Color.Transparent
                                ),
                                modifier = Modifier.testTag("chip_filtro_${filter.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            // Results count banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredList.size} registro(s) encontrado(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Sede San Pedro de la Paz",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            // LazyColumn for Historical Items
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(54.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.SearchOff,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Text(
                            text = "No se encontraron registros",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Prueba modificando el criterio de búsqueda o seleccionando otro filtro.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("lista_historial"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredList,
                        key = { it.id }
                    ) { item ->
                        HistorialCardItem(item = item)
                    }
                }
            }
        }

        // Bottom Export Actions (Exportar a PDF y Exportar a Excel)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .testTag("barra_exportar"),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón "Exportar a PDF"
                Button(
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Exportando historial a PDF... Documento generado con éxito.")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_exportar_pdf"),
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
                        text = "Exportar a PDF",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Botón "Exportar a Excel"
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Exportando historial a Excel (.xlsx)... Planilla descargada con éxito.")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_exportar_excel"),
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
                        text = "Exportar a Excel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Snackbar Host for export notifications
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 76.dp)
        )
    }
}

@Composable
fun HistorialCardItem(
    item: HistorialItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_historial_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Fecha and Tipo Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Fecha",
                        tint = InacapRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.fecha,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = InacapRed.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = item.tipo,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = InacapRed,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Docente
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Docente",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = item.docente,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Asignatura / Sección
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "Asignatura",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${item.asignatura} — Sec. ${item.seccion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Tema tratado (si existe)
            if (item.tema.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tema: ${item.tema}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                modifier = Modifier.padding(vertical = 2.dp)
            )

            // Footer Row: N° Asistentes and Verified Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = "Asistentes",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${item.numeroAsistentes} Asistentes (${item.asistentesFirmados} firmados)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SuccessGreenContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Completado",
                            tint = SuccessGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "100% Firmas",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

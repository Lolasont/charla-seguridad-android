package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entities.ArtConEtapas
import com.example.model.HistorialItem
import com.example.ui.theme.InacapRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.ui.theme.SyncPendingBorder
import com.example.ui.theme.SyncPendingContainer
import com.example.ui.theme.SyncPendingText
import com.example.ui.theme.SyncSuccessBorder
import com.example.ui.theme.SyncSuccessContainer
import com.example.ui.theme.SyncSuccessText
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ArtViewModel
import com.example.viewmodel.CharlasViewModel
import kotlinx.coroutines.launch

enum class TipoFiltroHistorial(val label: String) {
    PERSONA("Por Persona"),
    FECHA("Por Fecha"),
    DOCENTE("Por Docente")
}

@Composable
fun HistorialScreen(
    modifier: Modifier = Modifier,
    charlasViewModel: CharlasViewModel = viewModel(),
    artViewModel: ArtViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dynamic connection to shared Room ViewModels via Flow
    val charlasItems by charlasViewModel.historial.collectAsStateWithLifecycle()
    val artItems by artViewModel.historialArt.collectAsStateWithLifecycle()

    val isOnline by charlasViewModel.isOnline.collectAsStateWithLifecycle()
    val isSyncing by charlasViewModel.isSyncing.collectAsStateWithLifecycle()

    // Selector superior: 0 = Charlas, 1 = ART
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // Navigation state to Detalle Screens
    var selectedCharlaId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedCharla = remember(selectedCharlaId, charlasItems) {
        charlasItems.find { it.id == selectedCharlaId }
    }

    var selectedArtId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedArt = remember(selectedArtId, artItems) {
        artItems.find { it.art.id == selectedArtId }
    }

    if (selectedCharla != null) {
        DetalleCharlaScreen(
            item = selectedCharla,
            onVolver = { selectedCharlaId = null }
        )
    } else if (selectedArt != null) {
        DetalleArtScreen(
            artConEtapas = selectedArt,
            onVolver = { selectedArtId = null }
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Selector Superior ("Charlas" | "ART")
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = InacapRed,
                        modifier = Modifier.testTag("tab_selector_documentos")
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Assignment,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Charlas (${charlasItems.size})",
                                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            },
                            selectedContentColor = InacapRed,
                            unselectedContentColor = TextSecondary,
                            modifier = Modifier.testTag("tab_selector_charlas")
                        )

                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ART (${artItems.size})",
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            },
                            selectedContentColor = InacapRed,
                            unselectedContentColor = TextSecondary,
                            modifier = Modifier.testTag("tab_selector_art")
                        )
                    }
                }

                // Banner de Sincronización Simulada y Estado de Conexión
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("banner_conexion_sincronizacion"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isOnline) SuccessGreenContainer.copy(alpha = 0.4f) else InacapRed.copy(alpha = 0.06f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isOnline) SuccessGreen else TextSecondary,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isOnline) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isOnline) "Conectado" else "Sin conexión",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOnline) SuccessGreen else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isOnline) "Servidor INACAP en línea" else "Operando en modo local",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isOnline) "En línea" else "Offline",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Switch(
                                    checked = isOnline,
                                    onCheckedChange = { checked ->
                                        charlasViewModel.toggleConnection(checked) { count ->
                                            coroutineScope.launch {
                                                if (count > 0) {
                                                    snackbarHostState.showSnackbar("$count registros sincronizados")
                                                } else {
                                                    snackbarHostState.showSnackbar("Todos los registros ya estaban sincronizados")
                                                }
                                            }
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = SuccessGreen
                                    ),
                                    modifier = Modifier.testTag("switch_conexion")
                                )
                            }
                        }

                        // Botón de reconexión y sincronización
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isSyncing) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        color = InacapRed,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Sincronizando de a uno con el servidor...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = InacapRed,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            } else {
                                val totalPendientes = charlasItems.count { !it.sincronizado } + artItems.count { !it.art.sincronizado }
                                Text(
                                    text = if (totalPendientes > 0) "$totalPendientes documento(s) pendientes de subir" else "Todo sincronizado",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (totalPendientes > 0) SyncPendingText else SuccessGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Button(
                                onClick = {
                                    charlasViewModel.toggleConnection(true) { count ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("$count registros sincronizados")
                                        }
                                    }
                                },
                                enabled = !isSyncing,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = InacapRed,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("btn_simular_reconexion")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Simular reconexión",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Crossfade between independent lists
                Crossfade(
                    targetState = selectedTab,
                    label = "historial_tab_transition"
                ) { tabIndex ->
                    when (tabIndex) {
                        0 -> {
                            HistorialCharlasView(
                                charlasItems = charlasItems,
                                onItemClick = { item -> selectedCharlaId = item.id }
                            )
                        }
                        1 -> {
                            HistorialArtView(
                                artItems = artItems,
                                onItemClick = { artConEtapas -> selectedArtId = artConEtapas.art.id }
                            )
                        }
                    }
                }
            }

            // Snackbar Host for sync notifications
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

// -----------------------------------------------------------------------------
// LIST VIEW FOR CHARLAS
// -----------------------------------------------------------------------------
@Composable
private fun HistorialCharlasView(
    charlasItems: List<HistorialItem>,
    onItemClick: (HistorialItem) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(TipoFiltroHistorial.DOCENTE) }

    val filteredList by remember(charlasItems, searchQuery, selectedFilter) {
        derivedStateOf {
            val query = searchQuery.trim()
            if (query.isEmpty()) {
                charlasItems
            } else {
                charlasItems.filter { item ->
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

    Column(modifier = Modifier.fillMaxSize()) {
        // Search & Filter Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = when (selectedFilter) {
                                TipoFiltroHistorial.DOCENTE -> "Buscar por nombre del docente..."
                                TipoFiltroHistorial.FECHA -> "Buscar por fecha (ej: 02/10, oct)..."
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

        // Counter info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredList.size} charla(s) registrada(s)",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Toca para ver detalle",
                style = MaterialTheme.typography.labelSmall,
                color = InacapRed,
                fontWeight = FontWeight.SemiBold
            )
        }

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
                        text = "No se encontraron charlas",
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
                    .testTag("lista_historial_charlas"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = filteredList,
                    key = { it.id }
                ) { item ->
                    HistorialCardItem(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// LIST VIEW FOR ART
// -----------------------------------------------------------------------------
@Composable
private fun HistorialArtView(
    artItems: List<ArtConEtapas>,
    onItemClick: (ArtConEtapas) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredList by remember(artItems, searchQuery) {
        derivedStateOf {
            val query = searchQuery.trim()
            if (query.isEmpty()) {
                artItems
            } else {
                artItems.filter { item ->
                    item.art.trabajoActividad.contains(query, ignoreCase = true) ||
                        item.art.docente.contains(query, ignoreCase = true) ||
                        item.art.especialidad.contains(query, ignoreCase = true) ||
                        item.art.fecha.contains(query, ignoreCase = true) ||
                        item.art.lugar.contains(query, ignoreCase = true)
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Buscar por actividad, docente o especialidad...",
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar ART",
                            tint = InacapRed
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpiar",
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
                        .testTag("input_buscar_historial_art")
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredList.size} ART registrado(s)",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Toca para ver etapas y medidas",
                style = MaterialTheme.typography.labelSmall,
                color = InacapRed,
                fontWeight = FontWeight.SemiBold
            )
        }

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
                        text = "No se encontraron registros de ART",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Completa el formulario en la pestaña ART para registrar nuevas evaluaciones.",
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
                    .testTag("lista_historial_art"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = filteredList,
                    key = { it.art.id }
                ) { item ->
                    HistorialArtCardItem(
                        artConEtapas = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// CARD FOR CHARLA
// -----------------------------------------------------------------------------
@Composable
fun HistorialCardItem(
    item: HistorialItem,
    onClick: () -> Unit,
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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

            // Sync Status Badge
            if (item.sincronizado) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SyncSuccessContainer,
                    modifier = Modifier
                        .border(1.dp, SyncSuccessBorder, RoundedCornerShape(6.dp))
                        .testTag("tag_sincronizado_${item.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✓ Sincronizado",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SyncSuccessText
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SyncPendingContainer,
                    modifier = Modifier
                        .border(1.dp, SyncPendingBorder, RoundedCornerShape(6.dp))
                        .testTag("tag_pendiente_sincronizar_${item.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏳ Pendiente de sincronizar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SyncPendingText
                        )
                    }
                }
            }

            // Docente
            Row(verticalAlignment = Alignment.CenterVertically) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
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

            // Tema tratado
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

            // Footer Row: N° Asistentes y estado de firmas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    color = if (item.asistentesFirmados == item.numeroAsistentes && item.numeroAsistentes > 0) SuccessGreenContainer else InacapRed.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Firmas",
                            tint = if (item.asistentesFirmados == item.numeroAsistentes && item.numeroAsistentes > 0) SuccessGreen else InacapRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.asistentesFirmados}/${item.numeroAsistentes} Firmas",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (item.asistentesFirmados == item.numeroAsistentes && item.numeroAsistentes > 0) SuccessGreen else InacapRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// CARD FOR ART
// -----------------------------------------------------------------------------
@Composable
fun HistorialArtCardItem(
    artConEtapas: ArtConEtapas,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val art = artConEtapas.art
    val etapasCount = artConEtapas.etapas.size

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_historial_art_${art.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Fecha",
                        tint = InacapRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = art.fecha,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = InacapRed.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = InacapRed,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ART",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = InacapRed
                        )
                    }
                }
            }

            // Sync Status Badge
            if (art.sincronizado) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SyncSuccessContainer,
                    modifier = Modifier
                        .border(1.dp, SyncSuccessBorder, RoundedCornerShape(6.dp))
                        .testTag("tag_sincronizado_art_${art.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✓ Sincronizado",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SyncSuccessText
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SyncPendingContainer,
                    modifier = Modifier
                        .border(1.dp, SyncPendingBorder, RoundedCornerShape(6.dp))
                        .testTag("tag_pendiente_sincronizar_art_${art.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏳ Pendiente de sincronizar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SyncPendingText
                        )
                    }
                }
            }

            // Actividad
            Text(
                text = art.trabajoActividad,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Docente
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Docente",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = art.docente,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Especialidad y Lugar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "Especialidad",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${art.especialidad} • ${art.lugar}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                modifier = Modifier.padding(vertical = 2.dp)
            )

            // Footer Row: N° Etapas y Estado de Firma del Responsable
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$etapasCount Etapa(s) analizada(s)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (art.responsableFirmado) {
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
                                contentDescription = "Firmado",
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Resp. Firmado",
                                style = MaterialTheme.typography.labelSmall,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "Firma pendiente",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

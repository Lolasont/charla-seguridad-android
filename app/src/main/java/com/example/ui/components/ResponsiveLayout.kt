package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Contenedor responsivo que envuelve el contenido de una pantalla.
 * Permite que el contenido ocupe todo el ancho disponible y gestiona
 * el espaciado vertical y horizontal según el diseño especificado.
 */
@Composable
fun ResponsiveLayout(
    modifier: Modifier = Modifier,
    verticalSpacing: Dp = 16.dp,
    horizontalSpacing: Dp = 16.dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

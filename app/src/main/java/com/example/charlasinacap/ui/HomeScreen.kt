package com.example.charlasinacap.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Bienvenido al prototipo de Charlas INACAP",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("asaa") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Crear / Editar ASAA") }
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = { navController.navigate("charla") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Registrar Charla de Seguridad") }
    }
}

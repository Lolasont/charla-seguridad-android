package com.example.charlasinacap.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.charlasinacap.ui.screens.AsaaScreen
import com.example.charlasinacap.ui.screens.CharlaScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Charlas INACAP – Prototipo") }) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("asaa") { AsaaScreen(onBack = { navController.popBackStack() }) }
            composable("charla") { CharlaScreen(onBack = { navController.popBackStack() }) }
        }
    }
}

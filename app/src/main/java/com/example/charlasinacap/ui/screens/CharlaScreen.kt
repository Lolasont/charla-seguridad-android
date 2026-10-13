package com.example.charlasinacap.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.charlasinacap.viewmodel.CharlaViewModel
import com.example.charlasinacap.viewmodel.CharlaViewModelFactory

@Composable
fun CharlaScreen(
    viewModel: CharlaViewModel = viewModel(factory = CharlaViewModelFactory()),
    onBack: () -> Unit = {}
) {
    // The UI implementation from the previous CharlaScreen code should be placed here.
    // For brevity, we reference that implementation directly.
    com.example.charlasinacap.ui.screens.CharlaScreenContent(viewModel = viewModel, onBack = onBack)
}

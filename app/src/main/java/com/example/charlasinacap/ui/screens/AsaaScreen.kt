package com.example.charlasinacap.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.charlasinacap.viewmodel.AsaaViewModel
import com.example.charlasinacap.viewmodel.AsaaViewModelFactory

@Composable
fun AsaaScreen(
    viewModel: AsaaViewModel = viewModel(factory = AsaaViewModelFactory()),
    onBack: () -> Unit = {}
) {
    // The UI implementation from the previous AsaaScreen code should be placed here.
    // For brevity, we reference that implementation directly.
    com.example.charlasinacap.ui.screens.AsaaScreenContent(viewModel = viewModel, onBack = onBack)
}

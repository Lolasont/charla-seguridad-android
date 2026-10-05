package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.InacapTopBar
import com.example.ui.navigation.NavScreen
import com.example.ui.screens.ArtScreen
import com.example.ui.screens.HistorialScreen
import com.example.ui.screens.RegistroScreen
import com.example.ui.theme.InacapRed
import com.example.ui.theme.InacapRedContainer
import com.example.ui.theme.LightGrayBackground
import com.example.ui.theme.TextSecondary

@Composable
fun MainScreen() {
    var currentScreen by rememberSaveable { mutableStateOf(NavScreen.REGISTRO) }

    // If on a secondary tab, pressing back returns to the primary tab (Registro)
    if (currentScreen != NavScreen.REGISTRO) {
        BackHandler {
            currentScreen = NavScreen.REGISTRO
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            InacapTopBar()
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .testTag("bottom_navigation_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavScreen.values().forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = InacapRed,
                            selectedTextColor = InacapRed,
                            indicatorColor = InacapRed.copy(alpha = 0.14f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag(screen.testTag)
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentScreen,
                label = "screen_transition"
            ) { targetScreen ->
                when (targetScreen) {
                    NavScreen.REGISTRO -> RegistroScreen()
                    NavScreen.ART -> ArtScreen()
                    NavScreen.HISTORIAL -> HistorialScreen()
                }
            }
        }
    }
}

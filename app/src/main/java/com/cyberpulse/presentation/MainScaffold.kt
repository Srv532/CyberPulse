package com.cyberpulse.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cyberpulse.core.navigation.Screen
import com.cyberpulse.presentation.components.CyberPulseBottomBar
import com.cyberpulse.ui.theme.DarkBackground

/**
 * Main App Scaffold
 * 
 * Root scaffold with bottom navigation that wraps the main content areas.
 * Handles tab switching with smooth animations.
 */
@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""
    
    // Determine if bottom bar should be visible
    val showBottomBar = remember(currentRoute) {
        currentRoute in listOf(
            Screen.Home.route,
            Screen.BreachRadar.route,
            Screen.Academy.route,
            Screen.Events.route
        )
    }
    
    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                CyberPulseBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                // Pop up to start destination to avoid building up stack
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}

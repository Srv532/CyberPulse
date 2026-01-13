package com.cyberpulse.core.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cyberpulse.domain.model.DataPreferences
import com.cyberpulse.presentation.auth.DataSovereigntyScreen
import com.cyberpulse.presentation.auth.LoginScreen
import com.cyberpulse.presentation.home.HomeScreen
import com.cyberpulse.presentation.breach.BreachRadarScreen
import com.cyberpulse.presentation.academy.AcademyScreen
import com.cyberpulse.presentation.events.EventsScreen
import com.cyberpulse.presentation.tools.HIBPScreen
import com.cyberpulse.presentation.tools.CVELookupScreen

/**
 * Navigation Routes
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object DataSovereignty : Screen("data_sovereignty")
    object Home : Screen("home")
    object BreachRadar : Screen("breach_radar")
    object Academy : Screen("academy")
    object Events : Screen("events")
    object HIBPCheck : Screen("hibp_check")
    object CVELookup : Screen("cve_lookup")
    object ArticleDetail : Screen("article/{articleId}") {
        fun createRoute(articleId: String) = "article/$articleId"
    }
    object Settings : Screen("settings")
}

/**
 * Main Navigation Host
 */
@Composable
fun CyberPulseNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { 300 },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { -300 },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { -300 },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { 300 },
                animationSpec = tween(300)
            )
        }
    ) {
        // Authentication Flow
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.DataSovereignty.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.DataSovereignty.route) {
            DataSovereigntyScreen(
                onContinue = { preferences ->
                    // Save preferences and navigate to home
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.DataSovereignty.route) { inclusive = true }
                    }
                },
                onStatelessMode = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.DataSovereignty.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Main App
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToArticle = { articleId ->
                    navController.navigate(Screen.ArticleDetail.createRoute(articleId))
                },
                onNavigateToHIBP = {
                    navController.navigate(Screen.HIBPCheck.route)
                },
                onNavigateToCVE = {
                    navController.navigate(Screen.CVELookup.route)
                }
            )
        }
        
        composable(Screen.BreachRadar.route) {
            BreachRadarScreen(
                onNavigateToBreachDetail = { /* Handle */ }
            )
        }
        
        composable(Screen.Academy.route) {
            AcademyScreen(
                onNavigateToCourse = { /* Handle */ }
            )
        }
        
        composable(Screen.Events.route) {
            EventsScreen(
                onNavigateToEvent = { /* Handle */ }
            )
        }
        
        // Tools
        composable(Screen.HIBPCheck.route) {
            HIBPScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.CVELookup.route) {
            CVELookupScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Bottom Navigation Items
 */
enum class BottomNavItem(
    val route: String,
    val title: String,
    val iconName: String
) {
    HOME(Screen.Home.route, "Home", "home"),
    BREACH(Screen.BreachRadar.route, "Breach Radar", "security"),
    ACADEMY(Screen.Academy.route, "Academy", "school"),
    EVENTS(Screen.Events.route, "Events", "event")
}

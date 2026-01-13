package com.cyberpulse.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * CyberPulse Design System - Theme Configuration
 * 
 * Dark-first design with neon cyber aesthetics.
 * Optimized for 120Hz smooth scrolling and OLED displays.
 */

// ═══════════════════════════════════════════════════════════════════
// DARK COLOR SCHEME (Primary & Default)
// ═══════════════════════════════════════════════════════════════════
private val CyberPulseDarkColorScheme = darkColorScheme(
    // Primary - Neon Cyan
    primary = CyberCyan,
    onPrimary = DarkBackground,
    primaryContainer = CyberCyanDark,
    onPrimaryContainer = CyberCyanLight,
    
    // Secondary - Neon Green
    secondary = CyberGreen,
    onSecondary = DarkBackground,
    secondaryContainer = CyberGreenDark,
    onSecondaryContainer = CyberGreenLight,
    
    // Tertiary - Neon Purple (for Academy section)
    tertiary = CyberPurple,
    onTertiary = DarkBackground,
    tertiaryContainer = CyberPurple.copy(alpha = 0.3f),
    onTertiaryContainer = CyberPurple,
    
    // Error - Neon Red (for breaches, critical alerts)
    error = CyberRed,
    onError = DarkBackground,
    errorContainer = CyberRed.copy(alpha = 0.2f),
    onErrorContainer = CyberRed,
    
    // Background & Surface
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondary,
    
    // Other
    outline = Divider,
    outlineVariant = GlassBorder,
    inverseSurface = TextPrimary,
    inverseOnSurface = DarkBackground,
    inversePrimary = CyberCyanDark,
    scrim = DarkBackground.copy(alpha = 0.8f)
)

// ═══════════════════════════════════════════════════════════════════
// THEME COMPOSABLE
// ═══════════════════════════════════════════════════════════════════
@Composable
fun CyberPulseTheme(
    darkTheme: Boolean = true, // Dark mode is always default
    dynamicColor: Boolean = false, // Disable dynamic color to maintain cyber aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Allow dynamic color on Android 12+ only if explicitly enabled
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context)
        }
        else -> CyberPulseDarkColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // Set status bar to dark background
            window.statusBarColor = DarkBackground.toArgb()
            window.navigationBarColor = DarkBackground.toArgb()
            
            // Light icons on dark background
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = CyberPulseTypography,
        shapes = CyberPulseShapes,
        content = content
    )
}

// ═══════════════════════════════════════════════════════════════════
// THEME EXTENSIONS
// ═══════════════════════════════════════════════════════════════════

/**
 * Extended color palette accessible within the theme.
 * Usage: CyberPulseTheme.extendedColors.cyberCyanGlow
 */
object CyberPulseExtendedColors {
    val cyberCyanGlow = CyberCyanGlow
    val cyberGreenGlow = CyberGreenGlow
    val cyberRedGlow = CyberRedGlow
    val cyberPurpleGlow = CyberPurpleGlow
    
    val glassWhite = GlassWhite
    val glassBorder = GlassBorder
    val glassHighlight = GlassHighlight
    
    val tagRansomware = TagRansomware
    val tagZeroDay = TagZeroDay
    val tagDataBreach = TagDataBreach
    val tagPatchTuesday = TagPatchTuesday
    val tagCVE = TagCVE
    val tagPhishing = TagPhishing
    
    val cardBackground = DarkCard
    val cardElevated = DarkCardElevated
    val shimmer = Shimmer
    val divider = Divider
    
    val statusOnline = StatusOnline
    val statusOffline = StatusOffline
}

// ═══════════════════════════════════════════════════════════════════
// HELPER FUNCTIONS
// ═══════════════════════════════════════════════════════════════════

/**
 * Get tag color based on tag type
 */
fun getTagColor(tag: String): androidx.compose.ui.graphics.Color {
    return when (tag.lowercase()) {
        "ransomware" -> TagRansomware
        "zeroday", "zero-day", "0day" -> TagZeroDay
        "databreach", "data breach", "breach" -> TagDataBreach
        "patchtuesday", "patch tuesday" -> TagPatchTuesday
        "cve" -> TagCVE
        "phishing" -> TagPhishing
        else -> CyberCyan
    }
}

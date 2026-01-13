package com.cyberpulse.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * CyberPulse Design System - Color Palette
 * 
 * A dark-first, cybersecurity-themed color system with neon accents
 * and glassmorphism support.
 */

// ═══════════════════════════════════════════════════════════════════
// PRIMARY COLORS - Neon Cyan
// ═══════════════════════════════════════════════════════════════════
val CyberCyan = Color(0xFF00F5FF)           // Primary neon cyan
val CyberCyanDark = Color(0xFF00C4CC)       // Darker variant
val CyberCyanLight = Color(0xFF7FFBFF)      // Lighter variant
val CyberCyanGlow = Color(0x4000F5FF)       // Glow/shadow effect (25% alpha)

// ═══════════════════════════════════════════════════════════════════
// SECONDARY COLORS - Neon Green (Matrix-inspired)
// ═══════════════════════════════════════════════════════════════════
val CyberGreen = Color(0xFF00FF7F)          // Matrix green
val CyberGreenDark = Color(0xFF00CC66)      // Darker variant
val CyberGreenLight = Color(0xFF7FFFBF)     // Lighter variant
val CyberGreenGlow = Color(0x4000FF7F)      // Glow effect

// ═══════════════════════════════════════════════════════════════════
// ACCENT COLORS - For alerts and categories
// ═══════════════════════════════════════════════════════════════════
val CyberRed = Color(0xFFFF3366)            // Critical alerts, breaches
val CyberRedGlow = Color(0x40FF3366)
val CyberOrange = Color(0xFFFF9933)         // Warnings
val CyberPurple = Color(0xFF9966FF)         // Academy/Learning
val CyberPurpleGlow = Color(0x409966FF)
val CyberYellow = Color(0xFFFFE033)         // Events/Hackathons

// ═══════════════════════════════════════════════════════════════════
// BACKGROUND COLORS - Deep Blues & Blacks
// ═══════════════════════════════════════════════════════════════════
val DarkBackground = Color(0xFF0A0E17)      // Deepest black-blue
val DarkSurface = Color(0xFF0F1624)         // Slightly lighter surface
val DarkCard = Color(0xFF151C2C)            // Card backgrounds
val DarkCardElevated = Color(0xFF1A2238)    // Elevated cards

// ═══════════════════════════════════════════════════════════════════
// GLASSMORPHISM COLORS
// ═══════════════════════════════════════════════════════════════════
val GlassWhite = Color(0x1AFFFFFF)          // 10% white for glass effect
val GlassBorder = Color(0x33FFFFFF)         // 20% white for glass borders
val GlassHighlight = Color(0x0DFFFFFF)      // 5% highlight

// ═══════════════════════════════════════════════════════════════════
// TEXT COLORS
// ═══════════════════════════════════════════════════════════════════
val TextPrimary = Color(0xFFFFFFFF)         // Pure white for headings
val TextSecondary = Color(0xFFB0BEC5)       // Muted for body text
val TextTertiary = Color(0xFF78909C)        // Even more muted
val TextDisabled = Color(0xFF546E7A)        // Disabled state

// ═══════════════════════════════════════════════════════════════════
// TAG COLORS - For smart news tags
// ═══════════════════════════════════════════════════════════════════
val TagRansomware = Color(0xFFFF3366)
val TagZeroDay = Color(0xFFFF9933)
val TagDataBreach = Color(0xFF00F5FF)
val TagPatchTuesday = Color(0xFF00FF7F)
val TagCVE = Color(0xFF9966FF)
val TagPhishing = Color(0xFFFFE033)

// ═══════════════════════════════════════════════════════════════════
// GRADIENT DEFINITIONS (for Brush usage)
// ═══════════════════════════════════════════════════════════════════
val GradientCyanStart = Color(0xFF00F5FF)
val GradientCyanEnd = Color(0xFF0077FF)
val GradientGreenStart = Color(0xFF00FF7F)
val GradientGreenEnd = Color(0xFF00CFCF)
val GradientRedStart = Color(0xFFFF3366)
val GradientRedEnd = Color(0xFFFF6B6B)

// ═══════════════════════════════════════════════════════════════════
// SYSTEM/UTILITY
// ═══════════════════════════════════════════════════════════════════
val Divider = Color(0xFF1E2A3D)
val Shimmer = Color(0xFF2A3A52)
val StatusOnline = Color(0xFF00FF7F)
val StatusOffline = Color(0xFF546E7A)

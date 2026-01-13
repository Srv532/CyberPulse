package com.cyberpulse.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.cyberpulse.R

/**
 * CyberPulse Design System - Typography
 * 
 * Using Inter for clean readability and JetBrains Mono for 
 * technical/code elements.
 */

// ═══════════════════════════════════════════════════════════════════
// FONT FAMILIES
// ═══════════════════════════════════════════════════════════════════
val InterFont = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_black, FontWeight.Black)
)

val JetBrainsMonoFont = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium, FontWeight.Medium),
    Font(R.font.jetbrains_mono_bold, FontWeight.Bold)
)

// ═══════════════════════════════════════════════════════════════════
// MATERIAL 3 TYPOGRAPHY SCALE
// ═══════════════════════════════════════════════════════════════════
val CyberPulseTypography = Typography(
    // Display - For hero sections
    displayLarge = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Black,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
        color = TextPrimary
    ),
    displayMedium = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    displaySmall = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    
    // Headline - For screen titles
    headlineLarge = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    headlineSmall = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    
    // Title - For card titles, section headers
    titleLarge = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        color = TextPrimary
    ),
    titleSmall = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = TextPrimary
    ),
    
    // Body - For content text
    bodyLarge = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = TextSecondary
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        color = TextSecondary
    ),
    bodySmall = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        color = TextTertiary
    ),
    
    // Label - For buttons, chips, tags
    labelLarge = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = TextPrimary
    ),
    labelMedium = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = TextPrimary
    ),
    labelSmall = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = TextSecondary
    )
)

// ═══════════════════════════════════════════════════════════════════
// CUSTOM TEXT STYLES
// ═══════════════════════════════════════════════════════════════════

// For code/CVE displays
val CodeStyle = TextStyle(
    fontFamily = JetBrainsMonoFont,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp,
    color = CyberGreen
)

// For timestamp/metadata
val MetadataStyle = TextStyle(
    fontFamily = InterFont,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.5.sp,
    color = TextTertiary
)

// For neon headlines
val NeonHeadlineStyle = TextStyle(
    fontFamily = InterFont,
    fontWeight = FontWeight.Black,
    fontSize = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = 1.sp,
    color = CyberCyan
)

// For tag labels
val TagStyle = TextStyle(
    fontFamily = InterFont,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.sp,
    lineHeight = 12.sp,
    letterSpacing = 0.8.sp
)

// For terminal-style headers (User Terminal drawer)
val TerminalHeaderStyle = TextStyle(
    fontFamily = JetBrainsMonoFont,
    fontWeight = FontWeight.Bold,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 2.sp,
    color = CyberCyan
)

// For terminal-style text
val TerminalTextStyle = TextStyle(
    fontFamily = JetBrainsMonoFont,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.5.sp,
    color = TextSecondary
)

// For glowing titles with animation support
val GlowHeadlineStyle = TextStyle(
    fontFamily = InterFont,
    fontWeight = FontWeight.Black,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = 0.sp,
    color = CyberCyan
)

// For status indicators
val StatusStyle = TextStyle(
    fontFamily = JetBrainsMonoFont,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp,
    lineHeight = 12.sp,
    letterSpacing = 1.sp
)

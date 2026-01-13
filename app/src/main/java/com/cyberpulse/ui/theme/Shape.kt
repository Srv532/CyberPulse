package com.cyberpulse.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * CyberPulse Design System - Shapes
 * 
 * Modern rounded corners with consistent radii across components.
 */

val CyberPulseShapes = Shapes(
    // For small components: chips, tags, small buttons
    extraSmall = RoundedCornerShape(4.dp),
    
    // For medium components: text fields, small cards
    small = RoundedCornerShape(8.dp),
    
    // For cards, dialogs, containers
    medium = RoundedCornerShape(16.dp),
    
    // For large cards, modals
    large = RoundedCornerShape(24.dp),
    
    // For full-screen or bottom sheets
    extraLarge = RoundedCornerShape(32.dp)
)

// ═══════════════════════════════════════════════════════════════════
// CUSTOM SHAPES
// ═══════════════════════════════════════════════════════════════════

// For profile avatars
val CircleShape = RoundedCornerShape(percent = 50)

// For news cards with asymmetric corners
val NewsCardShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp,
    bottomStart = 20.dp,
    bottomEnd = 20.dp
)

// For floating action buttons
val FabShape = RoundedCornerShape(16.dp)

// For bottom navigation
val BottomNavShape = RoundedCornerShape(
    topStart = 24.dp,
    topEnd = 24.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

// For tag chips
val TagShape = RoundedCornerShape(6.dp)

// For search bar
val SearchBarShape = RoundedCornerShape(12.dp)

// For glassmorphism cards
val GlassCardShape = RoundedCornerShape(20.dp)

// For profile drawer
val DrawerShape = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 32.dp,
    bottomStart = 0.dp,
    bottomEnd = 32.dp
)

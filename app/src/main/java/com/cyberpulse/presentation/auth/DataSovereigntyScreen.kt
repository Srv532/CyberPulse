package com.cyberpulse.presentation.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberpulse.domain.model.DataPreferences
import com.cyberpulse.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Privacy Sovereignty Control
 * 
 * A glassmorphic modal sheet that provides tactile control over data preferences:
 * - Neon-accented toggles with haptic feedback
 * - Smooth spring animations for toggle interactions
 * - Premium frosted glass aesthetic
 * - Stateless mode option for maximum privacy
 */

@Composable
fun DataSovereigntyScreen(
    onContinue: (DataPreferences) -> Unit,
    onStatelessMode: () -> Unit,
    viewModel: DataSovereigntyViewModel = hiltViewModel()
) {
    val haptic = LocalHapticFeedback.current
    
    // State with animated toggles
    var storeLoginSession by remember { mutableStateOf(false) }
    var allowUsageAnalytics by remember { mutableStateOf(false) }
    var personalizeFeeds by remember { mutableStateOf(false) }
    var saveReadHistory by remember { mutableStateOf(false) }
    var cacheFeedsOffline by remember { mutableStateOf(true) }
    var enableNotifications by remember { mutableStateOf(true) }
    
    // Entrance animations
    var isVisible by remember { mutableStateOf(false) }
    var cardsVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
        delay(300)
        cardsVisible = true
    }
    
    val isAnyStorageEnabled = storeLoginSession || allowUsageAnalytics || 
                              personalizeFeeds || saveReadHistory
    
    // Pulsing glow for header
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "headerGlow"
    )
    
    // Continue button animation
    val buttonScale by animateFloatAsState(
        targetValue = if (isAnyStorageEnabled) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Animated background glows
        AnimatedGlows(glowAlpha = glowAlpha)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .systemBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Header with entrance animation
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500)) + slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    initialOffsetY = { -60 }
                )
            ) {
                HeaderSection(glowAlpha = glowAlpha)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Privacy toggles with staggered animation
            AnimatedVisibility(
                visible = cardsVisible,
                enter = fadeIn(tween(400)) + expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Section header
                    SectionHeader(
                        title = "DATA PREFERENCES",
                        color = CyberCyan
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    GlassToggleCard(
                        icon = Icons.Outlined.Login,
                        title = "Store Login Session",
                        description = "Stay signed in between app launches",
                        isChecked = storeLoginSession,
                        onCheckedChange = { 
                            storeLoginSession = it
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        accentColor = CyberCyan,
                        animationDelay = 0
                    )
                    
                    GlassToggleCard(
                        icon = Icons.Outlined.Analytics,
                        title = "Allow Usage Analytics",
                        description = "Help us improve with anonymous data",
                        isChecked = allowUsageAnalytics,
                        onCheckedChange = { 
                            allowUsageAnalytics = it
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        accentColor = CyberGreen,
                        animationDelay = 50
                    )
                    
                    GlassToggleCard(
                        icon = Icons.Outlined.AutoAwesome,
                        title = "Personalize Feed",
                        description = "Learn your interests for relevant news",
                        isChecked = personalizeFeeds,
                        onCheckedChange = { 
                            personalizeFeeds = it
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        accentColor = CyberPurple,
                        animationDelay = 100
                    )
                    
                    GlassToggleCard(
                        icon = Icons.Outlined.History,
                        title = "Save Read History",
                        description = "Remember articles you've read",
                        isChecked = saveReadHistory,
                        onCheckedChange = { 
                            saveReadHistory = it
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        accentColor = CyberOrange,
                        animationDelay = 150
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Essential features section
                    SectionHeader(
                        title = "ESSENTIAL FEATURES",
                        color = CyberGreen
                    )
                    
                    Text(
                        text = "Recommended for best experience",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    GlassToggleCard(
                        icon = Icons.Outlined.Download,
                        title = "Cache for Offline",
                        description = "Store last 50 articles locally",
                        isChecked = cacheFeedsOffline,
                        onCheckedChange = { 
                            cacheFeedsOffline = it
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        accentColor = CyberGreen,
                        isRecommended = true,
                        animationDelay = 200
                    )
                    
                    GlassToggleCard(
                        icon = Icons.Outlined.Notifications,
                        title = "Enable Notifications",
                        description = "Get alerts for critical vulnerabilities",
                        isChecked = enableNotifications,
                        onCheckedChange = { 
                            enableNotifications = it
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        accentColor = CyberYellow,
                        isRecommended = true,
                        animationDelay = 250
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Stateless mode notice
            AnimatedVisibility(
                visible = !isAnyStorageEnabled && cardsVisible,
                enter = fadeIn(tween(300)) + expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
                exit = fadeOut(tween(200)) + shrinkVertically()
            ) {
                StatelessModeCard()
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Continue button with spring animation
            AnimatedVisibility(
                visible = cardsVisible,
                enter = fadeIn(tween(300, delayMillis = 400)) + slideInVertically(
                    initialOffsetY = { 50 }
                )
            ) {
                ContinueButton(
                    isStatelessMode = !isAnyStorageEnabled,
                    scale = buttonScale,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val preferences = DataPreferences(
                            storeLoginSession = storeLoginSession,
                            allowUsageAnalytics = allowUsageAnalytics,
                            personalizeFeeds = personalizeFeeds,
                            saveReadHistory = saveReadHistory,
                            cacheFeedsOffline = cacheFeedsOffline,
                            enableNotifications = enableNotifications
                        )
                        
                        if (isAnyStorageEnabled) {
                            onContinue(preferences)
                        } else {
                            onStatelessMode()
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HeaderSection(glowAlpha: Float) {
    Column {
        // Shield icon with glow
        Box(
            modifier = Modifier.size(72.dp),
            contentAlignment = Alignment.Center
        ) {
            // Glow behind icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .blur(24.dp)
                    .background(
                        color = CyberCyan.copy(alpha = glowAlpha),
                        shape = CircleShape
                    )
            )
            
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = CyberCyan
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = "Your Data,\nYour Control",
            style = GlowHeadlineStyle.copy(
                lineHeight = 42.sp
            )
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Choose what data CyberPulse can store. You can change these settings anytime from your profile.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(3.dp, 16.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        
        Spacer(modifier = Modifier.width(10.dp))
        
        Text(
            text = title,
            style = TerminalHeaderStyle,
            color = color
        )
    }
}

@Composable
private fun GlassToggleCard(
    icon: ImageVector,
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color,
    isRecommended: Boolean = false,
    animationDelay: Int = 0
) {
    // Spring animation for toggle interaction
    val scale by animateFloatAsState(
        targetValue = if (isChecked) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardScale"
    )
    
    // Animated colors
    val backgroundColor by animateColorAsState(
        targetValue = if (isChecked) accentColor.copy(alpha = 0.08f) else DarkCard.copy(alpha = 0.6f),
        animationSpec = tween(300),
        label = "bgColor"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isChecked) accentColor.copy(alpha = 0.4f) else GlassBorder.copy(alpha = 0.3f),
        animationSpec = tween(300),
        label = "borderColor"
    )
    
    val glowAlpha by animateFloatAsState(
        targetValue = if (isChecked) 0.15f else 0f,
        animationSpec = tween(300),
        label = "glowAlpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        // Glow effect when selected
        if (isChecked) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(20.dp)
                    .graphicsLayer { alpha = glowAlpha }
                    .background(
                        color = accentColor,
                        shape = RoundedCornerShape(18.dp)
                    )
            )
        }
        
        // Glass card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
            color = backgroundColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon container with glow
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = accentColor.copy(alpha = if (isChecked) 0.2f else 0.1f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(14.dp))
                
                // Text content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = if (isChecked) TextPrimary else TextSecondary
                        )
                        
                        if (isRecommended) {
                            Spacer(modifier = Modifier.width(8.dp))
                            RecommendedBadge()
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Custom neon toggle
                NeonSwitch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    accentColor = accentColor
                )
            }
        }
    }
}

@Composable
private fun NeonSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color
) {
    val thumbPosition by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "thumbPosition"
    )
    
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = accentColor,
            checkedTrackColor = accentColor.copy(alpha = 0.3f),
            checkedBorderColor = accentColor.copy(alpha = 0.5f),
            uncheckedThumbColor = TextTertiary,
            uncheckedTrackColor = DarkCard,
            uncheckedBorderColor = GlassBorder.copy(alpha = 0.3f)
        )
    )
}

@Composable
private fun RecommendedBadge() {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = CyberGreen.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = CyberGreen.copy(alpha = 0.3f)
        )
    ) {
        Text(
            text = "REC",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = StatusStyle,
            color = CyberGreen
        )
    }
}

@Composable
private fun StatelessModeCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = CyberOrange.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = CyberOrange.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = CyberOrange.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PrivacyTip,
                    contentDescription = null,
                    tint = CyberOrange,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column {
                Text(
                    text = "Stateless Mode",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = CyberOrange
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "No personal data will be stored. You'll need to sign in each time. Perfect for maximum privacy.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ContinueButton(
    isStatelessMode: Boolean,
    scale: Float,
    onClick: () -> Unit
) {
    val buttonColor by animateColorAsState(
        targetValue = if (isStatelessMode) CyberOrange else CyberCyan,
        animationSpec = tween(300),
        label = "buttonColor"
    )
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = DarkBackground
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isStatelessMode) {
                Icon(
                    imageVector = Icons.Outlined.PrivacyTip,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            
            Text(
                text = if (isStatelessMode) "Enter Stateless Mode" else "Continue",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
            
            if (!isStatelessMode) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedGlows(glowAlpha: Float) {
    // Top-left cyan glow
    Box(
        modifier = Modifier
            .offset(x = (-80).dp, y = 80.dp)
            .size(250.dp)
            .blur(120.dp)
            .background(
                color = CyberCyan.copy(alpha = glowAlpha * 0.4f),
                shape = CircleShape
            )
    )
    
    // Bottom-right purple glow
    Box(
        modifier = Modifier
            .offset(x = 200.dp, y = 600.dp)
            .size(280.dp)
            .blur(140.dp)
            .background(
                color = CyberPurple.copy(alpha = glowAlpha * 0.3f),
                shape = CircleShape
            )
    )
    
    // Center green accent
    Box(
        modifier = Modifier
            .offset(x = 100.dp, y = 350.dp)
            .size(150.dp)
            .blur(80.dp)
            .background(
                color = CyberGreen.copy(alpha = glowAlpha * 0.2f),
                shape = CircleShape
            )
    )
}

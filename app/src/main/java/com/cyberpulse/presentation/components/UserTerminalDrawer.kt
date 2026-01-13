package com.cyberpulse.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cyberpulse.domain.model.UserProfile
import com.cyberpulse.ui.theme.*

/**
 * User Terminal & Profile Drawer
 * 
 * A sidebar drawer that provides deep customization and quick utilities:
 * - Terminal-like monospaced headers for technical feel
 * - "Am I Pwned?" quick utility with animated status
 * - CVE Lookup quick access
 * - Premium glassmorphic styling
 * - Smooth spring animations throughout
 */

@Composable
fun UserTerminalDrawer(
    userProfile: UserProfile?,
    isOpen: Boolean,
    onHIBPClick: () -> Unit,
    onCVEClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    
    // Animation for drawer entrance
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(isOpen) {
        showContent = isOpen
    }
    
    // Pulsing animation for status indicators
    val infiniteTransition = rememberInfiniteTransition(label = "terminal")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "statusPulse"
    )
    
    // Cursor blink for terminal effect
    val cursorVisible by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorBlink"
    )
    
    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = Color.Transparent,
        drawerShape = RoundedCornerShape(
            topEnd = 32.dp,
            bottomEnd = 32.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(320.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DarkSurface.copy(alpha = 0.95f),
                            DarkBackground.copy(alpha = 0.98f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GlassBorder,
                            GlassBorder.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(
                        topEnd = 32.dp,
                        bottomEnd = 32.dp
                    )
                )
        ) {
            // Background glow
            Box(
                modifier = Modifier
                    .offset(x = (-50).dp, y = 100.dp)
                    .size(200.dp)
                    .blur(100.dp)
                    .background(
                        color = CyberCyan.copy(alpha = 0.08f),
                        shape = CircleShape
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Terminal header
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(300)) + slideInHorizontally(
                        initialOffsetX = { -50 }
                    )
                ) {
                    TerminalHeader(cursorAlpha = cursorVisible)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // User profile section
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(300, delayMillis = 100)) + slideInHorizontally(
                        initialOffsetX = { -50 }
                    )
                ) {
                    UserProfileCard(
                        userProfile = userProfile,
                        statusPulseAlpha = pulseAlpha
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Quick utilities section
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(300, delayMillis = 200)) + expandVertically()
                ) {
                    Column {
                        TerminalSectionHeader(
                            title = "QUICK_UTILITIES",
                            prefix = "$"
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Am I Pwned? utility
                        QuickUtilityCard(
                            icon = Icons.Outlined.Security,
                            title = "Am I Pwned?",
                            description = "Check if your email was in a breach",
                            accentColor = CyberRed,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onHIBPClick()
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        // CVE Lookup utility
                        QuickUtilityCard(
                            icon = Icons.Outlined.BugReport,
                            title = "CVE Lookup",
                            description = "Search vulnerability database",
                            accentColor = CyberOrange,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onCVEClick()
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(28.dp))
                
                // Navigation section
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(300, delayMillis = 300)) + expandVertically()
                ) {
                    Column {
                        TerminalSectionHeader(
                            title = "NAVIGATION",
                            prefix = "~"
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        TerminalMenuItem(
                            icon = Icons.Outlined.Dashboard,
                            label = "Dashboard",
                            isActive = true,
                            onClick = { onClose() }
                        )
                        
                        TerminalMenuItem(
                            icon = Icons.Outlined.BookmarkBorder,
                            label = "Saved Articles",
                            onClick = { }
                        )
                        
                        TerminalMenuItem(
                            icon = Icons.Outlined.History,
                            label = "Read History",
                            onClick = { }
                        )
                        
                        TerminalMenuItem(
                            icon = Icons.Outlined.School,
                            label = "Security Academy",
                            badgeText = "NEW",
                            badgeColor = CyberPurple,
                            onClick = { }
                        )
                        
                        TerminalMenuItem(
                            icon = Icons.Outlined.Event,
                            label = "Events & CTFs",
                            badgeText = "3",
                            badgeColor = CyberYellow,
                            onClick = { }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(28.dp))
                
                // Settings section
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(300, delayMillis = 400)) + expandVertically()
                ) {
                    Column {
                        TerminalSectionHeader(
                            title = "SYSTEM",
                            prefix = "#"
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        TerminalMenuItem(
                            icon = Icons.Outlined.Settings,
                            label = "Settings & Privacy",
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onSettingsClick()
                            }
                        )
                        
                        TerminalMenuItem(
                            icon = Icons.Outlined.Notifications,
                            label = "Notifications",
                            onClick = { }
                        )
                        
                        TerminalMenuItem(
                            icon = Icons.Outlined.Info,
                            label = "About CyberPulse",
                            onClick = { }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Sign out button
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(300, delayMillis = 500)) + slideInVertically(
                        initialOffsetY = { 30 }
                    )
                ) {
                    SignOutButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSignOutClick()
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Version info
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(300, delayMillis = 600))
                ) {
                    VersionInfo()
                }
            }
        }
    }
}

@Composable
private fun TerminalHeader(cursorAlpha: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "user@cyberpulse",
            style = TerminalHeaderStyle.copy(
                fontSize = 14.sp,
                letterSpacing = 1.sp
            ),
            color = CyberGreen
        )
        
        Text(
            text = ":",
            style = TerminalHeaderStyle,
            color = TextSecondary
        )
        
        Text(
            text = "~",
            style = TerminalHeaderStyle,
            color = CyberCyan
        )
        
        Text(
            text = "$",
            style = TerminalHeaderStyle,
            color = TextSecondary,
            modifier = Modifier.padding(start = 4.dp)
        )
        
        // Blinking cursor
        Box(
            modifier = Modifier
                .padding(start = 4.dp)
                .size(8.dp, 16.dp)
                .graphicsLayer { alpha = cursorAlpha }
                .background(CyberCyan)
        )
    }
}

@Composable
private fun UserProfileCard(
    userProfile: UserProfile?,
    statusPulseAlpha: Float
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = DarkCard.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = GlassBorder.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with status ring
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                // Status ring
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    CyberCyan,
                                    CyberGreen,
                                    CyberCyan
                                )
                            ),
                            shape = CircleShape
                        )
                )
                
                if (userProfile?.photoUrl != null) {
                    AsyncImage(
                        model = userProfile.photoUrl,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(CyberCyan, CyberGreen)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile?.displayName?.first()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = DarkBackground
                        )
                    }
                }
                
                // Online status dot
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(14.dp)
                        .background(DarkSurface, CircleShape)
                        .padding(2.dp)
                        .background(
                            color = StatusOnline.copy(alpha = statusPulseAlpha),
                            shape = CircleShape
                        )
                )
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userProfile?.displayName ?: "Guest User",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = userProfile?.email ?: "Not signed in",
                    style = TerminalTextStyle.copy(fontSize = 11.sp),
                    color = TextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Security status badge
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(CyberGreen, CircleShape)
                    )
                    
                    Spacer(modifier = Modifier.width(6.dp))
                    
                    Text(
                        text = "SECURE",
                        style = StatusStyle,
                        color = CyberGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun TerminalSectionHeader(
    title: String,
    prefix: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prefix,
            style = TerminalTextStyle,
            color = CyberPurple
        )
        
        Spacer(modifier = Modifier.width(6.dp))
        
        Text(
            text = title,
            style = TerminalHeaderStyle.copy(
                fontSize = 11.sp
            ),
            color = TextTertiary
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Divider.copy(alpha = 0.5f),
            thickness = 0.5.dp
        )
    }
}

@Composable
private fun QuickUtilityCard(
    icon: ImageVector,
    title: String,
    description: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "pressScale"
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale },
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = accentColor.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = accentColor.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = accentColor
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }
            
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = accentColor.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun TerminalMenuItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    badgeText: String? = null,
    badgeColor: Color = CyberCyan,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) CyberCyan.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(200),
        label = "menuItemBg"
    )
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) CyberCyan else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isActive) TextPrimary else TextSecondary,
                modifier = Modifier.weight(1f)
            )
            
            if (badgeText != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = badgeColor.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = StatusStyle,
                        color = badgeColor
                    )
                }
            }
        }
    }
}

@Composable
private fun SignOutButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = CyberRed.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = CyberRed.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Logout,
                contentDescription = null,
                tint = CyberRed,
                modifier = Modifier.size(18.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Sign Out",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = CyberRed
            )
        }
    }
}

@Composable
private fun VersionInfo() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CyberPulse v1.0.0",
            style = TerminalTextStyle.copy(fontSize = 10.sp),
            color = TextTertiary.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Built with 🔒 by the CyberPulse Team",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = TextTertiary.copy(alpha = 0.4f)
        )
    }
}

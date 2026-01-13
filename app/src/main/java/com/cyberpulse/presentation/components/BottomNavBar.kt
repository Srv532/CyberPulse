package com.cyberpulse.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.cyberpulse.ui.theme.*

/**
 * CyberPulse Bottom Navigation Bar
 * 
 * Premium bottom navigation with:
 * - Glassmorphism background
 * - Animated selection indicators
 * - Neon glow effects on selected item
 * - Smooth scale animations
 */

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val accentColor: androidx.compose.ui.graphics.Color = CyberCyan,
    val badgeCount: Int? = null
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        accentColor = CyberCyan
    ),
    BottomNavItem(
        route = "breach_radar",
        title = "Breaches",
        selectedIcon = Icons.Filled.Security,
        unselectedIcon = Icons.Outlined.Security,
        accentColor = CyberRed,
        badgeCount = 3
    ),
    BottomNavItem(
        route = "academy",
        title = "Academy",
        selectedIcon = Icons.Filled.School,
        unselectedIcon = Icons.Outlined.School,
        accentColor = CyberPurple
    ),
    BottomNavItem(
        route = "events",
        title = "Events",
        selectedIcon = Icons.Filled.Event,
        unselectedIcon = Icons.Outlined.Event,
        accentColor = CyberYellow
    )
)

@Composable
fun CyberPulseBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DarkSurface.copy(alpha = 0.95f),
        shadowElevation = 8.dp,
        shape = BottomNavShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                CyberNavItem(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = { onNavigate(item.route) }
                )
            }
        }
    }
}

@Composable
private fun CyberNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "navItemScale"
    )
    
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.6f,
        animationSpec = tween(200),
        label = "navItemAlpha"
    )
    
    Column(
        modifier = Modifier
            .scale(animatedScale)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (isSelected) {
                    Modifier.background(
                        color = item.accentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(32.dp)
        ) {
            BadgedBox(
                badge = {
                    if (item.badgeCount != null && item.badgeCount > 0) {
                        Badge(
                            containerColor = CyberRed,
                            contentColor = TextPrimary
                        ) {
                            Text(
                                text = if (item.badgeCount > 9) "9+" else item.badgeCount.toString(),
                                style = TagStyle
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.title,
                    tint = if (isSelected) item.accentColor else TextSecondary.copy(alpha = animatedAlpha),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = item.title,
                style = TagStyle,
                color = item.accentColor,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Floating Action Button with cyber styling
 */
@Composable
fun CyberFAB(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = CyberCyan,
    extended: Boolean = false,
    text: String = ""
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fabScale"
    )
    
    if (extended && text.isNotEmpty()) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            modifier = modifier.scale(scale),
            containerColor = containerColor,
            contentColor = DarkBackground,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    } else {
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier.scale(scale),
            containerColor = containerColor,
            contentColor = DarkBackground,
            shape = FabShape
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription
            )
        }
    }
}

/**
 * Animated indicator dot for bottom nav
 */
@Composable
fun NavIndicator(
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "indicator")
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "indicatorGlow"
    )
    
    Box(
        modifier = modifier
            .size(width = 24.dp, height = 4.dp)
            .background(
                color = color.copy(alpha = glowAlpha),
                shape = CircleShape
            )
    )
}

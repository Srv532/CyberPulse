package com.cyberpulse.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cyberpulse.ui.theme.*

/**
 * Search Bar Component
 * 
 * Cyber-styled search bar with glassmorphism effect
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search news, CVEs, breaches...",
    isActive: Boolean = false,
    onActiveChange: (Boolean) -> Unit = {}
) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = isActive,
        onActiveChange = onActiveChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = TextTertiary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = CyberCyan
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Clear",
                        tint = TextSecondary
                    )
                }
            }
        },
        colors = SearchBarDefaults.colors(
            containerColor = DarkCard,
            dividerColor = Divider,
            inputFieldColors = TextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = CyberCyan
            )
        ),
        shape = SearchBarShape
    ) {
        // Search suggestions would go here
    }
}

/**
 * Stats Card for dashboard
 */
@Composable
fun StatsCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: @Composable () -> Unit,
    accentColor: Color = CyberCyan,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = GlassCardShape,
        color = accentColor.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = accentColor.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = accentColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary
                )
                
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = accentColor
                )
                
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

/**
 * Empty state placeholder
 */
@Composable
fun EmptyState(
    title: String,
    message: String,
    icon: @Composable () -> Unit,
    action: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = DarkCard,
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextTertiary,
            textAlign = TextAlign.Center
        )
        
        if (action != null) {
            Spacer(modifier = Modifier.height(24.dp))
            action()
        }
    }
}

/**
 * Section header component
 */
@Composable
fun SectionHeader(
    title: String,
    action: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = TextPrimary
        )
        
        if (action != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = action,
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberCyan
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Severity badge for CVE/alerts
 */
@Composable
fun SeverityBadge(
    severity: String,
    score: Double? = null,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (severity.uppercase()) {
        "CRITICAL" -> CyberRed to TextPrimary
        "HIGH" -> CyberOrange to DarkBackground
        "MEDIUM" -> CyberYellow to DarkBackground
        "LOW" -> CyberGreen to DarkBackground
        else -> TextTertiary to TextPrimary
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = severity.uppercase(),
                style = TagStyle,
                color = textColor
            )
            
            if (score != null) {
                Text(
                    text = String.format("%.1f", score),
                    style = TagStyle,
                    color = textColor
                )
            }
        }
    }
}

/**
 * Pulse indicator for live/updating content
 */
@Composable
fun LiveIndicator(
    label: String = "LIVE",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PulsingDot(color = CyberRed)
        
        Text(
            text = label,
            style = TagStyle,
            color = CyberRed
        )
    }
}

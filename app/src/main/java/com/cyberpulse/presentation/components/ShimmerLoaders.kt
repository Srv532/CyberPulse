package com.cyberpulse.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cyberpulse.ui.theme.*

/**
 * Shimmer Loading Components
 * 
 * Premium skeleton loaders with animated shimmer effect for smooth loading states.
 * Creates a cyber-themed loading experience that matches the app aesthetic.
 */

// Shimmer colors - Cyber themed
private val ShimmerColorShades = listOf(
    DarkCard.copy(alpha = 0.9f),
    DarkCardElevated.copy(alpha = 0.4f),
    DarkCard.copy(alpha = 0.9f)
)

// Enhanced cyber shimmer with subtle neon hints
private val CyberShimmerColors = listOf(
    DarkCard,
    DarkCardElevated.copy(alpha = 0.6f),
    CyberCyan.copy(alpha = 0.05f),
    DarkCardElevated.copy(alpha = 0.6f),
    DarkCard
)

/**
 * Creates an infinite shimmer animation brush
 */
@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    
    return Brush.linearGradient(
        colors = ShimmerColorShades,
        start = Offset(translateAnimation - 500f, translateAnimation - 500f),
        end = Offset(translateAnimation, translateAnimation)
    )
}

/**
 * Premium cyber-themed shimmer with subtle neon glow
 */
@Composable
fun cyberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "cyberShimmer")
    
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "cyberShimmerTranslate"
    )
    
    val glowAlpha by transition.animateFloat(
        initialValue = 0.03f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerGlow"
    )
    
    return Brush.linearGradient(
        colors = listOf(
            DarkCard,
            DarkCardElevated.copy(alpha = 0.5f),
            CyberCyan.copy(alpha = glowAlpha),
            DarkCardElevated.copy(alpha = 0.5f),
            DarkCard
        ),
        start = Offset(translateAnimation - 600f, translateAnimation - 300f),
        end = Offset(translateAnimation, translateAnimation + 300f)
    )
}

/**
 * Shimmer placeholder for news card
 */
@Composable
fun NewsCardShimmer(
    modifier: Modifier = Modifier
) {
    val brush = shimmerBrush()
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = DarkCard,
                shape = NewsCardShape
            )
            .padding(16.dp)
    ) {
        Column {
            // Image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(GlassCardShape)
                    .background(brush)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Source & time row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(24.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Title placeholder (2 lines)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Summary placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Tags placeholder
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(24.dp)
                            .clip(TagShape)
                            .background(brush)
                    )
                }
            }
        }
    }
}

/**
 * Shimmer placeholder for compact news item
 */
@Composable
fun CompactNewsShimmer(
    modifier: Modifier = Modifier
) {
    val brush = shimmerBrush()
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkCard, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(brush)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
    }
}

/**
 * Shimmer for breach card
 */
@Composable
fun BreachCardShimmer(
    modifier: Modifier = Modifier
) {
    val brush = shimmerBrush()
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = CyberRed.copy(alpha = 0.05f),
                shape = GlassCardShape
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Logo placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(brush)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
            
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(brush)
            )
        }
    }
}

/**
 * Full feed shimmer loading state
 */
@Composable
fun FeedLoadingShimmer(
    itemCount: Int = 5,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(itemCount) {
            NewsCardShimmer()
        }
    }
}

/**
 * Pulsing dot indicator
 */
@Composable
fun PulsingDot(
    color: Color = CyberCyan,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    Box(
        modifier = modifier
            .size((8 * scale).dp)
            .background(
                color = color.copy(alpha = alpha),
                shape = CircleShape
            )
    )
}

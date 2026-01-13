package com.cyberpulse.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Entrance Animations
 * 
 * Premium staggered animations for feed items and UI elements.
 * Creates a polished, dynamic feel when content loads.
 */

/**
 * Staggered fade-in animation for list items
 */
@Composable
fun StaggeredAnimatedItem(
    index: Int,
    delayPerItem: Long = 50L,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(key1 = index) {
        delay(index * delayPerItem)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            ),
            initialOffsetY = { it / 4 }
        ) + scaleIn(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            ),
            initialScale = 0.92f
        ),
        exit = fadeOut() + slideOutVertically()
    ) {
        content()
    }
}

/**
 * Scale-in animation with bounce effect
 */
@Composable
fun BounceScaleIn(
    visible: Boolean,
    delayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounceScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, delayMillis = delayMillis),
        label = "bounceAlpha"
    )
    
    Box(
        modifier = Modifier
            .scale(scale)
            .alpha(alpha)
    ) {
        content()
    }
}

/**
 * Slide-in from side animation
 */
@Composable
fun SlideInFromSide(
    visible: Boolean,
    fromRight: Boolean = false,
    delayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            animationSpec = tween(
                durationMillis = 400,
                delayMillis = delayMillis,
                easing = FastOutSlowInEasing
            ),
            initialOffsetX = { if (fromRight) it else -it }
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = delayMillis
            )
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { if (fromRight) it else -it }
        ) + fadeOut()
    ) {
        content()
    }
}

/**
 * Pulse animation for highlighting
 */
@Composable
fun PulseAnimation(
    content: @Composable (Float) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    content(scale)
}

/**
 * Typewriter text animation
 */
@Composable
fun TypewriterText(
    text: String,
    delayPerChar: Long = 50L,
    onComplete: () -> Unit = {}
): String {
    var displayedText by remember { mutableStateOf("") }
    
    LaunchedEffect(text) {
        displayedText = ""
        text.forEachIndexed { index, char ->
            delay(delayPerChar)
            displayedText = text.substring(0, index + 1)
        }
        onComplete()
    }
    
    return displayedText
}

/**
 * Floating animation for cards
 */
@Composable
fun FloatingEffect(
    content: @Composable (androidx.compose.ui.Modifier) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatingY"
    )
    
    content(
        Modifier.offset { IntOffset(0, offsetY.toInt()) }
    )
}

/**
 * Glow pulse animation for neon effects
 */
@Composable
fun GlowPulse(
    content: @Composable (Float) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    
    content(alpha)
}

/**
 * Staggered column for list animations
 */
@Composable
fun StaggeredColumn(
    itemCount: Int,
    modifier: Modifier = Modifier,
    staggerDelay: Long = 50L,
    content: @Composable (Int) -> Unit
) {
    Column(modifier = modifier) {
        repeat(itemCount) { index ->
            StaggeredAnimatedItem(
                index = index,
                delayPerItem = staggerDelay
            ) {
                content(index)
            }
        }
    }
}

/**
 * Animated counter for numbers
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    durationMillis: Int = 1000
): Int {
    var animatedValue by remember { mutableStateOf(0) }
    
    LaunchedEffect(targetValue) {
        val startValue = animatedValue
        val startTime = System.currentTimeMillis()
        
        while (animatedValue != targetValue) {
            val elapsed = System.currentTimeMillis() - startTime
            val progress = (elapsed.toFloat() / durationMillis).coerceIn(0f, 1f)
            
            animatedValue = (startValue + (targetValue - startValue) * progress).toInt()
            
            if (progress >= 1f) {
                animatedValue = targetValue
                break
            }
            
            delay(16) // ~60fps
        }
    }
    
    return animatedValue
}

/**
 * Card flip animation
 */
@Composable
fun FlipCard(
    isFlipped: Boolean,
    frontContent: @Composable () -> Unit,
    backContent: @Composable () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "flipRotation"
    )
    
    Box {
        if (rotation <= 90f) {
            frontContent()
        } else {
            backContent()
        }
    }
}

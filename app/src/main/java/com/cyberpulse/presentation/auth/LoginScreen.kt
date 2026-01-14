package com.cyberpulse.presentation.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberpulse.ui.theme.*
import com.cyberpulse.BuildConfig
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * CyberPulse One-Tap Auth
 * 
 * A minimalist, keyboard-less entry point featuring:
 * - Deep black background with ambient glow effects
 * - Floating biometric/Google login button (easy thumb access)
 * - Smooth spring animations and micro-interactions
 * - Premium cyberpunk aesthetic with neon accents
 */

private const val TAG = "GoogleSignIn"

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val uiState by viewModel.uiState.collectAsState()
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    var showBiometricButton by remember { mutableStateOf(false) }
    
    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )
    
    // Floating animation for the auth button
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatAnimation"
    )
    
    // Spring animation for button scale
    val buttonScale by animateFloatAsState(
        targetValue = if (uiState is AuthUiState.Loading) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )
    
    // Initialize One-Tap client
    val oneTapClient = remember { Identity.getSignInClient(context) }
    val signInRequest = remember { buildSignInRequest() }
    
    // Activity result launcher for One-Tap
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            scope.launch {
                handleSignInResult(
                    result = result,
                    oneTapClient = oneTapClient,
                    onSuccess = { idToken ->
                        viewModel.signInWithGoogle(idToken)
                    },
                    onError = { error ->
                        errorMessage = error
                        showError = true
                    }
                )
            }
        } else {
            errorMessage = "Sign-in was cancelled"
            showError = true
            viewModel.setLoading(false)
        }
    }
    
    // Staggered entrance animations
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
        delay(400)
        showBiometricButton = true
    }
    
    // Handle state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> onLoginSuccess()
            is AuthUiState.Error -> {
                errorMessage = (uiState as AuthUiState.Error).message
                showError = true
            }
            else -> {}
        }
    }
    
    // Main UI - Deep Black Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Animated background glow effects
        AnimatedBackgroundGlows(glowAlpha = glowAlpha)
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            // Logo and Title with entrance animation
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600)) + slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    initialOffsetY = { -100 }
                )
            ) {
                LogoSection()
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Tagline
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 200))
            ) {
                Text(
                    text = "Your Privacy-First\nSecurity Companion",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 28.sp
                    ),
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Floating Auth Button Section
            AnimatedVisibility(
                visible = showBiometricButton,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = (-floatOffset).dp)
                ) {
                    // Floating Biometric/Google Button
                    FloatingAuthButton(
                        isLoading = uiState is AuthUiState.Loading,
                        scale = buttonScale,
                        glowAlpha = glowAlpha,
                        onClick = {
                            scope.launch {
                                startOneTapSignIn(
                                    context = context,
                                    oneTapClient = oneTapClient,
                                    signInRequest = signInRequest,
                                    launcher = launcher,
                                    onLoading = { viewModel.setLoading(true) },
                                    onError = { error ->
                                        errorMessage = error
                                        showError = true
                                        viewModel.setLoading(false)
                                    }
                                )
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Instruction text
                    Text(
                        text = "Tap to authenticate",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Privacy note with entrance animation
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 600))
            ) {
                PrivacyNote()
            }
            
            // Error message
            AnimatedVisibility(
                visible = showError,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                ErrorMessage(
                    message = errorMessage,
                    onDismiss = { showError = false }
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun LogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glowing App Icon
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer glow
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .blur(40.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                CyberCyan.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            )
            
            // Main logo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(CyberCyan, CyberGreen)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = CyberCyan.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CP",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = DarkBackground
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // App Name with glow effect
        Text(
            text = "CyberPulse",
            style = NeonHeadlineStyle.copy(
                fontSize = 40.sp,
                letterSpacing = 2.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FloatingAuthButton(
    isLoading: Boolean,
    scale: Float,
    glowAlpha: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pulsing glow ring
        Box(
            modifier = Modifier
                .size(100.dp)
                .blur(20.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CyberCyan.copy(alpha = glowAlpha * 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        
        // Outer ring
        Box(
            modifier = Modifier
                .size(90.dp)
                .scale(scale)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CyberCyan.copy(alpha = 0.8f),
                            CyberGreen.copy(alpha = 0.6f)
                        )
                    ),
                    shape = CircleShape
                )
        )
        
        // Main button
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .size(76.dp)
                .scale(scale),
            shape = CircleShape,
            containerColor = DarkSurface,
            contentColor = CyberCyan,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = CyberCyan,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Fingerprint,
                    contentDescription = "Authenticate",
                    modifier = Modifier.size(36.dp),
                    tint = CyberCyan
                )
            }
        }
    }
}

@Composable
private fun AnimatedBackgroundGlows(glowAlpha: Float) {
    // Top-right cyan glow
    Box(
        modifier = Modifier
            .offset(x = 180.dp, y = (-80).dp)
            .size(350.dp)
            .blur(160.dp)
            .background(
                color = CyberCyan.copy(alpha = glowAlpha * 0.25f),
                shape = CircleShape
            )
    )
    
    // Bottom-left green glow
    Box(
        modifier = Modifier
            .offset(x = (-150).dp, y = 600.dp)
            .size(300.dp)
            .blur(140.dp)
            .background(
                color = CyberGreen.copy(alpha = glowAlpha * 0.15f),
                shape = CircleShape
            )
    )
    
    // Center subtle purple accent
    Box(
        modifier = Modifier
            .offset(x = 50.dp, y = 300.dp)
            .size(200.dp)
            .blur(100.dp)
            .background(
                color = CyberPurple.copy(alpha = glowAlpha * 0.1f),
                shape = CircleShape
            )
    )
}

@Composable
private fun PrivacyNote() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = GlassWhite.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = GlassBorder.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(CyberGreen, CircleShape)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "We only use Google for authentication.\nNo personal data is collected without your consent.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CyberRed.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = CyberRed.copy(alpha = 0.3f)
        ),
        onClick = onDismiss
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodySmall,
            color = CyberRed,
            textAlign = TextAlign.Center
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
// SIGN-IN LOGIC
// ═══════════════════════════════════════════════════════════════════

private fun buildSignInRequest(): BeginSignInRequest {
    return BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()
}

private suspend fun startOneTapSignIn(
    context: Context,
    oneTapClient: SignInClient,
    signInRequest: BeginSignInRequest,
    launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    onLoading: () -> Unit,
    onError: (String) -> Unit
) {
    onLoading()
    try {
        val result = oneTapClient.beginSignIn(signInRequest).await()
        val intentSenderRequest = IntentSenderRequest.Builder(
            result.pendingIntent.intentSender
        ).build()
        launcher.launch(intentSenderRequest)
    } catch (e: Exception) {
        Log.e(TAG, "One-Tap sign-in failed", e)
        onError(
            when (e) {
                is ApiException -> "No Google accounts available. Please add a Google account to your device."
                else -> "Sign-in failed: ${e.localizedMessage}"
            }
        )
    }
}

private suspend fun handleSignInResult(
    result: ActivityResult,
    oneTapClient: SignInClient,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
        val idToken = credential.googleIdToken
        
        if (idToken != null) {
            onSuccess(idToken)
        } else {
            onError("No ID token received from Google")
        }
    } catch (e: ApiException) {
        Log.e(TAG, "Failed to get credential", e)
        onError("Failed to get credentials: ${e.localizedMessage}")
    }
}

// ═══════════════════════════════════════════════════════════════════
// VIEW MODEL STATE
// ═══════════════════════════════════════════════════════════════════

sealed class AuthUiState {
    object Initial : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

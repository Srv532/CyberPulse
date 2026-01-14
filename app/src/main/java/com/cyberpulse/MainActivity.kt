package com.cyberpulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.cyberpulse.core.navigation.CyberPulseNavHost
import com.cyberpulse.ui.theme.CyberPulseTheme
import com.cyberpulse.ui.theme.DarkBackground
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity
 * 
 * Single-activity architecture with Jetpack Compose navigation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before super.onCreate()
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // Anti-Tampering: Detect Debugger
        if (!BuildConfig.DEBUG && android.os.Debug.isDebuggerConnected()) {
            android.os.Process.killProcess(android.os.Process.myPid())
        }
        
        // Initialize Firebase App Check
        com.google.firebase.FirebaseApp.initializeApp(this)
        val firebaseAppCheck = com.google.firebase.appcheck.FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        setContent {
            CyberPulseTheme {
                val navController = rememberNavController()
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    CyberPulseNavHost(navController = navController)
                }
            }
        }
    }
}

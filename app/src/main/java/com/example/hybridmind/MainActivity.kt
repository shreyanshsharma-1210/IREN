package com.example.hybridmind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.room.Room
import androidx.work.*
import com.example.hybridmind.core.NetworkMonitor
import com.example.hybridmind.data.ChatRepository
import com.example.hybridmind.data.ModelDownloader
import com.example.hybridmind.data.RepositoryProvider
import com.example.hybridmind.data.local.AppDatabase
import com.example.hybridmind.ui.auth.LoginScreen
import com.example.hybridmind.ui.auth.SignupScreen
import com.example.hybridmind.ui.settings.SettingsScreen
import com.example.hybridmind.ui.chat.ChatScreen
import com.example.hybridmind.ui.download.DownloadScreen
import com.example.hybridmind.ui.landing.LandingScreen
import com.example.hybridmind.ui.download.DownloadScreen
import com.example.hybridmind.ui.theme.IRENTheme
import com.example.hybridmind.workers.AutoPruneWorker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    // Using lazy initialization with singleton pattern
    private val networkMonitor: NetworkMonitor by lazy { RepositoryProvider.getNetworkMonitor(applicationContext) }
    private val database: AppDatabase by lazy { RepositoryProvider.getDatabase(applicationContext) }
    private val chatRepository: ChatRepository by lazy { RepositoryProvider.getChatRepository(applicationContext) }
    private val modelDownloader: ModelDownloader by lazy { ModelDownloader(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configure window to handle keyboard properly
        window.setSoftInputMode(
            android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        // Components are now lazily initialized via RepositoryProvider singleton
        // This prevents re-initialization if activity recreates (e.g., after camera)
        android.util.Log.d("MainActivity", "Activity created - using singleton instances")

        // Schedule auto-prune worker
        scheduleAutoPruneWorker()

        setContent {
            // Observe theme preference
            val themePreference = remember { com.example.hybridmind.data.ThemePreference(applicationContext) }
            val themeMode by themePreference.observeThemeMode().collectAsState(
                initial = com.example.hybridmind.data.ThemeMode.SYSTEM
            )
            
            IRENTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        chatRepository = chatRepository,
                        networkMonitor = networkMonitor,
                        modelDownloader = modelDownloader
                    )
                }
            }
        }
    }

    private fun scheduleAutoPruneWorker() {
        val workRequest = PeriodicWorkRequestBuilder<AutoPruneWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "AutoPruneWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    
    
    override fun onStop() {
        super.onStop()
        // Clean up empty sessions only when app goes to background (not visible)
        // This prevents cleanup during theme changes or configuration changes
        // onStop is called when activity is no longer visible to user
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                chatRepository.deleteEmptySessions()
                android.util.Log.d("MainActivity", "Cleaned up empty sessions on stop")
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error cleaning up empty sessions: ${e.message}", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Only cleanup if the activity is truly finishing (user closed app)
        // Don't cleanup if just going to background or being recreated
        if (isFinishing) {
            android.util.Log.d("MainActivity", "Activity finishing - cleaning up repository")
            chatRepository.cleanup()
        } else {
            android.util.Log.d("MainActivity", "Activity destroyed but not finishing - keeping model alive")
        }
    }
}

@Composable
fun AppNavigation(
    chatRepository: ChatRepository,
    networkMonitor: NetworkMonitor,
    modelDownloader: ModelDownloader
) {
    // Check if user is signed in and verified
    val currentUser = FirebaseAuth.getInstance().currentUser
    val initialScreen = if (currentUser != null && currentUser.isEmailVerified) {
        // Check if user already has a model downloaded
        if (modelDownloader.getAvailableModel() != null) {
            Screen.Chat // Existing user with model - go directly to chat
        } else {
            Screen.Download // New user needs to download
        }
    } else {
        Screen.Landing // Not signed in or not verified
    }
    
    var currentScreen by remember { mutableStateOf(initialScreen) }
    var showModelWarning by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    when (currentScreen) {
        Screen.Landing -> {
            LandingScreen(
                onGetStarted = {
                    currentScreen = Screen.Login // "Get Started" defaults to Login
                },
                onLogin = {
                    currentScreen = Screen.Login
                },
                onSignup = {
                    currentScreen = Screen.Signup
                }
            )
        }
        Screen.Login -> {
            LoginScreen(
                onLoginSuccess = {
                    // Check if model already exists - skip download if it does
                    currentScreen = if (modelDownloader.getAvailableModel() != null) {
                        Screen.Chat  // Go directly to chat with initializing bubble
                    } else {
                        Screen.Download  // Need to download model first
                    }
                },
                onNavigateToSignup = {
                    currentScreen = Screen.Signup
                },
                onBack = {
                    currentScreen = Screen.Landing
                }
            )
        }
        Screen.Signup -> {
            SignupScreen(
                onSignupSuccess = {
                    currentScreen = Screen.Login
                },
                onNavigateToLogin = {
                    currentScreen = Screen.Login
                },
                onBack = {
                    currentScreen = Screen.Landing
                }
            )
        }
        Screen.Download -> {
            DownloadScreen(
                modelDownloader = modelDownloader,
                networkMonitor = networkMonitor,
                onDownloadComplete = { modelPath ->
                    scope.launch {
                        try {
                            android.util.Log.d("MainActivity", "=== Model Initialization Started ===")
                            android.util.Log.d("MainActivity", "Model path: $modelPath")
                            
                            // Check if file exists
                            val file = java.io.File(modelPath)
                            if (!file.exists()) {
                                throw Exception("Model file not found at: $modelPath")
                            }
                            android.util.Log.d("MainActivity", "Model file exists: ${file.length()} bytes")
                            
                            // Initialize the model
                            chatRepository.initializeOfflineModel(modelPath)
                            
                            android.util.Log.d("MainActivity", "✅ Model initialization successful!")
                            
                            showModelWarning = false  // Clear warning if user downloads later
                            currentScreen = Screen.Chat
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "❌ Model initialization failed: ${e.message}", e)
                            e.printStackTrace()
                            // TODO: Show user-friendly error dialog with retry option
                            // For now, error is logged and user stays on download screen
                        }
                    }
                },
                onSkip = {
                    // User skipped download - go to chat with warning
                    showModelWarning = true
                    currentScreen = Screen.Chat
                },
                onBack = {
                    // Sign out and go back to landing
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                    currentScreen = Screen.Landing
                }
            )
        }
        Screen.Settings -> {
            SettingsScreen(
                chatRepository = chatRepository,
                modelDownloader = modelDownloader,
                onBack = { currentScreen = Screen.Chat },
                onSignOut = {
                   FirebaseAuth.getInstance().signOut()
                   currentScreen = Screen.Login
                },
                onModelSwitched = {
                    currentScreen = Screen.Chat
                }
            )
        }
        Screen.Chat -> {
            ChatScreen(
                chatRepository = chatRepository,
                networkMonitor = networkMonitor,
                showModelWarning = showModelWarning,
                onDismissWarning = { showModelWarning = false },
                onSignOut = {
                    FirebaseAuth.getInstance().signOut()
                    currentScreen = Screen.Login
                },
                onSettingsClick = {
                    currentScreen = Screen.Settings
                }
            )
        }
    }
}

sealed class Screen {
    object Landing : Screen()
    object Login : Screen()
    object Signup : Screen()
    object Download : Screen()
    object Chat : Screen()
    object Settings : Screen()
}

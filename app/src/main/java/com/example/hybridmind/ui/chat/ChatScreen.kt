package com.example.hybridmind.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.hybridmind.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hybridmind.core.NetworkMonitor
import com.example.hybridmind.data.ChatRepository
import com.example.hybridmind.data.ModelDownloader
import com.example.hybridmind.data.local.ChatSession
import com.example.hybridmind.data.local.Message
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage // Assuming Coil is available or using standard Image with Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.io.InputStream
import androidx.core.content.FileProvider
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatRepository: ChatRepository,
    networkMonitor: NetworkMonitor,
    showModelWarning: Boolean = false,
    onDismissWarning: () -> Unit = {},
    onSignOut: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current
    val modelDownloader = remember { ModelDownloader(context) }
    
    // Model initialization state
    var modelInitStatus by remember { mutableStateOf<ModelInitStatus>(ModelInitStatus.Checking) }
    var showStatusBubble by remember { mutableStateOf(true) }
    
    // Greeting state
    var showGreeting by remember { mutableStateOf(true) }
    val userName = FirebaseAuth.getInstance().currentUser?.displayName
    val firstName = remember(userName) { 
        if (userName.isNullOrBlank()) "there" else userName.trim().split(" ").firstOrNull() ?: "there"
    }
    
    // Streaming state
    var streamingMessage by remember { mutableStateOf<String?>(null) }
    var isStreaming by remember { mutableStateOf(false) }
    var streamingJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    
    
    var sessions by remember { mutableStateOf<List<ChatSession>>(emptyList()) }
    var currentSessionId by rememberSaveable { mutableStateOf<String?>(null) }
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var userInput by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var currentSessionImageData by remember { mutableStateOf<ByteArray?>(null) } // Persistent image context
    var isFirstImageSend by remember { mutableStateOf(true) } // Track if this is first time sending current image
    var fullScreenImagePath by remember { mutableStateOf<String?>(null) } // For full-screen viewer
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var debugInfo by remember { mutableStateOf("Not started") }
    
    // Track last user message that needs retry (when response failed/stopped)
    var lastFailedUserMessageId by remember { mutableStateOf<String?>(null) }
    
    // Voice input state
    var isVoiceInputActive by remember { mutableStateOf(false) }
    
    // Image picker dialog state
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            try {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    currentSessionImageData = inputStream.readBytes()
                    isFirstImageSend = true
                    android.util.Log.d("ChatScreen", "Gallery image loaded: ${currentSessionImageData?.size} bytes")
                }
            } catch (e: Exception) {
                android.util.Log.e("ChatScreen", "Error loading gallery image: ${e.message}", e)
            }
        }
    }
    
    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            selectedImageUri = tempCameraUri
            tempCameraUri?.let { uri ->
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        currentSessionImageData = inputStream.readBytes()
                        isFirstImageSend = true
                        android.util.Log.d("ChatScreen", "Camera image loaded: ${currentSessionImageData?.size} bytes")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ChatScreen", "Error loading camera image: ${e.message}", e)
                }
            }
        }
    }
    
    // Voice input launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(
                android.speech.RecognizerIntent.EXTRA_RESULTS
            )
            matches?.firstOrNull()?.let { spokenText ->
                // Append to existing input
                userInput = if (userInput.isNotBlank()) {
                    "$userInput $spokenText"
                } else {
                    spokenText
                }
            }
        }
        isVoiceInputActive = false
    }
    
    // Helper functions (defined before launchers that use them)
    fun startVoiceInput(launcher: androidx.activity.result.ActivityResultLauncher<android.content.Intent>, setActive: (Boolean) -> Unit) {
        val intent = android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, 
                    android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault())
            putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }
        setActive(true)
        launcher.launch(intent)
    }
    
    fun startCamera() {
        val file = java.io.File(context.cacheDir, "temp_camera_${System.currentTimeMillis()}.jpg")
        tempCameraUri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        cameraLauncher.launch(tempCameraUri)
    }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startVoiceInput(speechRecognizerLauncher) { isVoiceInputActive = it }
        } else {
            errorMessage = "Microphone permission denied"
        }
    }
    
    // Helper function that uses permissionLauncher (defined after launcher)
    fun requestMicPermissionAndStart() {
        when (androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        )) {
            android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                startVoiceInput(speechRecognizerLauncher) { isVoiceInputActive = it }
            }
            else -> {
                permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        }
    }
    
    val isOnline by networkMonitor.isOnline.collectAsState(initial = true)

    // Auto-initialize model on first launch
    LaunchedEffect(Unit) {
        if (!chatRepository.isOfflineModelReady()) {
            modelInitStatus = ModelInitStatus.Initializing
            try {
                android.util.Log.d("ChatScreen", "=== Starting Model Initialization ===")
                // Get user's preferred model or first available
                val modelName = modelDownloader.getAvailableModel()
                
                if (modelName != null) {
                    val modelPath = modelDownloader.getModelPath(modelName, "litertlm")
                    android.util.Log.d("ChatScreen", "Initializing model: $modelName at $modelPath")
                    chatRepository.initializeOfflineModel(modelPath)
                    modelInitStatus = ModelInitStatus.Ready
                    android.util.Log.d("ChatScreen", "✓ Model initialized successfully (first time)")
                } else {
                    modelInitStatus = ModelInitStatus.NotDownloaded
                    android.util.Log.w("ChatScreen", "No model available for initialization")
                }
            } catch (e: Exception) {
                modelInitStatus = ModelInitStatus.Error(e.message ?: "Unknown error")
                android.util.Log.e("ChatScreen", "Model initialization failed", e)
            }
        } else {
            modelInitStatus = ModelInitStatus.Ready
            android.util.Log.d("ChatScreen", "✓ Model already initialized (reusing existing instance)")
        }
    }

    // Load sessions and intelligently handle "New Chat" creation
    // Only initialize session if currentSessionId is null (first launch or after signout)
    // rememberSaveable persists currentSessionId across configuration changes (theme changes)
    LaunchedEffect(Unit) {
        // Only initialize if we don't already have a session ID
        if (currentSessionId != null) {
            android.util.Log.d("ChatScreen", "Session already set (ID: $currentSessionId), skipping initialization")
            // Just reload the sessions list
            sessions = chatRepository.getAllSessions()
            return@LaunchedEffect
        }
        
        // Wait for Firebase auth to be ready (with timeout)
        var authReady = FirebaseAuth.getInstance().currentUser != null
        var attempts = 0
        while (!authReady && attempts < 10) {
            kotlinx.coroutines.delay(100) // Wait 100ms
            authReady = FirebaseAuth.getInstance().currentUser != null
            attempts++
        }
        
        if (!authReady) {
            android.util.Log.e("ChatScreen", "Firebase auth not ready after waiting")
            errorMessage = "Authentication not ready. Please try again."
            return@LaunchedEffect
        }
        
        sessions = chatRepository.getAllSessions()
        
        // Smart session selection:
        // 1. Check if there's already an empty session (to avoid creating duplicates on theme changes)
        // 2. If found, reuse it
        // 3. If not, create a new "New Chat"
        
        var emptySession: ChatSession? = null
        for (session in sessions) {
            val sessionMessages = chatRepository.getMessagesForSession(session.id)
            if (sessionMessages.isEmpty()) {
                emptySession = session
                break
            }
        }
        
        if (emptySession != null) {
            // Found an existing empty session - reuse it
            currentSessionId = emptySession.id
            android.util.Log.d("ChatScreen", "Reusing existing empty session: ${emptySession.title}")
        } else {
            // No empty session exists - create a new one
            try {
                val isOnline = networkMonitor.isOnline.first()
                val sessionId = chatRepository.createNewSession("New Chat", !isOnline)
                currentSessionId = sessionId
                sessions = chatRepository.getAllSessions()
                android.util.Log.d("ChatScreen", "Created new session on app start")
            } catch (e: Exception) {
                android.util.Log.e("ChatScreen", "Failed to create session: ${e.message}", e)
                errorMessage = "Failed to create session: ${e.message}"
            }
        }
    }

    // Load messages when session changes
    LaunchedEffect(currentSessionId) {
        currentSessionId?.let {
            messages = chatRepository.getMessagesForSession(it)
            debugInfo = "Loaded ${messages.size} messages for session $it"
            // Show greeting for new empty sessions
            showGreeting = messages.isEmpty()
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF10111A)) // JetQuotes Dark Theme Background
    ) {
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                ChatDrawerContent(
                    sessions = sessions,
                    currentSessionId = currentSessionId,
                    currentSessionMessages = messages,
                    onSessionClick = { sessionId ->
                        scope.launch {
                            try {
                                // Before switching, clean up any empty sessions (except the target one)
                                val allSessions = chatRepository.getAllSessions()
                                for (session in allSessions) {
                                    // Skip the session we're switching to
                                    if (session.id == sessionId) continue
                                    
                                    // Check if session has no messages
                                    val sessionMessages = chatRepository.getMessagesForSession(session.id)
                                    if (sessionMessages.isEmpty()) {
                                        // Delete empty session
                                        chatRepository.deleteSession(session.id)
                                        android.util.Log.d("ChatScreen", "Deleted empty session: ${session.title}")
                                    }
                                }
                                
                                // Now switch to the selected session
                                currentSessionId = sessionId
                                
                                // Refresh sessions list to reflect deletions
                                sessions = chatRepository.getAllSessions()
                                
                                drawerState.close()
                            } catch (e: Exception) {
                                android.util.Log.e("ChatScreen", "Error switching sessions: ${e.message}", e)
                                errorMessage = "Failed to switch chat: ${e.message}"
                            }
                        }
                    },
                    onNewChat = {
                        scope.launch {
                            try {
                                val sessionId = chatRepository.createNewSession("New Chat", !isOnline)
                                currentSessionId = sessionId
                                sessions = chatRepository.getAllSessions()
                                drawerState.close()
                            } catch (e: Exception) {
                                android.util.Log.e("ChatScreen", "Error creating new chat: ${e.message}", e)
                                errorMessage = "Failed to create new chat: ${e.message}"
                            }
                        }
                    },
                    onSignOut = onSignOut,
                    onSettingsClick = onSettingsClick
                )
            }
        }
    ) {
        // Use MaterialTheme to detect actual app theme (not system theme)
        // Simple check: if background is closer to white, it's light theme
        val isDarkTheme = MaterialTheme.colorScheme.background != Color.White && 
                          MaterialTheme.colorScheme.background.red < 0.5f
                Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isDarkTheme) {
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0A0E27),
                                    Color(0xFF10111A)
                                )
                            )
                        } else {
                            // Light theme: Pure white background per spec
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFFFFFF), // Pure white
                                    Color(0xFFFFFFFF)  // Pure white
                                )
                            )
                        }
                    ),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "IREN",
                                 style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                                    fontSize = 28.sp,
                                    letterSpacing = 2.sp
                                ),
                                color = if (isDarkTheme) Color.White else Color.Black
                            )
                            Text(
                                text = if (isOnline) "Online - Gemini" else "Offline - Local",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isOnline) Color(0xFF00BFA5) else {
                                    if (isDarkTheme) Color(0xFFB0B3C6) else Color(0xFF4F4F4F)
                                }
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = if (isDarkTheme) Color.White else Color.Black
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = if (isDarkTheme) Color(0xFF000000) else Color(0xFFFFFFFF)
                    ),
                    modifier = Modifier.shadow(2.dp)  // Subtle shadow per spec
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                // Warning banner when model is not downloaded (user skipped)
                if (showModelWarning && modelInitStatus is ModelInitStatus.NotDownloaded) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Warning",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Offline model not downloaded",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = "You're using online mode only.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = onSettingsClick,
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                ) {
                                    Text("Settings", fontWeight = FontWeight.Bold)
                                }
                                IconButton(
                                    onClick = onDismissWarning,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
            ChatContent(
                messages = messages,
                userInput = userInput,
                onUserInputChange = { userInput = it },
                isLoading = isLoading,
                modelInitStatus = modelInitStatus,
                showStatusBubble = showStatusBubble,
                onDismissStatusBubble = { showStatusBubble = false },
                onSendMessage = {
                    debugInfo = "CALLBACK TRIGGERED!"
                    if (currentSessionId != null) {
                        streamingJob = scope.launch {
                            try {
                                // Hide greeting after first message
                                if (messages.isEmpty()) {
                                    showGreeting = false
                                }
                                
                                isLoading = true
                                isStreaming = true
                                streamingMessage = ""
                                errorMessage = null
                                debugInfo = "Sending message..."
                                val msg = if (userInput.isBlank()) {
                                    "Summarize this image and describe what is displayed"
                                } else {
                                    userInput
                                }
                                
                                // Clear input immediately
                                val messageText = userInput
                                userInput = ""
                                
                                // Use persistent image data if available
                                val imageData = currentSessionImageData
                                
                                // Clear image preview immediately (but keep persistent context)
                                selectedImageUri = null
                                
                                // Prevent sending if no image and no text (though button should be disabled)
                                if (msg.isBlank() && imageData == null) {
                                    isLoading = false
                                    isStreaming = false
                                    return@launch
                                }

                                // First, save the user message
                                chatRepository.saveUserMessage(
                                    sessionId = currentSessionId!!,
                                    userMessage = msg,
                                    imageData = imageData,
                                    saveImageToMessage = isFirstImageSend && imageData != null
                                )
                                
                                // Reload messages immediately to show user message in UI
                                debugInfo = "User message saved, showing in UI..."
                                messages = chatRepository.getMessagesForSession(currentSessionId!!)
                                
                                // Then generate the response with streaming
                                chatRepository.generateResponseStream(
                                    sessionId = currentSessionId!!,
                                    userMessage = msg,
                                    imageData = imageData,
                                    onChunk = { chunk ->
                                        streamingMessage = chunk
                                    }
                                )
                                
                                // Mark that we've sent this image once
                                if (imageData != null && isFirstImageSend) {
                                    isFirstImageSend = false
                                }
                                
                                debugInfo = "Response generated, reloading..."
                                messages = chatRepository.getMessagesForSession(currentSessionId!!)
                                debugInfo = "Reloaded: ${messages.size} messages"
                            } catch (e: kotlinx.coroutines.CancellationException) {
                                // Job was cancelled - save partial response
                                android.util.Log.d("ChatScreen", "Streaming cancelled, saving partial response")
                                if (!streamingMessage.isNullOrBlank()) {
                                    try {
                                        // Save the partial streaming message
                                        val partialMessage = com.example.hybridmind.data.local.Message(
                                            id = java.util.UUID.randomUUID().toString(),
                                            session_id = currentSessionId!!,
                                            role = "model",
                                            content = streamingMessage!!,
                                            timestamp = System.currentTimeMillis()
                                        )
                                        chatRepository.saveMessage(partialMessage)
                                        messages = chatRepository.getMessagesForSession(currentSessionId!!)
                                    } catch (saveError: Exception) {
                                        android.util.Log.e("ChatScreen", "Error saving partial response", saveError)
                                    }
                                }
                                // Mark as failed for retry
                                lastFailedUserMessageId = messages.findLast { it.role == "user" }?.id
                            } catch (e: Exception) {
                                e.printStackTrace()
                                errorMessage = "ERROR: ${e.javaClass.simpleName}: ${e.message}"
                                debugInfo = "ERROR: ${e.javaClass.simpleName}: ${e.message}"
                                // Mark as failed for retry
                                lastFailedUserMessageId = messages.findLast { it.role == "user" }?.id
                            } finally {
                                isLoading = false
                                isStreaming = false
                                streamingMessage = null
                                streamingJob = null
                            }
                        }
                    } else {
                        debugInfo = "ERROR: No session ID!"  
                    }
                },
                onRetryMessage = { messageToRetry ->
                    // Clear the failed state
                    lastFailedUserMessageId = null
                    // Retry by sending the same message
                    if (currentSessionId != null) {
                        streamingJob = scope.launch {
                            try {
                                isLoading = true
                                isStreaming = true
                                streamingMessage = ""
                                errorMessage = null
                                
                                // Get image data if the message had an image
                                val imageData = messageToRetry.image_path?.let { path ->
                                    try {
                                        java.io.File(path).readBytes()
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                                
                                // Generate response with streaming
                                chatRepository.generateResponseStream(
                                    sessionId = currentSessionId!!,
                                    userMessage = messageToRetry.content,
                                    imageData = imageData,
                                    onChunk = { chunk ->
                                        streamingMessage = chunk
                                    }
                                )
                                
                                messages = chatRepository.getMessagesForSession(currentSessionId!!)
                            } catch (e: kotlinx.coroutines.CancellationException) {
                                // Job was cancelled - save partial response 
                                if (!streamingMessage.isNullOrBlank()) {
                                    try {
                                        val partialMessage = com.example.hybridmind.data.local.Message(
                                            id = java.util.UUID.randomUUID().toString(),
                                            session_id = currentSessionId!!,
                                            role = "model",
                                            content = streamingMessage!!,
                                            timestamp = System.currentTimeMillis()
                                        )
                                        chatRepository.saveMessage(partialMessage)
                                        messages = chatRepository.getMessagesForSession(currentSessionId!!)
                                    } catch (saveError: Exception) {
                                        android.util.Log.e("ChatScreen", "Error saving partial response", saveError)
                                    }
                                }
                                lastFailedUserMessageId = messageToRetry.id
                            } catch (e: Exception) {
                                e.printStackTrace()
                                errorMessage = "ERROR: ${e.javaClass.simpleName}: ${e.message}"
                                lastFailedUserMessageId = messageToRetry.id
                            } finally {
                                isLoading = false
                                isStreaming = false
                                streamingMessage = null
                                streamingJob = null
                            }
                        }
                    }
                },
                lastFailedMessageId = lastFailedUserMessageId,
                onPickImage = {
                    showImagePickerDialog = true
                },
                onStopResponse = {
                    streamingJob?.cancel()
                },
                selectedImageUri = selectedImageUri,
                onRemoveImage = { 
                    selectedImageUri = null
                    currentSessionImageData = null // Clear persistent context too
                    isFirstImageSend = true // Reset for next image
                },
                onImageClick = { imagePath ->
                    fullScreenImagePath = imagePath
                },
                errorMessage = errorMessage,
                debugInfo = debugInfo,
                showGreeting = showGreeting,
                firstName = firstName,
                streamingMessage = streamingMessage,
                isStreaming = isStreaming,
                isVoiceInputActive = isVoiceInputActive,
                onVoiceInputClick = { requestMicPermissionAndStart() },
                onStopVoiceInput = { isVoiceInputActive = false }
            )
        }
    }
    
    
    // Image picker bottom sheet (full-width from bottom)
    if (showImagePickerDialog) {
        val isDarkTheme = MaterialTheme.colorScheme.background != Color.White && 
                          MaterialTheme.colorScheme.background.red < 0.5f
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    onClick = { showImagePickerDialog = false },
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) Color(0xFF2A2B36) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Title
                    Text(
                        text = "Choose Image Source",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkTheme) Color.White else Color(0xFF1F2937),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Camera option
                    Surface(
                        onClick = {
                            startCamera()
                            showImagePickerDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDarkTheme) Color(0xFF363748) else Color(0xFFF3F4F6)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF00BFA5).copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color(0xFF00BFA5),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Camera",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = if (isDarkTheme) Color.White else Color(0xFF1F2937)
                            )
                        }
                    }
                    
                    // Gallery option
                    Surface(
                        onClick = {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                            showImagePickerDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDarkTheme) Color(0xFF363748) else Color(0xFFF3F4F6)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF00BFA5).copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color(0xFF00BFA5),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Gallery",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = if (isDarkTheme) Color.White else Color(0xFF1F2937)
                            )
                        }
                    }
                    
                    // Cancel button
                    TextButton(
                        onClick = { showImagePickerDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            "Cancel",
                            color = if (isDarkTheme) Color(0xFFB0B3C6) else Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
    
    // Full-screen image viewer
    if (fullScreenImagePath != null) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { fullScreenImagePath = null }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = fullScreenImagePath,
                    contentDescription = "Full screen image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
                
                // Close button
                IconButton(
                    onClick = { fullScreenImagePath = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
    }
}
}

@Composable
fun ChatDrawerContent(
    sessions: List<ChatSession>,
    currentSessionId: String?,
    currentSessionMessages: List<Message>,
    onSessionClick: (String) -> Unit,
    onNewChat: () -> Unit,
    onSignOut: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Text(
            text = "Chat History",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onNewChat,
            modifier = Modifier.fillMaxWidth(),
            enabled = currentSessionMessages.isNotEmpty()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("New Chat")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(sessions) { session ->
                NavigationDrawerItem(
                    label = {
                        Column {
                            Text(session.title)
                            if (session.is_offline_only) {
                                Text(
                                    text = "Private (Offline)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    },
                    selected = session.id == currentSessionId,
                    onClick = { onSessionClick(session.id) }
                )
            }
        }

        HorizontalDivider()

        TextButton(
            onClick = onSettingsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Settings, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Settings")
        }
    }
}

@Composable
fun ChatContent(
    messages: List<Message>,
    userInput: String,
    onUserInputChange: (String) -> Unit,
    isLoading: Boolean,
    modelInitStatus: ModelInitStatus = ModelInitStatus.Ready,
    showStatusBubble: Boolean = false,
    onDismissStatusBubble: () -> Unit = {},
    onSendMessage: () -> Unit,
    onRetryMessage: (Message) -> Unit = {},
    lastFailedMessageId: String? = null,
    onPickImage: () -> Unit,
    onStopResponse: () -> Unit = {},
    selectedImageUri: Uri?,
    onRemoveImage: () -> Unit,
    onImageClick: (String) -> Unit = {}, // For full-screen image view
    errorMessage: String? = null,
    debugInfo: String = "",
    showGreeting: Boolean = false,
    firstName: String = "there",
    streamingMessage: String? = null,
    isStreaming: Boolean = false,
    isVoiceInputActive: Boolean = false,
    onVoiceInputClick: () -> Unit = {},
    onStopVoiceInput: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto-scroll when messages change or during streaming
    LaunchedEffect(messages.size, streamingMessage?.length) {
        if (messages.isNotEmpty() || streamingMessage != null) {
            scope.launch {
                // Calculate last item index (messages + streaming message if present)
                val lastIndex = if (streamingMessage != null) {
                    messages.size  // streaming message is an additional item
                } else {
                    maxOf(0, messages.size - 1)
                }
                listState.scrollToItem(lastIndex)  // Instant scroll for better UX
            }
        }
    }

    // Use MaterialTheme to detect actual app theme (not system theme)
    val isDarkTheme = MaterialTheme.colorScheme.background != Color.White && 
                      MaterialTheme.colorScheme.background.red < 0.5f

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Main content area - conditional layout
        if (showGreeting && messages.isEmpty() && streamingMessage == null) {
            // Centered greeting with message box
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Model Initialization Status (Top of screen)
                if (showStatusBubble && modelInitStatus != ModelInitStatus.Ready) {
                    ModelStatusBubble(
                        modelInitStatus = modelInitStatus,
                        isDarkTheme = isDarkTheme,
                        onDismiss = onDismissStatusBubble,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // IREN Logo Icon
                    Image(
                        painter = painterResource(id = R.drawable.iren_logo),
                        contentDescription = "IREN Logo",
                        modifier = Modifier
                            .size(180.dp)
                            .padding(bottom = 24.dp)
                    )
                    
                    // "WELCOME TO" text
                    Text(
                        text = "WELCOME TO",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF9CA3AF),
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    // "IREN" text with gradient effect
                    Text(
                        text = "IREN",
                        style = MaterialTheme.typography.displayLarge.copy(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF00E5FF), // Light cyan
                                    Color(0xFF00B8D4)  // Darker cyan
                                )
                            ),
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color(0xFF00BFA5).copy(alpha = 0.5f),
                                offset = androidx.compose.ui.geometry.Offset(0f, 4f),
                                blurRadius = 12f
                            ),
                            fontWeight = FontWeight.Bold,
                            fontSize = 56.sp,
                            letterSpacing = 8.sp
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Error message
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    // Image preview
                    if (selectedImageUri != null) {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .size(100.dp)
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected Image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.shapes.medium
                                    ),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                            
                            IconButton(
                                onClick = onRemoveImage,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-8).dp)
                                    .size(24.dp)
                                    .background(MaterialTheme.colorScheme.error, CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = MaterialTheme.colorScheme.onError,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }
                    
                    // Centered message box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = onPickImage) {
                            Icon(Icons.Default.Add, contentDescription = "Add attachment")
                        }
                        
                        OutlinedTextField(
                            value = userInput,
                            onValueChange = onUserInputChange,
                            placeholder = { Text("How can I help you today?") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 4
                        )
                        
                        if (userInput.isNotBlank() || selectedImageUri != null) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                IconButton(onClick = onSendMessage) {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                                }
                            }
                        } else {
                            // Show mic or stop icon based on voice input state
                            if (isVoiceInputActive) {
                                IconButton(onClick = onStopVoiceInput) {
                                    Icon(Icons.Default.Close, contentDescription = "Stop voice input")
                                }
                            } else {
                                IconButton(onClick = onVoiceInputClick) {
                                    Icon(Icons.Default.Mic, contentDescription = "Voice input")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Normal chat layout
            Column(modifier = Modifier.fillMaxSize()) {
                // Messages LazyColumn with improved keyboard handling
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = true,
                    reverseLayout = false
                ) {
                    // Model initialization status bubble removed from here as per user request

                    
                    items(messages) { message ->
                        MessageBubble(
                            message = message,
                            onImageClick = onImageClick,
                            showRetry = message.role == "user" && message.id == lastFailedMessageId,
                            onRetry = { onRetryMessage(message) }
                        )
                    }
                    
                    // Show streaming message
                    if (isStreaming && streamingMessage != null) {
                        item {
                            MessageBubble(
                                message = Message(
                                    id = "streaming",
                                    session_id = "",
                                    role = "model",
                                    content = streamingMessage,
                                    timestamp = System.currentTimeMillis()
                                ),
                                onImageClick = onImageClick,
                                isStreaming = true
                            )
                        }
                    }
                }
                
                // Input area at bottom
                Surface(
                    tonalElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        
                        if (selectedImageUri != null) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .size(100.dp)
                            ) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Selected Image",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.shapes.medium
                                        ),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                                
                                IconButton(
                                    onClick = onRemoveImage,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 8.dp, y = (-8).dp)
                                        .size(24.dp)
                                        .background(MaterialTheme.colorScheme.error, CircleShape)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.onError,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            
                            // Attachment button (left)
                            IconButton(
                                onClick = onPickImage,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add attachment",
                                    tint = if (isDarkTheme) Color.White else Color.Black
                                )
                            }
                            
                            // Input field with modern rounded design
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = 1.dp,
                                        color = if (isDarkTheme) Color(0xFF3A3A3A) else Color(0xFFDADADA),
                                        shape = RoundedCornerShape(24.dp)
                                    ),
                                shape = RoundedCornerShape(24.dp),
                                color = if (isDarkTheme) Color(0xFF1C1C1C) else Color(0xFFFFFFFF),
                                tonalElevation = 0.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BasicTextField(
                                        value = userInput,
                                        onValueChange = onUserInputChange,
                                        modifier = Modifier.weight(1f),
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF000000)
                                        ),
                                        maxLines = 4,
                                        decorationBox = { innerTextField ->
                                            Box(
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            ) {
                                                if (userInput.isEmpty()) {
                                                    Text(
                                                        text = "Ask me anything...",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = if (isDarkTheme) Color(0xFFA8A8A8) else Color(0xFF7A7A7A)
                                                    )
                                                }
                                                innerTextField()
                                            }
                                        }
                                    )
                                    
                                    // Voice input button inside text field
                                    if (userInput.isBlank() && selectedImageUri == null) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = onVoiceInputClick,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Mic,
                                                contentDescription = "Voice input",
                                                tint = if (isDarkTheme) Color.White else Color.Black,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // Send button (circular, right) - show send or stop based on state
                            if (isStreaming) {
                                // Stop button - red square in grey circle
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = CircleShape,
                                    color = Color(0xFF6B7280) // Grey circle
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(16.dp),
                                            shape = RoundedCornerShape(2.dp), // Small rounding for smoother square
                                            color = Color(0xFFEF4444) // Red square
                                        ) {
                                            IconButton(
                                                onClick = onStopResponse,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                // Empty - the red square is the visual
                                            }
                                        }
                                    }
                                }
                            } else if (userInput.isNotBlank() || selectedImageUri != null) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(40.dp),
                                        strokeWidth = 3.dp,
                                        color = Color(0xFF00BFA5)
                                    )
                                } else {
                                    Surface(
                                        modifier = Modifier.size(40.dp),
                                        shape = CircleShape,
                                        color = Color(0xFF00BFA5) // Teal Send Button
                                    ) {
                                        IconButton(onClick = onSendMessage) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.Send,
                                                contentDescription = "Send",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    onImageClick: (String) -> Unit = {},
    isStreaming: Boolean = false,
    showRetry: Boolean = false,
    onRetry: () -> Unit = {}
) {
    val isUser = message.role == "user"
    
    // Use MaterialTheme to detect actual app theme
    val isDarkTheme = MaterialTheme.colorScheme.background != Color.White && 
                      MaterialTheme.colorScheme.background.red < 0.5f
    
    // Theme-aware colors per specification
    val userBubbleColor = if (isDarkTheme) Color(0xFF2B2B2B) else Color(0xFFE6E6E6)
    val botBubbleColor = if (isDarkTheme) Color(0xFF333333) else Color(0xFFFFFFFF)
    val textColor = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF000000)
    
    val bubbleColor = if (isUser) userBubbleColor else botBubbleColor
    
    var showMenu by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // Retry menu for user messages (left side)
        if (isUser && showRetry) {
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Retry",
                        tint = Color(0xFFB0B3C6),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Retry") },
                        onClick = {
                            showMenu = false
                            onRetry()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(4.dp))
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = bubbleColor
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isDarkTheme && isUser) 1.dp else 0.dp
            ),
            modifier = Modifier
                .widthIn(max = 300.dp)
                .then(
                    // Add border for bot messages in light theme
                    if (!isDarkTheme && !isUser) {
                        Modifier.border(
                            width = 1.dp,
                            color = Color(0xFFD9D9D9),
                            shape = RoundedCornerShape(20.dp)
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Display image if it exists
                message.image_path?.let { imagePath ->
                    AsyncImage(
                        model = imagePath,
                        contentDescription = "Message image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .padding(bottom = 8.dp)
                            .clickable { onImageClick(imagePath) },
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
                
                // Text Content
                if (message.content.isNotEmpty()) {
                    SelectionContainer {
                        Column {
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 22.sp
                                ),
                                color = textColor
                            )
                            
                            // Streaming cursor
                            if (isStreaming) {
                                val infiniteTransition = rememberInfiniteTransition(label = "cursor")
                                val alpha by infiniteTransition.animateFloat(
                                    initialValue = 0f,
                                    targetValue = 1f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(500),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "alpha"
                                )
                                Text(
                                    text = "▋",
                                    color = if (isDarkTheme) Color.White else Color.Black,
                                    modifier = Modifier.alpha(alpha)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Model initialization status states
sealed class ModelInitStatus {
    object Checking : ModelInitStatus()
    object Initializing : ModelInitStatus()
    object Ready : ModelInitStatus()
    object NotDownloaded : ModelInitStatus()
    data class Error(val message: String) : ModelInitStatus()
}

@Composable
fun ModelStatusBubble(
    modelInitStatus: ModelInitStatus,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when (modelInitStatus) {
                is ModelInitStatus.Initializing -> if (isDarkTheme) Color(0xFF1E3A5F) else Color(0xFFE3F2FD)
                is ModelInitStatus.Error -> if (isDarkTheme) Color(0xFF5F1E1E) else Color(0xFFFFEBEE)
                else -> if (isDarkTheme) Color(0xFF2A2B36) else Color(0xFFF5F5F5)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (modelInitStatus) {
                is ModelInitStatus.Initializing -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF1976D2)
                    )
                    Text(
                        "Initializing model...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkTheme) Color.White else Color(0xFF1F2937)
                    )
                }
                is ModelInitStatus.Error -> {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (isDarkTheme) Color(0xFFEF5350) else Color(0xFFD32F2F)
                    )
                    Text(
                        "Init failed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkTheme) Color.White else Color(0xFF1F2937)
                    )
                }
                else -> {}
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Dismiss",
                    modifier = Modifier.size(18.dp),
                    tint = if (isDarkTheme) Color.Gray else Color(0xFF6B7280)
                )
            }
        }
    }
}

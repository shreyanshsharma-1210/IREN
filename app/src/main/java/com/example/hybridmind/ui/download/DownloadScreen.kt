package com.example.hybridmind.ui.download

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hybridmind.data.DownloadProgress
import com.example.hybridmind.data.DownloadStatus
import com.example.hybridmind.data.ModelDownloader
import com.example.hybridmind.core.NetworkMonitor
import kotlinx.coroutines.launch
import androidx.work.WorkManager
import androidx.work.WorkInfo
import androidx.work.await
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    modelDownloader: ModelDownloader,
    networkMonitor: NetworkMonitor,
    onDownloadComplete: (String) -> Unit,
    onSkip: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedModel by remember { mutableStateOf<String?>(null) }
    var downloadProgress by remember { mutableStateOf<DownloadProgress?>(null) }
    var currentWorkId by remember { mutableStateOf<UUID?>(null) }
    var currentModelName by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val isOnline by networkMonitor.isOnline.collectAsState(initial = true)

    LaunchedEffect(Unit) {
        // Check if model was previously downloaded
        if (modelDownloader.isModelDownloaded("gemma-2b", "litertlm")) {
            onDownloadComplete(modelDownloader.getModelPath("gemma-2b", "litertlm"))
            return@LaunchedEffect
        }
        
        if (modelDownloader.isModelDownloaded("gemma-4b", "litertlm")) {
            onDownloadComplete(modelDownloader.getModelPath("gemma-4b", "litertlm"))
            return@LaunchedEffect
        }
        
        // Check for active downloads when screen opens
        scope.launch {
            val workManager = WorkManager.getInstance(context)
            
            // Try to reconnect to gemma-2b download
            try {
                val gemma2bInfo = workManager.getWorkInfosForUniqueWork("download_gemma-2b").await()
                if (gemma2bInfo.isNotEmpty() && gemma2bInfo[0].state.isFinished == false) {
                    currentModelName = "gemma-2b"
                    selectedModel = "gemma-2b"
                    currentWorkId = gemma2bInfo[0].id
                    // Reconnect to existing download
                    modelDownloader.observeDownloadProgress(gemma2bInfo[0].id).collect { progress ->
                        downloadProgress = progress
                        if (progress.status == DownloadStatus.COMPLETED) {
                            onDownloadComplete(modelDownloader.getModelPath("gemma-2b", "litertlm"))
                        }
                    }
                    return@launch
                }
            } catch (e: Exception) {
                android.util.Log.e("DownloadScreen", "Error reconnecting to gemma-2b: ${e.message}")
            }
            
            // Try to reconnect to gemma-4b download
            try {
                val gemma4bInfo = workManager.getWorkInfosForUniqueWork("download_gemma-4b").await()
                if (gemma4bInfo.isNotEmpty() && gemma4bInfo[0].state.isFinished == false) {
                    currentModelName = "gemma-4b"
                    selectedModel = "gemma-4b"
                    currentWorkId = gemma4bInfo[0].id
                    // Reconnect to existing download
                    modelDownloader.observeDownloadProgress(gemma4bInfo[0].id).collect { progress ->
                        downloadProgress = progress
                        if (progress.status == DownloadStatus.COMPLETED) {
                            onDownloadComplete(modelDownloader.getModelPath("gemma-4b", "litertlm"))
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("DownloadScreen", "Error reconnecting to gemma-4b: ${e.message}")
            }
        }
        
        // No model found - show download screen
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { Text("Download AI Model") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Download Intelligence",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Select an AI model to download for offline use",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Standard Model - Multimodal
                ModelOptionCard(
                    title = "Standard (Gemma 3n E2B)",
                    description = "Multimodal AI with image support. ~2GB download",
                    enabled = downloadProgress == null,
                    selected = selectedModel == "gemma-2b",
                    onClick = { selectedModel = "gemma-2b" }
                )

                // Advanced Model
                ModelOptionCard(
                    title = "Advanced (Gemma 3n E4B)",
                    description = "Better quality, multimodal. ~4GB download",
                    enabled = downloadProgress == null,
                    selected = selectedModel == "gemma-4b",
                    onClick = { selectedModel = "gemma-4b" }
                )

                // Progress indicator
                if (downloadProgress != null) {
                    when (downloadProgress!!.status) {
                        DownloadStatus.DOWNLOADING -> {
                            LinearProgressIndicator(
                                progress = { downloadProgress!!.progress / 100f },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "${downloadProgress!!.progress}% - ${formatBytes(downloadProgress!!.downloadedBytes)} / ${formatBytes(downloadProgress!!.totalBytes)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Download continues even if you close the app",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        DownloadStatus.COMPLETED -> {
                            Text(
                                text = "Download completed! Initializing...",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        DownloadStatus.FAILED -> {
                            Text(
                                text = "❌ Download failed: ${downloadProgress!!.errorMessage ?: "Unknown error"}",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Please try again",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DownloadStatus.CANCELLED -> {
                            // Reset to allow new selection
                            LaunchedEffect(Unit) {
                                downloadProgress = null
                            }
                        }
                        else -> {}
                    }
                }

                // Download/Retry Button
                if (downloadProgress?.status == DownloadStatus.FAILED) {
                    // Show Try Again button when download fails
                    Button(
                        onClick = {
                            // Check network before retry
                            if (!isOnline) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "⚠️ No network connection. Please connect to WiFi or mobile data.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                                return@Button
                            }
                            
                            selectedModel?.let { model ->
                                currentModelName = model
                                scope.launch {
                                    val url = when (model) {
                                        "gemma-2b" -> "https://huggingface.co/Ph03nix1210/HybridMind-Assets/resolve/main/gemma-3n-E2B-it-int4.litertlm"
                                        "gemma-4b" -> "https://huggingface.co/Ph03nix1210/HybridMind-Assets/resolve/main/gemma-3n-E4B-it-int4.litertlm"
                                        else -> return@launch
                                    }
                                    
                                    // Reset progress to null so Download button becomes enabled if retry fails
                                    downloadProgress = null
                                    
                                    // Start new download
                                    val workId = modelDownloader.startDownload(url, model, "litertlm")
                                    currentWorkId = workId
                                    
                                    // Observe progress
                                    modelDownloader.observeDownloadProgress(workId).collect { progress ->
                                        downloadProgress = progress
                                        if (progress.status == DownloadStatus.COMPLETED) {
                                            onDownloadComplete(modelDownloader.getModelPath(model, "litertlm"))
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Try Again")
                    }
                } else {
                    // Show Download button when no download is in progress
                    Button(
                        onClick = {
                            selectedModel?.let { model ->
                                currentModelName = model
                                scope.launch {
                                    val url = when (model) {
                                        "gemma-2b" -> "https://huggingface.co/Ph03nix1210/HybridMind-Assets/resolve/main/gemma-3n-E2B-it-int4.litertlm"
                                        "gemma-4b" -> "https://huggingface.co/Ph03nix1210/HybridMind-Assets/resolve/main/gemma-3n-E4B-it-int4.litertlm"
                                        else -> return@launch
                                    }
                                    
                                    val workId = modelDownloader.startDownload(url, model, "litertlm")
                                    currentWorkId = workId
                                    
                                    // Observe progress
                                    modelDownloader.observeDownloadProgress(workId).collect { progress ->
                                        downloadProgress = progress
                                        if (progress.status == DownloadStatus.COMPLETED) {
                                            // Main model downloaded - proceed with initialization
                                            onDownloadComplete(modelDownloader.getModelPath(model, "litertlm"))
                                            
                                            // Download Vision Model in background (non-blocking)
                                            scope.launch {
                                                try {
                                                    val visionUrl = "https://storage.googleapis.com/mediapipe-models/image_classifier/efficientnet_lite0/float32/1/efficientnet_lite0.tflite"
                                                    val visionWorkId = modelDownloader.startDownload(visionUrl, "efficientnet_lite0", "tflite")
                                                    modelDownloader.observeDownloadProgress(visionWorkId).collect { visionProgress ->
                                                        if (visionProgress.status == DownloadStatus.COMPLETED) {
                                                            android.util.Log.d("DownloadScreen", "Vision model downloaded successfully")
                                                        } else if (visionProgress.status == DownloadStatus.FAILED) {
                                                            android.util.Log.w("DownloadScreen", "Vision model download failed - image features may be limited")
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    android.util.Log.e("DownloadScreen", "Vision model download error: ${e.message}")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedModel != null && downloadProgress == null
                    ) {
                        Text("Download")
                    }
                }

                // Cancel button (only show during download)
                if (downloadProgress?.status == DownloadStatus.DOWNLOADING) {
                    OutlinedButton(
                        onClick = {
                            currentWorkId?.let { modelDownloader.cancelDownload(it) }
                            downloadProgress = null
                            currentWorkId = null
                            selectedModel = null  // Reset selection to allow fresh start
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel Download")
                    }
                }
                
                // Skip button (only show when no download is in progress)
                if (downloadProgress == null || downloadProgress?.status == DownloadStatus.FAILED) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onSkip,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Skip for now",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "You can download the model later from Settings",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelOptionCard(
    title: String,
    description: String,
    enabled: Boolean,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = { if (enabled) onClick() },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        enabled = enabled
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
        }
    }
}

fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024))
    }
}

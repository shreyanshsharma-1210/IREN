package com.example.hybridmind.data

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.*
import com.example.hybridmind.workers.DownloadWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.UUID

data class DownloadProgress(
    val status: DownloadStatus,
    val progress: Int = 0,
    val downloadedBytes: Long = 0,
    val totalBytes: Long = 0,
    val errorMessage: String? = null
)

enum class DownloadStatus {
    IDLE,
    DOWNLOADING,
    COMPLETED,
    FAILED,
    CANCELLED
}

class ModelDownloader(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)
    private val prefs = context.getSharedPreferences("model_prefs", Context.MODE_PRIVATE)
    
    /**
     * Start downloading a model in the background using WorkManager.
     * Returns the Work ID that can be used to observe progress.
     */
    fun startDownload(modelUrl: String, modelName: String, extension: String = "litertlm"): UUID {
        val inputData = workDataOf(
            DownloadWorker.KEY_MODEL_URL to modelUrl,
            DownloadWorker.KEY_MODEL_NAME to modelName,
            DownloadWorker.KEY_EXTENSION to extension
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .build()

        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                java.util.concurrent.TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            "download_$modelName",
            ExistingWorkPolicy.KEEP, // Keep existing download if already running
            downloadRequest
        )

        Log.d("ModelDownloader", "Download started for $modelName with ID: ${downloadRequest.id}")
        return downloadRequest.id
    }

    /**
     * Observe download progress for a given Work ID.
     * Returns a Flow of DownloadProgress that updates as the download progresses.
     */
    fun observeDownloadProgress(workId: UUID): Flow<DownloadProgress> = flow {
        val workInfo = workManager.getWorkInfoById(workId)
        
        // Emit initial state based on work info
        when (workInfo.await()?.state) {
            WorkInfo.State.ENQUEUED -> emit(DownloadProgress(DownloadStatus.IDLE))
            WorkInfo.State.RUNNING -> {
                val progress = workInfo.await()?.progress
                emit(DownloadProgress(
                    status = DownloadStatus.DOWNLOADING,
                    progress = progress?.getInt(DownloadWorker.KEY_PROGRESS, 0) ?: 0,
                    downloadedBytes = progress?.getLong(DownloadWorker.KEY_DOWNLOADED_BYTES, 0L) ?: 0L,
                    totalBytes = progress?.getLong(DownloadWorker.KEY_TOTAL_BYTES, 0L) ?: 0L
                ))
            }
            WorkInfo.State.SUCCEEDED -> emit(DownloadProgress(DownloadStatus.COMPLETED, 100))
            WorkInfo.State.FAILED -> {
                val errorMsg = workInfo.await()?.outputData?.getString(DownloadWorker.KEY_ERROR_MESSAGE)
                emit(DownloadProgress(DownloadStatus.FAILED, errorMessage = errorMsg))
            }
            WorkInfo.State.CANCELLED -> emit(DownloadProgress(DownloadStatus.CANCELLED))
            else -> emit(DownloadProgress(DownloadStatus.IDLE))
        }

        // Continue observing for updates
        workManager.getWorkInfoByIdFlow(workId).collect { info ->
            // Handle null WorkInfo (can happen when work is removed/completed)
            if (info == null) return@collect
            
            when (info.state) {
                WorkInfo.State.ENQUEUED -> emit(DownloadProgress(DownloadStatus.IDLE))
                WorkInfo.State.RUNNING -> {
                    val progress = info.progress
                    emit(DownloadProgress(
                        status = DownloadStatus.DOWNLOADING,
                        progress = progress.getInt(DownloadWorker.KEY_PROGRESS, 0),
                        downloadedBytes = progress.getLong(DownloadWorker.KEY_DOWNLOADED_BYTES, 0L),
                        totalBytes = progress.getLong(DownloadWorker.KEY_TOTAL_BYTES, 0L)
                    ))
                }
                WorkInfo.State.SUCCEEDED -> emit(DownloadProgress(DownloadStatus.COMPLETED, 100))
                WorkInfo.State.FAILED -> {
                    val errorMsg = info.outputData.getString(DownloadWorker.KEY_ERROR_MESSAGE)
                    emit(DownloadProgress(DownloadStatus.FAILED, errorMessage = errorMsg))
                }
                WorkInfo.State.CANCELLED -> emit(DownloadProgress(DownloadStatus.CANCELLED))
                else -> {}
            }
        }
    }

    /**
     * Cancel an ongoing download.
     */
    fun cancelDownload(workId: UUID) {
        workManager.cancelWorkById(workId)
        Log.d("ModelDownloader", "Download cancelled: $workId")
    }

    /**
     * Cancel download by model name.
     */
    fun cancelDownloadByName(modelName: String) {
        workManager.cancelUniqueWork("download_$modelName")
        Log.d("ModelDownloader", "Download cancelled: $modelName")
    }

    /**
     * Get the file path for a downloaded model.
     */
    fun getModelPath(modelName: String, extension: String = "litertlm"): String {
        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "$modelName.$extension"
        ).absolutePath
    }

    /**
     * Check if a model has been fully downloaded.
     */
    fun isModelDownloaded(modelName: String, extension: String = "litertlm"): Boolean {
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "$modelName.$extension"
        )
        val marker = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "$modelName.$extension.complete"
        )
        val threshold = if (extension == "tflite") 1024 * 1024 * 2 else 1024 * 1024 * 10
        return file.exists() && file.length() > threshold && marker.exists()
    }

    /**
     * Save the user's selected model preference.
     */
    fun saveSelectedModel(modelName: String) {
        prefs.edit().putString("selected_model", modelName).apply()
        Log.d("ModelDownloader", "Saved model preference: $modelName")
    }

    /**
     * Get the user's last selected model.
     */
    fun getSelectedModel(): String? {
        return prefs.getString("selected_model", null)
    }

    /**
     * Get the best available model - prefers user's last selection,
     * falls back to first available model.
     */
    fun getAvailableModel(): String? {
        // First check user's last selection
        val selectedModel = getSelectedModel()
        if (selectedModel != null && isModelDownloaded(selectedModel, "litertlm")) {
            Log.d("ModelDownloader", "Using user's preferred model: $selectedModel")
            return selectedModel
        }
        
        // Fallback to first available model
        return when {
            isModelDownloaded("gemma-2b", "litertlm") -> {
                Log.d("ModelDownloader", "Using fallback model: gemma-2b")
                "gemma-2b"
            }
            isModelDownloaded("gemma-4b", "litertlm") -> {
                Log.d("ModelDownloader", "Using fallback model: gemma-4b")
                "gemma-4b"
            }
            else -> {
                Log.d("ModelDownloader", "No model available")
                null
            }
        }
    }
}

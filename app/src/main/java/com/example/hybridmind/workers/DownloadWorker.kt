package com.example.hybridmind.workers

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.TimeUnit

class DownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .protocols(listOf(okhttp3.Protocol.HTTP_1_1))
        .build()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val modelUrl = inputData.getString(KEY_MODEL_URL) ?: return@withContext Result.failure()
            val modelName = inputData.getString(KEY_MODEL_NAME) ?: return@withContext Result.failure()
            val extension = inputData.getString(KEY_EXTENSION) ?: "litertlm"

            val destination = File(
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "$modelName.$extension"
            )
            val marker = File(
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "$modelName.$extension.complete"
            )

            // Check if already downloaded
            if (destination.exists() && marker.exists() && destination.length() > 0) {
                Log.d(TAG, "Model already downloaded")
                return@withContext Result.success()
            }

            // Set initial progress
            setProgress(workDataOf(
                KEY_PROGRESS to 0,
                KEY_DOWNLOADED_BYTES to 0L,
                KEY_TOTAL_BYTES to 0L
            ))

            // Get file size
            val headRequest = Request.Builder().url(modelUrl).head().build()
            val headResponse = client.newCall(headRequest).execute()
            val totalBytes = headResponse.header("Content-Length")?.toLong() ?: -1L
            headResponse.close()

            if (totalBytes <= 0L) {
                Log.e(TAG, "Invalid content length")
                return@withContext Result.failure(
                    workDataOf(KEY_ERROR_MESSAGE to "Unable to determine file size")
                )
            }

            // Delete existing partial files
            if (destination.exists()) destination.delete()
            if (marker.exists()) marker.delete()
            destination.createNewFile()

            // Pre-allocate file space
            RandomAccessFile(destination, "rw").use { it.setLength(totalBytes) }

            // Download file
            val request = Request.Builder().url(modelUrl).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e(TAG, "Download request failed: ${response.code}")
                return@withContext Result.failure(
                    workDataOf(KEY_ERROR_MESSAGE to "Download failed: HTTP ${response.code}")
                )
            }

            val source = response.body?.byteStream() ?: return@withContext Result.failure(
                workDataOf(KEY_ERROR_MESSAGE to "No response body")
            )

            RandomAccessFile(destination, "rw").use { raf ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var downloadedBytes = 0L
                var lastProgressUpdate = System.currentTimeMillis()

                while (source.read(buffer).also { bytesRead = it } != -1) {
                    if (isStopped) {
                        Log.d(TAG, "Download cancelled")
                        source.close()
                        return@withContext Result.failure(
                            workDataOf(KEY_ERROR_MESSAGE to "Download cancelled")
                        )
                    }

                    raf.write(buffer, 0, bytesRead)
                    downloadedBytes += bytesRead

                    // Update progress every 500ms to avoid excessive updates
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastProgressUpdate > 500) {
                        val progress = ((downloadedBytes * 100) / totalBytes).toInt()
                        setProgress(workDataOf(
                            KEY_PROGRESS to progress,
                            KEY_DOWNLOADED_BYTES to downloadedBytes,
                            KEY_TOTAL_BYTES to totalBytes
                        ))
                        lastProgressUpdate = currentTime
                    }
                }

                source.close()
            }

            response.close()

            // Mark as complete
            marker.createNewFile()
            
            // Set final progress
            setProgress(workDataOf(
                KEY_PROGRESS to 100,
                KEY_DOWNLOADED_BYTES to totalBytes,
                KEY_TOTAL_BYTES to totalBytes
            ))

            Log.d(TAG, "Download completed successfully")
            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Download failed", e)
            Result.failure(
                workDataOf(KEY_ERROR_MESSAGE to (e.message ?: "Unknown error"))
            )
        }
    }

    companion object {
        private const val TAG = "DownloadWorker"
        
        const val KEY_MODEL_URL = "model_url"
        const val KEY_MODEL_NAME = "model_name"
        const val KEY_EXTENSION = "extension"
        const val KEY_PROGRESS = "progress"
        const val KEY_DOWNLOADED_BYTES = "downloaded_bytes"
        const val KEY_TOTAL_BYTES = "total_bytes"
        const val KEY_ERROR_MESSAGE = "error_message"
    }
}

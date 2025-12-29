package com.example.hybridmind.data

import android.content.Context
import androidx.room.Room
import com.example.hybridmind.BuildConfig
import com.example.hybridmind.core.NetworkMonitor
import com.example.hybridmind.data.local.AppDatabase

/**
 * Singleton provider for ChatRepository to ensure:
 * 1. Single instance across app lifecycle
 * 2. Model stays initialized even if activity recreates
 * 3. Better resource management
 */
object RepositoryProvider {
    @Volatile
    private var chatRepository: ChatRepository? = null
    
    @Volatile
    private var database: AppDatabase? = null
    
    @Volatile
    private var networkMonitor: NetworkMonitor? = null
    
    fun getChatRepository(context: Context): ChatRepository {
        return chatRepository ?: synchronized(this) {
            chatRepository ?: createChatRepository(context).also { 
                chatRepository = it
                android.util.Log.d("RepositoryProvider", "✓ ChatRepository singleton created")
            }
        }
    }
    
    fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "hybridmind_database"
            ).fallbackToDestructiveMigration().build().also {
                database = it
                android.util.Log.d("RepositoryProvider", "✓ Database singleton created")
            }
        }
    }
    
    fun getNetworkMonitor(context: Context): NetworkMonitor {
        return networkMonitor ?: synchronized(this) {
            networkMonitor ?: NetworkMonitor(context.applicationContext).also {
                networkMonitor = it
                android.util.Log.d("RepositoryProvider", "✓ NetworkMonitor singleton created")
            }
        }
    }
    
    private fun createChatRepository(context: Context): ChatRepository {
        val appContext = context.applicationContext
        val db = getDatabase(appContext)
        val netMonitor = getNetworkMonitor(appContext)
        val geminiApiKey = BuildConfig.GEMINI_API_KEY
        
        return ChatRepository(
            context = appContext,
            networkMonitor = netMonitor,
            database = db,
            geminiApiKey = geminiApiKey
        )
    }
    
    /**
     * Clear all singletons (useful for testing or forced resets)
     */
    fun reset() {
        synchronized(this) {
            chatRepository?.cleanup()
            chatRepository = null
            database = null
            networkMonitor = null
            android.util.Log.d("RepositoryProvider", "All singletons reset")
        }
    }
}

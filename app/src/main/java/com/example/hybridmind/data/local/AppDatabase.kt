package com.example.hybridmind.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import androidx.room.RoomDatabase

@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey val id: String,
    val user_id: String, // Added for user isolation
    val title: String,
    val is_offline_only: Boolean,
    val last_updated: Long
)

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatSession::class,
            parentColumns = ["id"],
            childColumns = ["session_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index(value = ["session_id"])]
)
data class Message(
    @PrimaryKey val id: String,
    val session_id: String,
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long,
    val image_path: String? = null
)

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_sessions WHERE user_id = :userId ORDER BY last_updated DESC")
    suspend fun getAllSessions(userId: String): List<ChatSession>

    @Query("DELETE FROM chat_sessions WHERE user_id = :userId")
    suspend fun deleteAllSessions(userId: String)
    
    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("SELECT * FROM messages WHERE session_id = :sessionId ORDER BY timestamp ASC")
    suspend fun getMessagesForSession(sessionId: String): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSession)

    @Update
    suspend fun updateSession(session: ChatSession)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)
    
    @Query("DELETE FROM messages WHERE session_id IN (SELECT id FROM chat_sessions WHERE is_offline_only = 1) AND timestamp < :threshold")
    suspend fun pruneOfflineMessages(threshold: Long)
}

@Database(entities = [ChatSession::class, Message::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}

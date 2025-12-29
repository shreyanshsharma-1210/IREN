package com.example.hybridmind.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to create DataStore instance
private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

class ThemePreference(private val context: Context) {
    
    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }
    
    /**
     * Save the selected theme mode
     */
    suspend fun saveThemeMode(mode: ThemeMode) {
        context.themeDataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }
    
    /**
     * Observe the current theme mode as a Flow
     * Defaults to SYSTEM if not set
     */
    fun observeThemeMode(): Flow<ThemeMode> {
        return context.themeDataStore.data.map { preferences ->
            val modeName = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
            try {
                ThemeMode.valueOf(modeName)
            } catch (e: IllegalArgumentException) {
                ThemeMode.SYSTEM // Fallback to system if invalid value
            }
        }
    }
}

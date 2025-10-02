package de.frinshy.plink.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Top-level singleton DataStore delegate to ensure only one DataStore instance
 * is created per Context/file. This prevents the IllegalStateException when
 * multiple repositories or callers accidentally create their own instances.
 */
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_preferences")

/**
 * Simple repository for app-level settings persisted via DataStore Preferences.
 */
class SettingsRepository(private val context: Context) {

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")

        // New key to store ThemeMode as int (ordinal of ThemeMode)
        private val THEME_MODE_KEY = intPreferencesKey("theme_mode")
    }

    /**
     * Flow that emits the currently selected ThemeMode. If an older boolean preference
     * exists we migrate it on-the-fly: true -> DARK, false -> LIGHT. Default is SYSTEM.
     */
    val themeMode = context.settingsDataStore.data.map { prefs ->
        when {
            prefs.contains(THEME_MODE_KEY) -> {
                val ordinal = prefs[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.ordinal
                ThemeMode.values().getOrElse(ordinal) { ThemeMode.SYSTEM }
            }
            // Backwards compatibility: keep reading old boolean key if present
            prefs.contains(DARK_MODE_KEY) -> {
                val dark = prefs[DARK_MODE_KEY] ?: false
                if (dark) ThemeMode.DARK else ThemeMode.LIGHT
            }

            else -> ThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = mode.ordinal
        }
    }

    // Convenience methods to maintain compatibility with existing callers
    val isDarkTheme: Flow<Boolean> = themeMode.map { mode ->
        when (mode) {
            ThemeMode.SYSTEM -> false // callers who expect isDarkTheme should handle system separately
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
        }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        // Map boolean to explicit light/dark
        setThemeMode(if (enabled) ThemeMode.DARK else ThemeMode.LIGHT)
    }

    suspend fun toggleDarkTheme() {
        // Toggle between LIGHT and DARK; if SYSTEM, switch to DARK
        context.settingsDataStore.edit { prefs ->
            val current = when {
                prefs.contains(THEME_MODE_KEY) -> {
                    ThemeMode.values().getOrElse(prefs[THEME_MODE_KEY] ?: 0) { ThemeMode.SYSTEM }
                }

                prefs.contains(DARK_MODE_KEY) -> {
                    if (prefs[DARK_MODE_KEY] == true) ThemeMode.DARK else ThemeMode.LIGHT
                }

                else -> ThemeMode.SYSTEM
            }
            val next = when (current) {
                ThemeMode.LIGHT -> ThemeMode.DARK
                ThemeMode.DARK -> ThemeMode.LIGHT
                ThemeMode.SYSTEM -> ThemeMode.DARK
            }
            prefs[THEME_MODE_KEY] = next.ordinal
        }
    }
}

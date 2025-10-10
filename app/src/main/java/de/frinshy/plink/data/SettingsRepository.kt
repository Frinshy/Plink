package de.frinshy.plink.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map


private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_preferences")


class SettingsRepository(private val context: Context) {

    companion object {

        private val THEME_MODE_KEY = intPreferencesKey("theme_mode")


        private val DEBUG_MENU_ENABLED_KEY = booleanPreferencesKey("debug_menu_enabled")
    }


    val themeMode = context.settingsDataStore.data.map { prefs ->
        when {
            prefs.contains(THEME_MODE_KEY) -> {
                val ordinal = prefs[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.ordinal
                ThemeMode.entries.toTypedArray().getOrElse(ordinal) { ThemeMode.SYSTEM }
            }

            else -> ThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = mode.ordinal
        }
    }


    val isDebugMenuEnabled = context.settingsDataStore.data.map { prefs ->
        prefs[DEBUG_MENU_ENABLED_KEY] ?: false
    }


    suspend fun toggleDebugMenu() {
        context.settingsDataStore.edit { prefs ->
            val current = prefs[DEBUG_MENU_ENABLED_KEY] ?: false
            prefs[DEBUG_MENU_ENABLED_KEY] = !current
        }
    }


}

package de.frinshy.plink.data

/** GameRepository: DataStore-backed persistence for game state. */

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Repository for persisting game data using DataStore Preferences. */
class GameRepository(private val context: Context) {

    companion object {
        private val COINS_KEY = longPreferencesKey("coins")
        private val COINS_PER_TAP_KEY = intPreferencesKey("coins_per_tap")

        // Generic keys for upgrades; stored individually to avoid JSON in preferences.
        private fun upgradeKeyFor(id: String) = intPreferencesKey("upgrade_level_$id")
        private val TOTAL_COINS_EARNED_KEY = longPreferencesKey("total_coins_earned")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_preferences")

    /** Flow of the current merged [GameState] derived from DataStore. */
    val gameState: Flow<GameState> = context.dataStore.data.map { preferences ->
        val upgrades = mutableMapOf<String, Int>()
        // Read upgrade levels via generic keys only
        upgrades["tap_upgrade"] = preferences[upgradeKeyFor("tap_upgrade")] ?: 0
        upgrades["auto_collector"] = preferences[upgradeKeyFor("auto_collector")] ?: 0

        GameState(
            coins = preferences[COINS_KEY] ?: 0L,
            coinsPerTap = preferences[COINS_PER_TAP_KEY] ?: 1,
            upgradeLevels = upgrades.toMap(),
            totalCoinsEarned = preferences[TOTAL_COINS_EARNED_KEY] ?: 0L
        )
    }

    /** Add `amount` to the stored coin balance and total earned. */
    suspend fun addCoins(amount: Long) {
        context.dataStore.edit { preferences ->
            val currentCoins = preferences[COINS_KEY] ?: 0L
            val currentTotal = preferences[TOTAL_COINS_EARNED_KEY] ?: 0L
            preferences[COINS_KEY] = currentCoins + amount
            preferences[TOTAL_COINS_EARNED_KEY] = currentTotal + amount
        }
    }

    /**
     * Debug function: Add coins without affecting total earned (for development)
     */
    suspend fun debugAddCoins(amount: Long) {
        context.dataStore.edit { preferences ->
            val currentCoins = preferences[COINS_KEY] ?: 0L
            preferences[COINS_KEY] = currentCoins + amount
        }
    }

    /**
     * Debug function: Reset all game data
     */
    suspend fun debugResetGame() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Debug function: Set coins to specific amount
     */
    suspend fun debugSetCoins(amount: Long) {
        context.dataStore.edit { preferences ->
            preferences[COINS_KEY] = amount
        }
    }

    /**
     * Spend coins (subtract from total)
     */
    suspend fun spendCoins(amount: Long) {
        context.dataStore.edit { preferences ->
            val currentCoins = preferences[COINS_KEY] ?: 0L
            preferences[COINS_KEY] = maxOf(0L, currentCoins - amount)
        }
    }

    /**
     * Update coins per tap
     */
    suspend fun updateCoinsPerTap(newValue: Int) {
        context.dataStore.edit { preferences ->
            preferences[COINS_PER_TAP_KEY] = newValue
        }
    }

    /**
     * Update number of auto collectors
     */
    suspend fun updateAutoCollectors(newValue: Int) {
        // Persist auto-collector level using generic upgrade key
        updateUpgradeLevel("auto_collector", newValue)
    }

    /**
     * Generic: update an upgrade level by id
     */
    suspend fun updateUpgradeLevel(id: String, newValue: Int) {
        val key = upgradeKeyFor(id)
        context.dataStore.edit { preferences ->
            preferences[key] = newValue
        }
    }

    /**
     * Update tap upgrade level
     */
    suspend fun updateTapUpgradeLevel(newValue: Int) {
        // Persist tap upgrade level using generic upgrade key
        updateUpgradeLevel("tap_upgrade", newValue)
    }

    /**
     * Purchase an upgrade
     */
    suspend fun purchaseUpgrade(upgrade: Upgrade, currentState: GameState) {
        when (upgrade) {
            is Upgrade.TapUpgrade -> {
                val tapLevel = currentState.upgradeLevels["tap_upgrade"] ?: 0
                spendCoins(upgrade.getCurrentPrice(tapLevel))
                updateTapUpgradeLevel(tapLevel + 1)
                updateCoinsPerTap(currentState.coinsPerTap + 1)
            }

            is Upgrade.AutoCollector -> {
                val autoLevel = currentState.upgradeLevels["auto_collector"] ?: 0
                spendCoins(upgrade.getCurrentPrice(autoLevel))
                updateAutoCollectors(autoLevel + 1)
            }
        }
    }
}
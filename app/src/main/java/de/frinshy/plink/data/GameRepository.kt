package de.frinshy.plink.data


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import de.frinshy.plink.widgets.WidgetUpdater
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "game_preferences")

class GameRepository(private val context: Context) {

    companion object {
        private val COINS_KEY = longPreferencesKey("coins")
        private val COINS_PER_TAP_KEY = intPreferencesKey("coins_per_tap")


        private fun upgradeKeyFor(id: String) = intPreferencesKey("upgrade_level_$id")
        private val TOTAL_COINS_EARNED_KEY = longPreferencesKey("total_coins_earned")
    }


    private val dataStore: DataStore<Preferences> by lazy { context.applicationContext.dataStore }


    val gameState: Flow<GameState> = dataStore.data.map { preferences ->
        val upgrades = mutableMapOf<String, Int>()

        upgrades["tap_upgrade"] = preferences[upgradeKeyFor("tap_upgrade")] ?: 0
        upgrades["auto_collector"] = preferences[upgradeKeyFor("auto_collector")] ?: 0

        GameState(
            coins = preferences[COINS_KEY] ?: 0L,
            coinsPerTap = preferences[COINS_PER_TAP_KEY] ?: 1,
            upgradeLevels = upgrades.toMap(),
            totalCoinsEarned = preferences[TOTAL_COINS_EARNED_KEY] ?: 0L
        )
    }


    suspend fun addCoins(amount: Long) {
        dataStore.edit { preferences ->
            val currentCoins = preferences[COINS_KEY] ?: 0L
            val currentTotal = preferences[TOTAL_COINS_EARNED_KEY] ?: 0L
            preferences[COINS_KEY] = currentCoins + amount
            preferences[TOTAL_COINS_EARNED_KEY] = currentTotal + amount
        }



        WidgetUpdater.updateAllCoins(context)
    }


    suspend fun debugAddCoins(amount: Long) {
        dataStore.edit { preferences ->
            val currentCoins = preferences[COINS_KEY] ?: 0L
            preferences[COINS_KEY] = currentCoins + amount
        }
        WidgetUpdater.updateAllCoins(context)
    }


    suspend fun debugResetGame() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
        WidgetUpdater.updateAllCoins(context)
    }


    suspend fun debugSetCoins(amount: Long) {
        dataStore.edit { preferences ->
            preferences[COINS_KEY] = amount
        }
        WidgetUpdater.updateAllCoins(context)
    }


    suspend fun spendCoins(amount: Long) {
        dataStore.edit { preferences ->
            val currentCoins = preferences[COINS_KEY] ?: 0L
            preferences[COINS_KEY] = maxOf(0L, currentCoins - amount)
        }
        WidgetUpdater.updateAllCoins(context)
    }


    suspend fun gamble(wager: Long): Boolean {
        if (wager <= 0L) return false


        var result = false
        dataStore.edit { preferences ->
            val currentCoins = preferences[COINS_KEY] ?: 0L
            if (wager > currentCoins) {

                return@edit
            }


            val won = (kotlin.random.Random.nextBoolean())
            if (won) {

                preferences[COINS_KEY] = currentCoins + wager
                val currentTotal = preferences[TOTAL_COINS_EARNED_KEY] ?: 0L
                preferences[TOTAL_COINS_EARNED_KEY] = currentTotal + wager
            } else {

                preferences[COINS_KEY] = maxOf(0L, currentCoins - wager)
            }
            result = won
        }
        WidgetUpdater.updateAllCoins(context)
        return result
    }

    suspend fun updateCoinsPerTap(newValue: Int) {
        context.dataStore.edit { preferences ->
            preferences[COINS_PER_TAP_KEY] = newValue
        }
        WidgetUpdater.updateAllCoins(context)
    }

    suspend fun updateAutoCollectors(newValue: Int) {
        updateUpgradeLevel("auto_collector", newValue)
        WidgetUpdater.updateAllCoins(context)
    }


    suspend fun updateUpgradeLevel(id: String, newValue: Int) {
        val key = upgradeKeyFor(id)
        context.dataStore.edit { preferences ->
            preferences[key] = newValue
        }
        WidgetUpdater.updateAllCoins(context)
    }

    suspend fun updateTapUpgradeLevel(newValue: Int) {
        updateUpgradeLevel("tap_upgrade", newValue)
    }

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
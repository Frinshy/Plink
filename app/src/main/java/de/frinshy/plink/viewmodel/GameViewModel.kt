package de.frinshy.plink.viewmodel

/** GameViewModel: orchestrates game logic and exposes UI state. */

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.frinshy.plink.data.GameRepository
import de.frinshy.plink.data.GameState
import de.frinshy.plink.data.Upgrade
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** ViewModel that manages game state and business logic. */
class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var autoCollectorJob: Job? = null

    init {
        // Observe persisted game state and update UI state
        viewModelScope.launch {
            repository.gameState.collectLatest { gameState ->
                _uiState.value = _uiState.value.copy(
                    gameState = gameState,
                    isLoading = false
                )
                // Start or stop the auto-collector loop based on stored collector count
                if ((gameState.upgradeLevels["auto_collector"] ?: 0) > 0) {
                    startAutoCollector()
                } else {
                    stopAutoCollector()
                }
            }
        }
    }

    /**
     * Called when the main coin is tapped
     */
    fun onCoinTap() {
        viewModelScope.launch {
            val currentState = _uiState.value.gameState
            repository.addCoins(currentState.coinsPerTap.toLong())

            // Increment animation counter (UI reacts to each increment)
            _uiState.value = _uiState.value.copy(
                tapAnimationCounter = _uiState.value.tapAnimationCounter + 1
            )
        }
    }

    /**
     * Purchase an upgrade
     */
    fun purchaseUpgrade(upgrade: Upgrade) {
        viewModelScope.launch {
            val currentState = _uiState.value.gameState

            when (upgrade) {
                is Upgrade.TapUpgrade -> {
                    val tapLevel = currentState.upgradeLevels["tap_upgrade"] ?: 0
                    if (upgrade.isAffordable(currentState.coins, tapLevel) &&
                        upgrade.isPurchasable(tapLevel)
                    ) {
                        repository.purchaseUpgrade(upgrade, currentState)
                    }
                }

                is Upgrade.AutoCollector -> {
                    val autoLevel = currentState.upgradeLevels["auto_collector"] ?: 0
                    if (upgrade.isAffordable(currentState.coins, autoLevel) &&
                        upgrade.isPurchasable(autoLevel)
                    ) {
                        repository.purchaseUpgrade(upgrade, currentState)
                    }
                }
            }
        }
    }

    /** Start the auto-collector loop which grants coins every second. */
    private fun startAutoCollector() {
        stopAutoCollector()

        autoCollectorJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val currentState = _uiState.value.gameState
                val autoCollectors = currentState.upgradeLevels["auto_collector"] ?: 0
                if (autoCollectors > 0) {
                    repository.addCoins(autoCollectors.toLong())
                }
            }
        }
    }

    /**
     * Stop the auto collector coroutine
     */
    private fun stopAutoCollector() {
        autoCollectorJob?.cancel()
        autoCollectorJob = null
    }

    /**
     * Get current upgrade level for a specific upgrade
     */
    fun getUpgradeLevel(upgrade: Upgrade): Int {
        val currentState = _uiState.value.gameState
        return when (upgrade) {
            is Upgrade.TapUpgrade -> currentState.upgradeLevels["tap_upgrade"] ?: 0
            is Upgrade.AutoCollector -> currentState.upgradeLevels["auto_collector"] ?: 0
        }
    }

    // Debug functions for development

    /**
     * Debug: Add coins without affecting total earned
     */
    fun debugAddCoins(amount: Long) {
        viewModelScope.launch {
            repository.debugAddCoins(amount)
        }
    }

    /**
     * Debug: Set coins to specific amount
     */
    fun debugSetCoins(amount: Long) {
        viewModelScope.launch {
            repository.debugSetCoins(amount)
        }
    }

    /**
     * Debug: Reset all game data
     */
    fun debugResetGame() {
        viewModelScope.launch {
            repository.debugResetGame()
        }
    }

    /**
     * Debug: Max out all upgrades
     */
    fun debugMaxUpgrades() {
        viewModelScope.launch {
            repository.debugSetCoins(999_999_999L)
            repository.updateTapUpgradeLevel(50)
            repository.updateCoinsPerTap(51)
            repository.updateAutoCollectors(25)
        }
    }

    /**
     * Toggle debug mode visibility
     */
    fun toggleDebugMode() {
        _uiState.value = _uiState.value.copy(
            isDebugMode = !_uiState.value.isDebugMode
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoCollector()
    }
}

/**
 * UI state for the game
 */
data class GameUiState(
    val gameState: GameState = GameState(),
    val isLoading: Boolean = true,
    /**
     * Counter that increments on every tap. Using a counter avoids races when taps are fired
     * rapidly — the UI can react to each increment independently.
     */
    val tapAnimationCounter: Int = 0,
    val isDebugMode: Boolean = false
)

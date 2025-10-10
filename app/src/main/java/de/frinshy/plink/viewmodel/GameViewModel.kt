package de.frinshy.plink.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var autoCollectorJob: Job? = null


    private var isAppInForeground: Boolean = false


    private var isMainScreenVisible: Boolean = false


    private val lifecycleObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                isAppInForeground = true

                val collectors = _uiState.value.gameState.upgradeLevels["auto_collector"] ?: 0
                if (collectors > 0) startAutoCollector()
            }

            Lifecycle.Event.ON_STOP -> {
                isAppInForeground = false

                stopAutoCollector()
            }

            else -> {}
        }
    }


    fun setMainScreenVisible(visible: Boolean) {
        isMainScreenVisible = visible
        if (visible) {

            val collectors = _uiState.value.gameState.upgradeLevels["auto_collector"] ?: 0
            if (isAppInForeground && collectors > 0) startAutoCollector()
        } else {

            stopAutoCollector()
        }
    }

    init {

        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)


        viewModelScope.launch {
            repository.gameState.collectLatest { gameState ->
                _uiState.value = _uiState.value.copy(
                    gameState = gameState,
                    isLoading = false
                )

                val collectors = (gameState.upgradeLevels["auto_collector"] ?: 0)
                if (collectors > 0) {

                    if (isAppInForeground) startAutoCollector()
                } else {
                    stopAutoCollector()
                }
            }
        }
    }


    fun onCoinTap() {
        viewModelScope.launch {
            val currentState = _uiState.value.gameState
            repository.addCoins(currentState.coinsPerTap.toLong())


            _uiState.value = _uiState.value.copy(
                tapAnimationCounter = _uiState.value.tapAnimationCounter + 1
            )
        }
    }


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


    private fun startAutoCollector() {

        if (!isAppInForeground) return

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


    private fun stopAutoCollector() {
        autoCollectorJob?.cancel()
        autoCollectorJob = null
    }


    fun getUpgradeLevel(upgrade: Upgrade): Int {
        val currentState = _uiState.value.gameState
        return when (upgrade) {
            is Upgrade.TapUpgrade -> currentState.upgradeLevels["tap_upgrade"] ?: 0
            is Upgrade.AutoCollector -> currentState.upgradeLevels["auto_collector"] ?: 0
        }
    }


    fun debugAddCoins(amount: Long) {
        viewModelScope.launch {
            repository.debugAddCoins(amount)
        }
    }


    fun debugSetCoins(amount: Long) {
        viewModelScope.launch {
            repository.debugSetCoins(amount)
        }
    }


    fun debugResetGame() {
        viewModelScope.launch {
            repository.debugResetGame()
        }
    }


    fun debugMaxUpgrades() {
        viewModelScope.launch {
            repository.debugSetCoins(999_999_999L)
            repository.updateTapUpgradeLevel(50)
            repository.updateCoinsPerTap(51)
            repository.updateAutoCollectors(25)
        }
    }


    fun gamble(wager: Long, onResult: ((won: Boolean, newBalance: Long) -> Unit)? = null) {
        viewModelScope.launch {
            val won = repository.gamble(wager)
            delay(50)
            val freshState = repository.gameState.first()
            onResult?.invoke(won, freshState.coins)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoCollector()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleObserver)
    }
}


data class GameUiState(
    val gameState: GameState = GameState(),
    val isLoading: Boolean = true,
    val tapAnimationCounter: Int = 0,
    val isDebugMode: Boolean = false
)

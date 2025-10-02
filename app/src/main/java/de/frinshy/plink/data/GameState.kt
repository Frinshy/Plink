package de.frinshy.plink.data

/**
 * Represents the complete game state
 */
data class GameState(
    val coins: Long = 0L,
    val coinsPerTap: Int = 1,
    /**
     * Generic map storing levels for upgrades by their id (e.g. "tap_upgrade", "auto_collector").
     * This centralizes upgrade state so adding/removing upgrades is simpler in future.
     */
    val upgradeLevels: Map<String, Int> = emptyMap(),
    val totalCoinsEarned: Long = 0L
)

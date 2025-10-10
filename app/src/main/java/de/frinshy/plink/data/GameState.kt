package de.frinshy.plink.data


data class GameState(
    val coins: Long = 0L,
    val coinsPerTap: Int = 1,

    val upgradeLevels: Map<String, Int> = emptyMap(),
    val totalCoinsEarned: Long = 0L
)

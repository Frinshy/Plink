package de.frinshy.plink.data

/**
 * Represents different types of upgrades available in the shop
 */
sealed class Upgrade(
    val id: String,
    val title: String,
    val description: String,
    val basePrice: Long,
    val maxLevel: Int = Int.MAX_VALUE
) {
    abstract fun getCurrentPrice(currentLevel: Int): Long
    abstract fun isAffordable(coins: Long, currentLevel: Int): Boolean
    fun isPurchasable(currentLevel: Int): Boolean = currentLevel < maxLevel

    /**
     * Increases coins per tap
     */
    data object TapUpgrade : Upgrade(
        id = "tap_upgrade",
        title = "Better Finger",
        description = "Increases coins per tap",
        basePrice = 15L,
        maxLevel = 50
    ) {
        override fun getCurrentPrice(currentLevel: Int): Long = basePrice * (currentLevel + 1) * 2
        override fun isAffordable(coins: Long, currentLevel: Int): Boolean =
            coins >= getCurrentPrice(currentLevel)
    }

    /**
     * Adds an auto collector that generates coins over time
     */
    data object AutoCollector : Upgrade(
        id = "auto_collector",
        title = "Auto Collector",
        description = "Generates 1 coin per second automatically",
        basePrice = 50L,
        maxLevel = 25
    ) {
        override fun getCurrentPrice(currentLevel: Int): Long = basePrice * (currentLevel + 1) * 3
        override fun isAffordable(coins: Long, currentLevel: Int): Boolean =
            coins >= getCurrentPrice(currentLevel)
    }

    /** List of all available upgrades */
    companion object {
        val allUpgrades = listOf(TapUpgrade, AutoCollector)
    }
}
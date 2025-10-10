package de.frinshy.plink.data


sealed class Upgrade(
    val title: String,
    val description: String,
    val basePrice: Long,
    val maxLevel: Int = Int.MAX_VALUE
) {
    abstract fun getCurrentPrice(currentLevel: Int): Long
    abstract fun isAffordable(coins: Long, currentLevel: Int): Boolean
    fun isPurchasable(currentLevel: Int): Boolean = currentLevel < maxLevel


    data object TapUpgrade : Upgrade(
        title = "Better Finger",
        description = "Increases coins per tap",
        basePrice = 15L,
        maxLevel = 50
    ) {
        override fun getCurrentPrice(currentLevel: Int): Long = basePrice * (currentLevel + 1) * 2
        override fun isAffordable(coins: Long, currentLevel: Int): Boolean =
            coins >= getCurrentPrice(currentLevel)
    }


    data object AutoCollector : Upgrade(
        title = "Auto Collector",
        description = "Generates 1 coin per second automatically",
        basePrice = 50L,
        maxLevel = 25
    ) {
        override fun getCurrentPrice(currentLevel: Int): Long = basePrice * (currentLevel + 1) * 3
        override fun isAffordable(coins: Long, currentLevel: Int): Boolean =
            coins >= getCurrentPrice(currentLevel)
    }


    companion object {
        val allUpgrades = listOf(TapUpgrade, AutoCollector)
    }
}
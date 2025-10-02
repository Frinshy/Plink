package de.frinshy.plink.ui.theme

/**
 * Design tokens for consistent elevation throughout the app.
 * Based on Material Design 3 elevation system.
 */
object Elevation {
    val none = ExpressiveElevation.level0
    val level1 = ExpressiveElevation.level1
    val level2 = ExpressiveElevation.level2
    val level3 = ExpressiveElevation.level3
    val level4 = ExpressiveElevation.level4
    val level5 = ExpressiveElevation.level5

    // Component-specific elevations
    val card = ExpressiveElevation.card
    val primaryCard = ExpressiveElevation.primaryCard
    val secondaryCard = ExpressiveElevation.secondaryCard
    val button = ExpressiveElevation.button
    val bottomSheet = ExpressiveElevation.level2
    val dialog = ExpressiveElevation.level4
    val fab = ExpressiveElevation.level3
    val appBar = ExpressiveElevation.level1
}
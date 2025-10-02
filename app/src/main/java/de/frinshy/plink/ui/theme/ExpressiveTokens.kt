package de.frinshy.plink.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Minimal expressive tokens inspired by Material 3 Expressive theme.
 * These provide slightly larger radii and elevated card/button depths
 * tuned for a playful game-like UI.
 */
object ExpressiveCornerRadius {
    val small = 6.dp
    val medium = 12.dp
    val large = 20.dp
    val extraLarge = 28.dp
}

object ExpressiveElevation {
    val level0 = 0.dp
    val level1 = 2.dp
    val level2 = 6.dp
    val level3 = 12.dp
    val level4 = 16.dp
    val level5 = 24.dp

    val card = level3
    val primaryCard = level4
    val secondaryCard = level2
    val button = level5
}

package de.frinshy.plink.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Design tokens for consistent spacing throughout the app.
 * Based on Material Design 3 spacing guidelines.
 */
object Spacing {
    val none = 0.dp
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
    val huge = 48.dp
    val massive = 64.dp

    // Component-specific spacing
    val componentPadding = medium
    val cardPadding = large
    val screenPadding = large
    val sectionSpacing = large
    val itemSpacing = small
    val buttonSpacing = medium
}
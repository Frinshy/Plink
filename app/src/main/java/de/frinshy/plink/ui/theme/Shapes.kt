package de.frinshy.plink.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Design tokens for consistent corner radius throughout the app.
 * Based on Material Design 3 shape system.
 */
object CornerRadius {
    val none = 0.dp
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 12.dp
    val large = 16.dp
    val extraLarge = 24.dp
    val full = 50.dp // For pill-shaped components
}

/**
 * Predefined shapes for common components.
 */
object GameShapes {
    val button = RoundedCornerShape(ExpressiveCornerRadius.medium)
    val card = RoundedCornerShape(ExpressiveCornerRadius.large)
    val chip = RoundedCornerShape(ExpressiveCornerRadius.small)
    val dialog = RoundedCornerShape(ExpressiveCornerRadius.extraLarge)
    val bottomSheet = RoundedCornerShape(
        topStart = CornerRadius.extraLarge,
        topEnd = CornerRadius.extraLarge
    )
    val circular = CircleShape
    val pill = RoundedCornerShape(CornerRadius.full)
}

/**
 * Material 3 shapes configuration for the app.
 */
val gameShapes = Shapes(
    extraSmall = RoundedCornerShape(ExpressiveCornerRadius.small),
    small = RoundedCornerShape(ExpressiveCornerRadius.small),
    medium = RoundedCornerShape(ExpressiveCornerRadius.medium),
    large = RoundedCornerShape(ExpressiveCornerRadius.large),
    extraLarge = RoundedCornerShape(ExpressiveCornerRadius.extraLarge)
)
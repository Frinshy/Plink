package de.frinshy.plink.ui.components

/** UI component: coin graphic used in the main tap button. */

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.frinshy.plink.ui.theme.coinGold
import de.frinshy.plink.ui.theme.coinGoldDark
import de.frinshy.plink.ui.theme.coinShine

/** Draws a stylized coin using Canvas. Sizes itself to [size]. */
@Composable
fun CoinGraphic(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    rimColor: Color = coinGoldDark,
    faceColor: Color = coinGold,
    shineColor: Color = coinShine,
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val diameter = size.toPx()
            val radius = diameter / 2f

            // Rim
            drawCircle(
                color = rimColor,
                radius = radius,
            )

            // Slightly inset face
            val faceRadius = radius * 0.9f

            // Radial gradient for coin face
            val center = Offset(radius, radius)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(faceColor, faceColor.copy(alpha = 0.85f), rimColor),
                    center = center,
                    radius = faceRadius,
                    tileMode = TileMode.Clamp
                ),
                radius = faceRadius,
                center = center
            )

            // Highlight (upper-left)
            drawCircle(
                color = shineColor.copy(alpha = 0.9f),
                radius = faceRadius * 0.35f,
                center = Offset(radius * 0.6f, radius * 0.5f)
            )

            // Subtle inner shadow (lower-right)
            drawCircle(
                color = rimColor.copy(alpha = 0.12f),
                radius = faceRadius * 0.95f,
                center = Offset(radius * 1.05f, radius * 1.05f)
            )
        }
    }
}

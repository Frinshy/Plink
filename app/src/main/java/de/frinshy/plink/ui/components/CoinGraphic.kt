package de.frinshy.plink.ui.components

/** UI component: coin graphic used in the main tap button. */

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.frinshy.plink.ui.theme.coinGold
import de.frinshy.plink.ui.theme.coinGoldDark
import de.frinshy.plink.ui.theme.coinShine
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min

/** Draws a stylized coin using Canvas. Sizes itself to [size]. */
@Composable
fun CoinGraphic(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    rimColor: Color = coinGoldDark,
    faceColor: Color = coinGold,
    shineColor: Color = coinShine,
) {
    Box(
        modifier = modifier.sizeIn(maxWidth = size, maxHeight = size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Use the actual canvas size to compute drawing dimensions so the graphic
            // always fits inside its available space (avoids clipping when parent
            // applies padding or different constraints).
            val canvasSize = this.size
            val diameter = min(canvasSize.width, canvasSize.height)
            val radius = diameter / 2f

            // Center of the canvas
            val center = Offset(canvasSize.width / 2f, canvasSize.height / 2f)

            // Slightly inset face
            val faceRadius = radius * 0.9f

            // Rim
            drawCircle(
                color = rimColor,
                radius = radius,
            )

            // Radial gradient for coin face
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

            // Highlight (upper-left) relative to center
            drawCircle(
                color = shineColor.copy(alpha = 0.9f),
                radius = faceRadius * 0.35f,
                center = center + Offset(-radius * 0.4f, -radius * 0.5f)
            )

            // Subtle inner shadow (lower-right) slightly offset from center
            drawCircle(
                color = rimColor.copy(alpha = 0.12f),
                radius = faceRadius * 0.95f,
                center = center + Offset(radius * 0.05f, radius * 0.05f)
            )
        }
    }
}

/**
 * An animated coin with 3D flip effects for gambling animations.
 * Features realistic perspective changes and smooth rotation.
 */
@Composable
fun AnimatedCoinGraphic(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    flipTrigger: Int = 0,
    onFlipComplete: ((heads: Boolean) -> Unit)? = null,
    rimColor: Color = coinGoldDark,
    faceColor: Color = coinGold,
    shineColor: Color = coinShine,
) {
    val rotationX = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }

    // Trigger flip animation when flipTrigger changes
    LaunchedEffect(flipTrigger) {
        if (flipTrigger > 0) {

            // Random number of flips (3-7 full rotations)
            val flips = (3..7).random()
            val finalResult = kotlin.random.Random.nextBoolean()

            // Start with a slight bounce
            scale.animateTo(1.1f, animationSpec = tween(150, easing = FastOutSlowInEasing))

            // X-axis flip (main flip motion)
            rotationX.animateTo(
                targetValue = flips * 360f + (if (finalResult) 0f else 180f),
                animationSpec = tween(1200, easing = LinearEasing)
            )

            // Scale bounce on landing
            scale.animateTo(0.9f, animationSpec = tween(100))
            scale.animateTo(1.05f, animationSpec = tween(150))
            scale.animateTo(1f, animationSpec = tween(200))

            onFlipComplete?.invoke(finalResult)

            // Reset rotations for next flip
            rotationX.snapTo(0f)
        }
    }

    Box(
        modifier = modifier.sizeIn(maxWidth = size, maxHeight = size),
        contentAlignment = Alignment.Center
    ) {
        val currentRotationX = rotationX.value
        val currentScale = scale.value

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .scale(currentScale)
                .rotate(currentRotationX)
        ) {
            val canvasSize = this.size
            val diameter = min(canvasSize.width, canvasSize.height)
            val radius = diameter / 2f
            val center = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
            val faceRadius = radius * 0.9f

            // Calculate perspective effect based on X rotation
            val perspectiveScale = abs(cos(Math.toRadians(currentRotationX.toDouble()))).toFloat()
            val showingBack = (currentRotationX % 360) > 90 && (currentRotationX % 360) < 270

            // Adjust colors based on perspective and side
            val currentRimColor = if (showingBack) rimColor.copy(alpha = 0.8f) else rimColor
            val currentFaceColor = if (showingBack) faceColor.copy(alpha = 0.7f) else faceColor

            // Draw rim (always visible)
            drawCircle(
                color = currentRimColor,
                radius = radius * perspectiveScale.coerceAtLeast(0.1f),
                center = center
            )

            // Draw face with perspective scaling
            if (perspectiveScale > 0.1f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            currentFaceColor,
                            currentFaceColor.copy(alpha = 0.85f),
                            currentRimColor
                        ),
                        center = center,
                        radius = faceRadius * perspectiveScale,
                        tileMode = TileMode.Clamp
                    ),
                    radius = faceRadius * perspectiveScale,
                    center = center
                )

                // Highlight effect (only on front side)
                if (!showingBack) {
                    drawCircle(
                        color = shineColor.copy(alpha = 0.9f * perspectiveScale),
                        radius = faceRadius * 0.35f * perspectiveScale,
                        center = center + Offset(
                            -radius * 0.4f * perspectiveScale,
                            -radius * 0.5f * perspectiveScale
                        )
                    )
                }
            }
        }
    }
}

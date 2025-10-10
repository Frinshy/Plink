package de.frinshy.plink.ui.components

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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.frinshy.plink.ui.theme.Color.coinGold
import de.frinshy.plink.ui.theme.Color.coinGoldDark
import de.frinshy.plink.ui.theme.Color.coinShine
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.random.Random


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
            drawCoin(
                rimColor = rimColor,
                faceColor = faceColor,
                shineColor = shineColor
            )
        }
    }
}


@Composable
fun AnimatedCoinGraphic(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    flipTrigger: Int = 0,
    result: Boolean? = null,
    onFlipComplete: ((heads: Boolean) -> Unit)? = null,
    rimColor: Color = coinGoldDark,
    faceColor: Color = coinGold,
    shineColor: Color = coinShine,
) {
    val rotationX = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }


    LaunchedEffect(flipTrigger) {
        if (flipTrigger > 0) {

            val flips = (3..7).random()
            val finalResult = result ?: Random.nextBoolean()


            scale.animateTo(1.1f, animationSpec = tween(150, easing = FastOutSlowInEasing))


            rotationX.animateTo(
                targetValue = flips * 360f + (if (finalResult) 0f else 180f),
                animationSpec = tween(1200, easing = LinearEasing)
            )


            scale.animateTo(0.9f, animationSpec = tween(100))
            scale.animateTo(1.05f, animationSpec = tween(150))
            scale.animateTo(1f, animationSpec = tween(200))

            onFlipComplete?.invoke(finalResult)


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

            val perspectiveScale = abs(cos(Math.toRadians(currentRotationX.toDouble()))).toFloat()
            val normalizedAngle = (currentRotationX % 360 + 360) % 360
            val showingBack = normalizedAngle > 90 && normalizedAngle < 270

            drawCoin(
                rimColor = if (showingBack) rimColor.copy(alpha = 0.8f) else rimColor,
                faceColor = if (showingBack) faceColor.copy(alpha = 0.7f) else faceColor,
                shineColor = shineColor,
                perspectiveScale = perspectiveScale
            )
        }
    }
}


private fun DrawScope.drawCoin(
    rimColor: Color,
    faceColor: Color,
    shineColor: Color,
    perspectiveScale: Float = 1f
) {
    val canvasSize = this.size
    val diameter = min(canvasSize.width, canvasSize.height)
    val radius = diameter / 2f
    val center = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
    val faceRadius = radius * 0.9f

    drawCircle(
        color = rimColor,
        radius = radius * perspectiveScale.coerceAtLeast(0.1f),
        center = center
    )

    if (perspectiveScale > 0.1f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(faceColor, faceColor.copy(alpha = 0.85f), rimColor),
                center = center,
                radius = faceRadius * perspectiveScale,
                tileMode = TileMode.Clamp
            ),
            radius = faceRadius * perspectiveScale,
            center = center
        )

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

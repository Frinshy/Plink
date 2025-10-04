package de.frinshy.plink.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.frinshy.plink.ui.theme.Elevation
import de.frinshy.plink.ui.theme.PlinkTheme

/**
 * A primary action button with consistent styling across the app.
 */
@Composable
fun PrimaryGameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * A secondary action button with tonal styling.
 */
@Composable
fun SecondaryGameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * An outlined button for less prominent actions.
 */
@Composable
fun OutlinedGameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * A large circular button typically used for main game actions like coin tapping.
 */
@Composable
fun CircularGameButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    containerColor: Color = Color.Transparent,
    contentColor: Color = Color.Transparent,
    shape: Shape = CircleShape,
    content: @Composable () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        // Use sizeIn so the button uses up to `size` but can shrink if parent imposes
        // tighter constraints (prevents the outer rim from being clipped).
        modifier = modifier.sizeIn(maxWidth = size, maxHeight = size),
        shape = shape,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Elevation.button,
            pressedElevation = Elevation.button + 6.dp,
            hoveredElevation = Elevation.button + 3.dp
        ),
        content = { content() }
    )
}

/**
 * A specialized button for gambling actions with enhanced visual feedback.
 * Features state-based styling, pulse animation when processing, and press feedback.
 */
@Composable
fun GambleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isProcessing: Boolean = false,
    amount: String? = null
) {
    val scale = remember { Animatable(1f) }
    val pulseScale = remember { Animatable(1f) }

    // Pulse animation when processing
    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            pulseScale.animateTo(
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            pulseScale.snapTo(1f)
        }
    }

    // Press feedback animation
    LaunchedEffect(enabled) {
        if (!enabled && !isProcessing) {
            scale.animateTo(0.95f, animationSpec = tween(100))
            scale.animateTo(1f, animationSpec = spring())
        }
    }

    val buttonColor by animateColorAsState(
        targetValue = when {
            isProcessing -> MaterialTheme.colorScheme.tertiary
            !enabled -> MaterialTheme.colorScheme.outline
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(300),
        label = "button_color"
    )

    Button(
        onClick = {
            onClick()
        },
        modifier = modifier
            .fillMaxWidth()
            .scale(scale.value)
            .scale(pulseScale.value),
        enabled = enabled && !isProcessing,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isProcessing) 8.dp else 4.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isProcessing) "Rolling..." else text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        if (amount != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "($amount)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameButtonsPreview() {
    PlinkTheme {
        PrimaryGameButton(
            text = "Primary Action",
            onClick = {},
            icon = Icons.Default.ShoppingCart
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SecondaryGameButtonPreview() {
    PlinkTheme {
        SecondaryGameButton(
            text = "Secondary Action",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OutlinedGameButtonPreview() {
    PlinkTheme {
        OutlinedGameButton(
            text = "Outlined Action",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GambleButtonPreview() {
    PlinkTheme {
        GambleButton(
            text = "Gamble",
            onClick = {},
            amount = "1,000"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GambleButtonProcessingPreview() {
    PlinkTheme {
        GambleButton(
            text = "Gamble",
            onClick = {},
            isProcessing = true,
            amount = "1,000"
        )
    }
}
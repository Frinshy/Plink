package de.frinshy.plink.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.frinshy.plink.ui.theme.Elevation
import de.frinshy.plink.ui.theme.PlinkTheme

/**
 * A reusable elevated card component with consistent styling across the app.
 * Supports different container colors and elevation levels.
 */
@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    elevation: Dp = Elevation.card,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        content = content
    )
}

/**
 * A primary game card with the app's primary container colors.
 */
@Composable
fun PrimaryGameCard(
    modifier: Modifier = Modifier,
    elevation: Dp = Elevation.primaryCard,
    content: @Composable ColumnScope.() -> Unit
) {
    GameCard(
        modifier = modifier,
        elevation = elevation,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        content = content
    )
}

/**
 * A secondary game card with the app's secondary container colors.
 */
@Composable
fun SecondaryGameCard(
    modifier: Modifier = Modifier,
    elevation: Dp = Elevation.secondaryCard,
    content: @Composable ColumnScope.() -> Unit
) {
    GameCard(
        modifier = modifier,
        elevation = elevation,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun GameCardPreview() {
    PlinkTheme {
        PrimaryGameCard {
            Text(
                text = "Primary Game Card",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SecondaryGameCardPreview() {
    PlinkTheme {
        SecondaryGameCard {
            Text(
                text = "Secondary Game Card",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
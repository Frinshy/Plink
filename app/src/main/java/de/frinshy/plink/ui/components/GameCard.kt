package de.frinshy.plink.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import de.frinshy.plink.ui.theme.Elevation


@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    elevation: Dp = Elevation.card,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable ColumnScope.() -> Unit
) {
    val elevationSpec = CardDefaults.elevatedCardElevation(defaultElevation = elevation)
    val colors = CardDefaults.elevatedCardColors(
        containerColor = containerColor,
        contentColor = contentColor
    )

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = elevationSpec,
        colors = colors,
        content = content
    )
}


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
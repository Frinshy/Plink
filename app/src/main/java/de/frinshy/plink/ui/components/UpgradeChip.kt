package de.frinshy.plink.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frinshy.plink.ui.theme.PlinkTheme

/**
 * A reusable component that displays an upgrade with its level.
 * Shows different styling based on whether the upgrade has been purchased.
 */
@Composable
fun UpgradeChip(
    label: String,
    level: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    AssistChip(
        onClick = { onClick?.invoke() },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        },
        trailingIcon = if (level > 0) {
            {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = level.toString(),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else null,
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (level > 0)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            else
                MaterialTheme.colorScheme.surface,
            labelColor = if (level > 0)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun UpgradeChipPreview() {
    PlinkTheme {
        UpgradeChip(
            label = "Tap Power",
            level = 5
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UpgradeChipNoLevelPreview() {
    PlinkTheme {
        UpgradeChip(
            label = "Not Purchased",
            level = 0
        )
    }
}
package de.frinshy.plink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.frinshy.plink.data.Upgrade
import de.frinshy.plink.ui.components.PrimaryGameCard
import de.frinshy.plink.ui.theme.Color.coinGold
import de.frinshy.plink.ui.theme.Color.successGreen
import de.frinshy.plink.ui.theme.PlinkTheme
import de.frinshy.plink.ui.theme.Spacing

import de.frinshy.plink.utils.NumberFormatter
import de.frinshy.plink.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    gameViewModel: GameViewModel = viewModel()
) {
    val uiState by gameViewModel.uiState.collectAsState()
    val gameState = uiState.gameState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.screenPadding)
    ) {

        CoinBalanceCard(coins = gameState.coins)


        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            items(Upgrade.allUpgrades) { upgrade ->
                UpgradeCard(
                    upgrade = upgrade,
                    currentLevel = gameViewModel.getUpgradeLevel(upgrade),
                    coins = gameState.coins,
                    onPurchase = { gameViewModel.purchaseUpgrade(upgrade) })
            }


            item { Spacer(modifier = Modifier.height(Spacing.medium)) }
        }
    }
}

@Composable
private fun CoinBalanceCard(coins: Long) {
    PrimaryGameCard {
        Row(
            modifier = Modifier.padding(Spacing.cardPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            Surface(
                shape = MaterialTheme.shapes.small, color = coinGold.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "💰",
                    modifier = Modifier.padding(Spacing.medium),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Available Coins",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Text(
                    text = NumberFormatter.formatNumber(coins),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = coinGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun UpgradeCard(
    upgrade: Upgrade, currentLevel: Int, coins: Long, onPurchase: () -> Unit
) {
    val isAffordable = upgrade.isAffordable(coins, currentLevel)
    val isPurchasable = upgrade.isPurchasable(currentLevel)
    val canPurchase = isAffordable && isPurchasable
    val currentPrice = if (isPurchasable) upgrade.getCurrentPrice(currentLevel) else 0L

    val icon = when (upgrade) {
        is Upgrade.TapUpgrade -> Icons.Outlined.TouchApp
        is Upgrade.AutoCollector -> Icons.Outlined.AutoAwesome
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (canPurchase) 6.dp else 2.dp
        ), colors = CardDefaults.elevatedCardColors(
            containerColor = if (canPurchase) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (canPurchase) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (canPurchase) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp),
                            tint = if (canPurchase) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = upgrade.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = upgrade.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }


                if (currentLevel > 0) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = currentLevel.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isPurchasable) "Price" else "Max Level Reached",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    if (isPurchasable) {
                        Text(
                            text = "${NumberFormatter.formatNumber(currentPrice)} coins",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isAffordable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.titleSmall,
                            color = successGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (isPurchasable) {
                    Button(
                        onClick = onPurchase,
                        enabled = canPurchase,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                            disabledContentColor = MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(
                            text = if (isAffordable) "Purchase" else "Too Expensive",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    AssistChip(
                        onClick = { },
                        label = { Text("Maxed") },
                        enabled = false,
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = successGreen.copy(alpha = 0.12f),
                            labelColor = successGreen,
                            disabledContainerColor = successGreen.copy(alpha = 0.12f),
                            disabledLabelColor = successGreen
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShopScreenPreview() {
    PlinkTheme {
        ShopScreen(
        )
    }
}

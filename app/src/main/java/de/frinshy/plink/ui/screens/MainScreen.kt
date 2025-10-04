package de.frinshy.plink.ui.screens

/** MainScreen: primary game UI (balance, tap target, upgrades). */

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.frinshy.plink.data.GameState
import de.frinshy.plink.ui.components.CircularGameButton
import de.frinshy.plink.ui.components.CoinGraphic
import de.frinshy.plink.ui.components.PrimaryGameButton
import de.frinshy.plink.ui.components.PrimaryGameCard
import de.frinshy.plink.ui.components.SecondaryGameCard
import de.frinshy.plink.ui.components.SectionHeader
import de.frinshy.plink.ui.components.StatChip
import de.frinshy.plink.ui.components.UpgradeChip
import de.frinshy.plink.ui.theme.CoinDisplayTypography
import de.frinshy.plink.ui.theme.GameTitleTypography
import de.frinshy.plink.ui.theme.PlinkTheme
import de.frinshy.plink.ui.theme.Spacing
import de.frinshy.plink.ui.theme.coinGold
import de.frinshy.plink.utils.NumberFormatter
import de.frinshy.plink.viewmodel.GameUiState
import de.frinshy.plink.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
        /** Main game screen: coin display, tap button and upgrades overview. */
fun MainScreen(
    onNavigateToShop: () -> Unit,
    gameViewModel: GameViewModel = viewModel()
) {
    val uiState: GameUiState by gameViewModel.uiState.collectAsState()
    val gameState = uiState.gameState

    // Notify ViewModel that the main screen is visible while this composable is
    // in composition. This ensures auto-collector runs only when the main game
    // screen is actually shown.
    DisposableEffect(Unit) {
        gameViewModel.setMainScreenVisible(true)
        onDispose {
            gameViewModel.setMainScreenVisible(false)
        }
    }

    // Animatable scale for tap-press animation
    val coinButtonScale = remember { androidx.compose.animation.core.Animatable(1f) }

    // Track last seen tap counter so each increment triggers an animation
    var lastTapCounter by remember { mutableIntStateOf(uiState.tapAnimationCounter) }

    LaunchedEffect(uiState.tapAnimationCounter) {
        // Run one press animation per counter increment (robust for rapid taps)
        if (uiState.tapAnimationCounter != lastTapCounter) {
            lastTapCounter = uiState.tapAnimationCounter
            // quick press then spring back
            coinButtonScale.snapTo(0.88f)
            coinButtonScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    // Locking the app to portrait allows us to use weight/fill to make the coin
    // area fill the available screen space reliably.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionSpacing)
    ) {
        // App title
        Text(
            text = "Plink",
            style = GameTitleTypography,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Coin display
        PrimaryGameCard {
            Column(
                modifier = Modifier.padding(Spacing.cardPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                Text(
                    text = "Total Coins",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )

                Text(
                    text = NumberFormatter.formatNumber(gameState.coins),
                    style = CoinDisplayTypography,
                    color = coinGold,
                    textAlign = TextAlign.Center
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.extraLarge),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatChip(
                        icon = Icons.Outlined.TouchApp,
                        label = "Per Click",
                        value = NumberFormatter.formatNumber(gameState.coinsPerTap.toLong()),
                        modifier = Modifier.weight(1f)
                    )
                    StatChip(
                        icon = Icons.Outlined.AutoAwesome,
                        label = "Per Second",
                        // Auto coins per second derived from auto collector count
                        value = NumberFormatter.formatNumber(
                            (gameState.upgradeLevels["auto_collector"] ?: 0).toLong()
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Main coin button
        // Main coin area: fill the remaining screen space so the coin is large
        // and centered on portrait phones.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            CircularGameButton(
                onClick = {
                    gameViewModel.onCoinTap()
                },
                modifier = Modifier
                    .sizeIn(maxWidth = 240.dp, maxHeight = 240.dp)
                    .scale(coinButtonScale.value)
            ) {
                // Canvas-drawn coin graphic (fixed inside the button)
                CoinGraphic(size = 200.dp)
            }
        }
        // Upgrades overview
        UpgradesOverviewCard(
            gameState = gameState,
            onShopClick = onNavigateToShop
        )
    }
}

@Composable
private fun UpgradesOverviewCard(
    gameState: GameState,
    onShopClick: () -> Unit
) {
    SecondaryGameCard {
        Column(
            modifier = Modifier.padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            SectionHeader(
                title = "Upgrades",
                icon = Icons.AutoMirrored.Outlined.TrendingUp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                modifier = Modifier.fillMaxWidth()
            ) {
                UpgradeChip(
                    label = "Tap Power",
                    level = gameState.upgradeLevels["tap_upgrade"] ?: 0,
                    modifier = Modifier.weight(1f)
                )
                UpgradeChip(
                    label = "Auto Collectors",
                    level = gameState.upgradeLevels["auto_collector"] ?: 0,
                    modifier = Modifier.weight(1f)
                )
            }

            PrimaryGameButton(
                text = "Visit Shop",
                onClick = onShopClick,
                icon = Icons.Default.ShoppingCart
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PlinkTheme {
        MainScreen(
            onNavigateToShop = {}
        )
    }
}
package de.frinshy.plink.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import de.frinshy.plink.data.GameRepository
import de.frinshy.plink.utils.NumberFormatter

class CoinsWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Read the latest game state from repository (DataStore) and pass coins into the Glance UI
        val repo = GameRepository(context)

        provideContent {
            GlanceTheme {
                // create your AppWidget here and provide the formatted coins
                MyContent(repo)
            }
        }
    }

    @Composable
    private fun MyContent(repo: GameRepository) {
        val currentState = repo.gameState
            .collectAsState(initial = null)
            .value

        val formattedCoins = currentState?.let { NumberFormatter.formatNumber(it.coins) } ?: "..."
        val formattedTotal =
            currentState?.let { NumberFormatter.formatNumber(it.totalCoinsEarned) } ?: "..."

        // Determine available size and pick a layout variant. LocalSize gives
        // the size the widget has been allocated; we'll switch to a compact
        // layout for narrow widgets and show an expanded, more game-like UI
        // for larger widgets.
        val size = LocalSize.current
        val isCompact = size.width < 120.dp

        when {
            isCompact -> {
                // Compact layout: single-line coin display with bold emoji
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.background)
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🪙",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontSize = 18.sp
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = formattedCoins, style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer
                        )
                    )
                }
            }

            else -> {
                // Expanded layout: game-like card with coin, big number, and stats
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.background)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title row with coin image and badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🪙", modifier = GlanceModifier.padding(end = 8.dp))
                        Text(
                            text = "Coins",
                            style = TextStyle(
                                color = GlanceTheme.colors.onPrimaryContainer,
                                fontSize = 14.sp
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    // Big coin number to attract attention
                    Text(
                        text = formattedCoins,
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontSize = 22.sp
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    // Small stats row
                    Text(
                        text = "Total earned: $formattedTotal",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}
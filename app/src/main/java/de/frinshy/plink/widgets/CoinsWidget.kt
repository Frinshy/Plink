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
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
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
import de.frinshy.plink.MainActivity
import de.frinshy.plink.data.GameRepository
import de.frinshy.plink.utils.NumberFormatter

class CoinsWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Read the latest game state from repository (DataStore) and pass coins into the Glance UI
        val repo = GameRepository(context)

        val clickModifier = GlanceModifier.clickable(actionStartActivity<MainActivity>())

        provideContent {
            GlanceTheme {
                // create your AppWidget here and provide the formatted coins
                MyContent(repo, clickModifier)
            }
        }
    }

    @Composable
    private fun MyContent(repo: GameRepository, clickModifier: GlanceModifier) {
        val currentState = repo.gameState
            .collectAsState(initial = null)
            .value

        val formattedCoins = currentState?.let { NumberFormatter.formatNumber(it.coins) } ?: "..."
        val formattedTotal =
            currentState?.let { NumberFormatter.formatNumber(it.totalCoinsEarned) } ?: "..."

        // Get available size and determine layout variant
        val size = LocalSize.current

        // Define size categories with adjusted breakpoints for better readability
        val widgetLayout = when {
            size.width < 80.dp || size.height < 50.dp -> WidgetLayout.MINIMAL
            size.width < 140.dp || size.height < 70.dp -> WidgetLayout.COMPACT
            size.width < 200.dp || size.height < 100.dp -> WidgetLayout.MEDIUM
            else -> WidgetLayout.LARGE
        }

        // Adjust font sizes based on coin text length to prevent overflow
        val coinTextLength = formattedCoins.length
        val fontSizeModifier = when {
            coinTextLength > 15 -> 0.75f
            coinTextLength > 10 -> 0.9f
            else -> 1.0f
        }

        when (widgetLayout) {
            WidgetLayout.MINIMAL -> {
                // Minimal layout: Only coin emoji and number
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.primaryContainer)
                        .padding(6.dp)
                        .then(clickModifier),
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
                    Text(
                        text = formattedCoins,
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontSize = (14.sp.value * fontSizeModifier).sp
                        )
                    )
                }
            }

            WidgetLayout.COMPACT -> {
                // Compact layout: Coin and number with minimal spacing
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.primaryContainer)
                        .padding(8.dp)
                        .then(clickModifier),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🪙",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontSize = 20.sp
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(3.dp))
                    Text(
                        text = formattedCoins,
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontSize = (16.sp.value * fontSizeModifier).sp
                        )
                    )
                }
            }

            WidgetLayout.MEDIUM -> {
                // Medium layout: Coin, title, and main number
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.primaryContainer)
                        .padding(10.dp)
                        .then(clickModifier),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title row with coin and label
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = "🪙",
                            style = TextStyle(fontSize = 16.sp),
                            modifier = GlanceModifier.padding(end = 6.dp)
                        )
                        Text(
                            text = "Coins",
                            style = TextStyle(
                                color = GlanceTheme.colors.onPrimaryContainer,
                                fontSize = 14.sp
                            )
                        )
                    }

                    // Main coin display
                    Text(
                        text = formattedCoins,
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontSize = (20.sp.value * fontSizeModifier).sp
                        )
                    )
                }
            }

            WidgetLayout.LARGE -> {
                // Large layout: Full experience with all information
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.primaryContainer)
                        .padding(12.dp)
                        .then(clickModifier),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title row with coin image and proper spacing
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "🪙",
                            style = TextStyle(fontSize = 18.sp),
                            modifier = GlanceModifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Plink Coins",
                            style = TextStyle(
                                color = GlanceTheme.colors.onPrimaryContainer,
                                fontSize = 16.sp
                            )
                        )
                    }

                    // Main coin display with proper hierarchy
                    Text(
                        text = formattedCoins,
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontSize = (24.sp.value * fontSizeModifier).sp
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    // Additional stats with better styling
                    Text(
                        text = "Total: $formattedTotal",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }

    // Enum to define widget layout types
    private enum class WidgetLayout {
        MINIMAL,    // < 80dp width or < 50dp height
        COMPACT,    // < 140dp width or < 70dp height  
        MEDIUM,     // < 200dp width or < 100dp height
        LARGE       // >= 200dp width and >= 100dp height
    }
}
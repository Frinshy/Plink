package de.frinshy.plink.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.frinshy.plink.ui.components.AnimatedCoinGraphic
import de.frinshy.plink.ui.components.GambleButton
import de.frinshy.plink.ui.components.PrimaryGameCard
import de.frinshy.plink.ui.components.SecondaryGameCard
import de.frinshy.plink.ui.theme.Spacing
import de.frinshy.plink.utils.NumberFormatter
import de.frinshy.plink.utils.rememberGameFeedbackManager
import de.frinshy.plink.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GambleScreen(
    gameViewModel: GameViewModel = viewModel()
) {
    val uiState by gameViewModel.uiState.collectAsState()
    val coins = uiState.gameState.coins
    val context = LocalContext.current
    val feedbackManager = rememberGameFeedbackManager()
    val coroutineScope = rememberCoroutineScope()

    var wagerText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var resultWon by remember { mutableStateOf(false) }
    var resultDelta by remember { mutableLongStateOf(0L) }
    var flipTrigger by remember { mutableIntStateOf(0) }
    var previousBalance by remember { mutableLongStateOf(coins) }
    var animatedBalance by remember { mutableLongStateOf(coins) }
    var gamblingResult by remember { mutableStateOf<Boolean?>(null) }

    // Screen shake animation for big wins
    val screenShake = remember { Animatable(0f) }
    val backgroundPulse = remember { Animatable(1f) }

    // Update animated balance when coins change
    LaunchedEffect(coins) {
        if (!isProcessing) {
            animatedBalance = coins
        }
    }

    // Reset gambling result when starting new gamble
    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            gamblingResult = null
            showResult = false
        }
    }

    // Handle screen effects when result is shown  
    LaunchedEffect(showResult, resultWon, resultDelta) {
        if (showResult) {
            // Trigger screen effects based on result
            if (resultWon && kotlin.math.abs(resultDelta) >= 10000) {
                // Big win screen shake
                repeat(3) {
                    screenShake.animateTo(10f, tween(50))
                    screenShake.animateTo(-10f, tween(50))
                }
                screenShake.animateTo(0f, tween(100))
            }

            if (resultWon) {
                // Win pulse effect
                backgroundPulse.animateTo(1.02f, tween(200))
                backgroundPulse.animateTo(1f, spring())
            }
        }
    }

    // Animate balance counter
    LaunchedEffect(showResult) {
        if (showResult) {
            val startBalance = previousBalance
            val endBalance = coins
            val duration = 800L
            val steps = 20

            repeat(steps) { step ->
                val progress = (step + 1).toFloat() / steps
                val currentBalance =
                    startBalance + ((endBalance - startBalance) * progress).toLong()
                animatedBalance = currentBalance
                delay(duration / steps)
            }
            animatedBalance = endBalance
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        // Combined balance and coin area for compact design
        PrimaryGameCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Balance display - left side
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
                ) {
                    Text(
                        text = "Balance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = NumberFormatter.formatNumber(animatedBalance),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (showResult && resultWon) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Coin animation - center/right
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = screenShake.value
                            scaleX = backgroundPulse.value
                            scaleY = backgroundPulse.value
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
                    ) {
                        Text(
                            text = if (isProcessing) "Flipping..." else "Ready",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )

                        AnimatedCoinGraphic(
                            size = 100.dp, // Compact size
                            flipTrigger = flipTrigger,
                            result = gamblingResult,
                            onFlipComplete = { animationResult ->
                                // Animation completed, now show the result
                                if (gamblingResult != null) {
                                    resultWon = gamblingResult!!
                                    showResult = true

                                    // Haptic feedback based on result
                                    if (resultWon) {
                                        if (resultDelta >= 10000) {
                                            feedbackManager.celebrationHaptic()
                                            feedbackManager.playBigWinSound()
                                        } else {
                                            feedbackManager.mediumHaptic()
                                            feedbackManager.playWinSound()
                                        }
                                    } else {
                                        feedbackManager.lossHaptic()
                                        feedbackManager.playLossSound()
                                    }
                                }
                            }
                        )

                        Text(
                            text = "50/50",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // Improved wager input and action section
        SecondaryGameCard {
            Column(
                modifier = Modifier.padding(Spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                // Section title
                Text(
                    text = "Place Your Wager",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                // Wager input - full width for better appearance
                OutlinedTextField(
                    value = wagerText,
                    onValueChange = { newValue ->
                        val filtered = newValue.filter { it.isDigit() }.take(10)
                        wagerText = filtered
                    },
                    label = { Text("Wager Amount") },
                    placeholder = { Text("Enter coins to wager") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing,
                    singleLine = true,
                    suffix = { Text("coins", style = MaterialTheme.typography.bodyMedium) }
                )

                // Action row with quick bets and flip button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick bet buttons - compact
                    if (!isProcessing) {
                        val quickBets = listOf(100L, 1000L, coins / 4, coins / 2)
                            .filter { it > 0 && it <= coins }
                            .distinct()
                            .take(3) // Limit to 3 for better fit

                        quickBets.forEach { amount ->
                            androidx.compose.material3.OutlinedButton(
                                onClick = {
                                    feedbackManager.lightHaptic()
                                    feedbackManager.playButtonClickSound()
                                    wagerText = amount.toString()
                                },
                                enabled = amount <= coins,
                                contentPadding = PaddingValues(
                                    horizontal = 8.dp,
                                    vertical = 6.dp
                                )
                            ) {
                                Text(
                                    text = NumberFormatter.formatNumber(amount),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Spacer to push flip button to the right
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))

                    // Flip button
                    GambleButton(
                        text = "Flip Coin",
                        onClick = {
                            val wager = wagerText.toLongOrNull() ?: 0L
                            if (wager <= 0L) {
                                Toast.makeText(
                                    context,
                                    "Enter a positive wager",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                return@GambleButton
                            }
                            if (wager > coins) {
                                Toast.makeText(context, "Not enough coins", Toast.LENGTH_SHORT)
                                    .show()
                                return@GambleButton
                            }

                            feedbackManager.coinFlipHaptic()
                            feedbackManager.playCoinFlipSound()

                            isProcessing = true
                            showResult = false
                            previousBalance = coins
                            resultDelta = wager
                            gamblingResult = null
                            flipTrigger += 1

                            coroutineScope.launch {
                                // Start gambling in background while animation plays
                                gameViewModel.gamble(wager) { won, newBalance ->
                                    // Set the gambling result which will be used by animation
                                    gamblingResult = won
                                    resultDelta = if (won) wager else -wager

                                    coroutineScope.launch {
                                        // Wait for animation to complete before cleanup
                                        delay(1500)
                                        isProcessing = false
                                        wagerText = ""

                                        // Reset result after showing for a while
                                        delay(3000)
                                        showResult = false
                                        gamblingResult = null

                                        delay(500)
                                        val message = if (won) {
                                            "🎉 You won ${NumberFormatter.formatNumber(wager)} coins!"
                                        } else {
                                            "💸 You lost ${NumberFormatter.formatNumber(wager)} coins"
                                        }
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        enabled = !isProcessing && wagerText.toLongOrNull()
                            ?.let { it > 0 && it <= coins } == true && gamblingResult == null,
                        isProcessing = isProcessing,
                        amount = null
                    )
                }
            }
        }

        // Compact result display and rules
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            // Result display
            AnimatedVisibility(
                visible = showResult,
                enter = fadeIn(animationSpec = tween(500)) + scaleIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(300)) + scaleOut(animationSpec = tween(300)),
                modifier = Modifier.weight(1f)
            ) {
                SecondaryGameCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (resultWon) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.errorContainer
                            )
                            .padding(Spacing.medium),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
                        ) {
                            Text(
                                text = if (resultWon) "🎉 WON!" else "💸 LOST",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (resultWon) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onErrorContainer
                            )

                            val sign = if (resultDelta >= 0L) "+" else ""
                            Text(
                                text = "$sign${NumberFormatter.formatNumber(resultDelta)}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = if (resultWon) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // Game rules - always visible but compact
            if (!showResult) {
                SecondaryGameCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
                    ) {
                        Text(
                            text = "Rules",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Text(
                            text = "• Win: Double your wager\n• Lose: Forfeit wager\n• 50/50 chance",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.frinshy.plink.ui.components.AnimatedCoinGraphic
import de.frinshy.plink.ui.components.GambleButton
import de.frinshy.plink.ui.components.PrimaryGameCard
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

    // Screen shake animation for big wins
    val screenShake = remember { Animatable(0f) }
    val backgroundPulse = remember { Animatable(1f) }

    // Update animated balance when coins change
    LaunchedEffect(coins) {
        if (!isProcessing) {
            animatedBalance = coins
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

    PrimaryGameCard {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = screenShake.value
                    scaleX = backgroundPulse.value
                    scaleY = backgroundPulse.value
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                // Title with enhanced styling
                Text(
                    text = "🎲 Coin Flip Gamble",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Animated coin
                AnimatedCoinGraphic(
                    size = 120.dp,
                    flipTrigger = flipTrigger,
                    onFlipComplete = { won ->
                        resultWon = won
                        showResult = true

                        // Haptic feedback based on result
                        if (won) {
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
                )

                // Handle screen effects when result is shown
                LaunchedEffect(showResult, resultWon, resultDelta) {
                    if (showResult) {
                        // Trigger screen effects based on result
                        if (resultWon && resultDelta >= 10000) {
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

                Spacer(modifier = Modifier.height(8.dp))

                // Balance display with animation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Balance: ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = NumberFormatter.formatNumber(animatedBalance),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (showResult && resultWon) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Wager input
                OutlinedTextField(
                    value = wagerText,
                    onValueChange = { newValue ->
                        // Only allow digits and limit to reasonable length
                        val filtered = newValue.filter { it.isDigit() }.take(10)
                        wagerText = filtered
                    },
                    label = { Text("Wager Amount") },
                    placeholder = { Text("Enter coins to wager") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing,
                    singleLine = true
                )

                // Quick bet buttons
                if (!isProcessing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val quickBets = listOf(100L, 1000L, coins / 4, coins / 2)
                            .filter { it > 0 && it <= coins }
                            .distinct()
                            .take(4)

                        quickBets.forEach { amount ->
                            androidx.compose.material3.OutlinedButton(
                                onClick = {
                                    feedbackManager.lightHaptic()
                                    feedbackManager.playButtonClickSound()
                                    wagerText = amount.toString()
                                },
                                modifier = Modifier.weight(1f),
                                enabled = amount <= coins
                            ) {
                                Text(
                                    text = NumberFormatter.formatNumber(amount),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Enhanced gamble button
                GambleButton(
                    text = "Flip Coin",
                    onClick = {
                        val wager = wagerText.toLongOrNull() ?: 0L
                        if (wager <= 0L) {
                            Toast.makeText(context, "Enter a positive wager", Toast.LENGTH_SHORT)
                                .show()
                            return@GambleButton
                        }
                        if (wager > coins) {
                            Toast.makeText(context, "Not enough coins", Toast.LENGTH_SHORT).show()
                            return@GambleButton
                        }

                        // Button press feedback
                        feedbackManager.coinFlipHaptic()
                        feedbackManager.playCoinFlipSound()

                        // Start animation sequence
                        isProcessing = true
                        showResult = false
                        previousBalance = coins
                        resultDelta = wager // Store the wager amount

                        // Trigger coin flip animation
                        flipTrigger += 1

                        // Execute gamble after a delay for suspense
                        coroutineScope.launch {
                            delay(300) // Wait for flip to start
                            gameViewModel.gamble(wager) { won, _ ->
                                resultDelta = if (won) wager else -wager

                                // Wait for coin flip to complete before showing result
                                coroutineScope.launch {
                                    delay(1200) // Wait for flip animation
                                    isProcessing = false
                                    wagerText = ""

                                    // Show toast after a brief delay
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
                        ?.let { it > 0 && it <= coins } == true,
                    isProcessing = isProcessing,
                    amount = wagerText.toLongOrNull()?.let { NumberFormatter.formatNumber(it) }
                )

                // Animated result display
                AnimatedVisibility(
                    visible = showResult,
                    enter = fadeIn(animationSpec = tween(500)) + scaleIn(animationSpec = tween(500)),
                    exit = fadeOut(animationSpec = tween(300)) + scaleOut(animationSpec = tween(300))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (resultWon) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.errorContainer
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (resultWon) "🎉 YOU WON!" else "💸 YOU LOST",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (resultWon) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onErrorContainer
                            )

                            val sign = if (resultDelta >= 0L) "+" else ""
                            Text(
                                text = "$sign${NumberFormatter.formatNumber(resultDelta)} coins",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (resultWon) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                // Game rules explanation
                if (!isProcessing && !showResult) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "💰 Win: Double your wager\n💸 Lose: Forfeit your wager\n🎯 50/50 chance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

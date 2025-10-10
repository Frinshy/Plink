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
import androidx.compose.foundation.layout.Spacer
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GambleScreen(
    gameViewModel: GameViewModel = viewModel()
) {
    val uiState by gameViewModel.uiState.collectAsState()
    val coins = uiState.gameState.coins
    val context = LocalContext.current
    val feedback = rememberGameFeedbackManager()
    val scope = rememberCoroutineScope()


    var wagerText by remember { mutableStateOf("") }
    var isFlipping by remember { mutableStateOf(false) }
    var showOutcome by remember { mutableStateOf(false) }
    var didWin by remember { mutableStateOf(false) }
    var deltaAmount by remember { mutableLongStateOf(0L) }
    var flipCount by remember { mutableIntStateOf(0) }
    var previousBalance by remember { mutableLongStateOf(coins) }
    var animatedBalance by remember { mutableLongStateOf(coins) }
    var flipOutcome by remember { mutableStateOf<Boolean?>(null) }

    val screenShake = remember { Animatable(0f) }
    val backgroundPulse = remember { Animatable(1f) }

    LaunchedEffect(coins) { if (!isFlipping) animatedBalance = coins }

    LaunchedEffect(isFlipping) {
        if (isFlipping) {
            flipOutcome = null
            showOutcome = false
        }
    }

    LaunchedEffect(showOutcome, didWin, deltaAmount) {
        if (!showOutcome) return@LaunchedEffect

        if (didWin && abs(deltaAmount) >= 10_000L) {
            repeat(3) {
                screenShake.animateTo(10f, tween(50))
                screenShake.animateTo(-10f, tween(50))
            }
            screenShake.animateTo(0f, tween(100))
        }

        if (didWin) {
            backgroundPulse.animateTo(1.02f, tween(200))
            backgroundPulse.animateTo(1f, spring())
        }
    }

    LaunchedEffect(showOutcome) {
        if (!showOutcome) return@LaunchedEffect
        val start = previousBalance
        val end = coins
        val duration = 800L
        val steps = 20

        repeat(steps) { step ->
            val progress = (step + 1).toFloat() / steps
            val diff = (end - start).toFloat()
            val current = start + (diff * progress).toLong()
            animatedBalance = current
            delay(duration / steps)
        }
        animatedBalance = end
    }

    fun launchResultCleanup(
        coroutineScope: CoroutineScope,
        won: Boolean,
        wager: Long,
        onFinish: () -> Unit
    ) {
        coroutineScope.launch {
            delay(1500)
            onFinish()
            delay(3000)
            showOutcome = false
            flipOutcome = null
            delay(500)
            val message = if (won) "🎉 You won ${NumberFormatter.formatNumber(wager)} coins!"
            else "💸 You lost ${NumberFormatter.formatNumber(wager)} coins"
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        PrimaryGameCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        color = if (showOutcome && didWin) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }


                AnimatedCoinGraphic(
                    size = 100.dp,
                    flipTrigger = flipCount,
                    result = flipOutcome,
                    onFlipComplete = { _ ->
                        if (flipOutcome != null) {
                            didWin = flipOutcome!!
                            showOutcome = true
                            if (didWin) {
                                if (deltaAmount >= 10_000L) {
                                    feedback.celebrationHaptic()
                                    feedback.playBigWinSound()
                                } else {
                                    feedback.mediumHaptic()
                                    feedback.playWinSound()
                                }
                            } else {
                                feedback.lossHaptic()
                                feedback.playLossSound()
                            }
                        }
                    }
                )
            }

        }

        SecondaryGameCard {
            Column(
                modifier = Modifier.padding(Spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                Text(
                    text = "Place Your Wager",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                OutlinedTextField(
                    value = wagerText,
                    onValueChange = { newValue ->
                        wagerText = newValue.filter { it.isDigit() }.take(10)
                    },
                    label = { Text("Wager Amount") },
                    placeholder = { Text("Enter coins to wager") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isFlipping,
                    singleLine = true,
                    suffix = { Text("coins", style = MaterialTheme.typography.bodyMedium) }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isFlipping) {
                        val quickBets = run {
                            val candidates = listOf(100L, 1_000L, coins / 4, coins / 2)
                            candidates.filter { it > 0 && it <= coins }.distinct().take(3)
                        }

                        quickBets.forEach { amount ->
                            androidx.compose.material3.OutlinedButton(
                                onClick = {
                                    feedback.lightHaptic(); feedback.playButtonClickSound(); wagerText =
                                    amount.toString()
                                },
                                enabled = amount <= coins,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(text = NumberFormatter.formatNumber(amount), fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    GambleButton(
                        text = "Flip Coin",
                        onClick = {
                            val wager = wagerText.toLongOrNull() ?: 0L
                            if (wager <= 0L) {
                                Toast.makeText(
                                    context,
                                    "Enter a positive wager",
                                    Toast.LENGTH_SHORT
                                ).show(); return@GambleButton
                            }
                            if (wager > coins) {
                                Toast.makeText(context, "Not enough coins", Toast.LENGTH_SHORT)
                                    .show(); return@GambleButton
                            }

                            feedback.coinFlipHaptic(); feedback.playCoinFlipSound()

                            isFlipping = true; showOutcome = false; previousBalance =
                            coins; deltaAmount = wager; flipOutcome = null; flipCount += 1

                            scope.launch {
                                gameViewModel.gamble(wager) { won, _ ->
                                    flipOutcome = won; deltaAmount = if (won) wager else -wager
                                    launchResultCleanup(
                                        coroutineScope = scope,
                                        won = won,
                                        wager = wager
                                    ) {
                                        isFlipping = false; wagerText = ""
                                    }
                                }
                            }
                        },
                        enabled = !isFlipping && wagerText.toLongOrNull()
                            ?.let { it > 0 && it <= coins } == true && flipOutcome == null,
                        isProcessing = isFlipping,
                        amount = null
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            AnimatedVisibility(
                visible = showOutcome,
                enter = fadeIn(animationSpec = tween(500)) + scaleIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(300)) + scaleOut(animationSpec = tween(300)),
                modifier = Modifier.weight(1f)
            ) {
                SecondaryGameCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (didWin) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer)
                            .padding(Spacing.medium), contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
                        ) {
                            Text(
                                text = if (didWin) "🎉 WON!" else "💸 LOST",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (didWin) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                            )
                            val sign = if (deltaAmount >= 0L) "+" else ""
                            Text(
                                text = "$sign${NumberFormatter.formatNumber(deltaAmount)}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = if (didWin) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            if (!showOutcome) {
                SecondaryGameCard(modifier = Modifier.weight(1f)) {
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
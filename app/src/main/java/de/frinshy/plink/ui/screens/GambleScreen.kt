package de.frinshy.plink.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.frinshy.plink.ui.components.CoinGraphic
import de.frinshy.plink.ui.components.PrimaryGameCard
import de.frinshy.plink.ui.theme.Spacing
import de.frinshy.plink.utils.NumberFormatter
import de.frinshy.plink.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GambleScreen(
    gameViewModel: GameViewModel = viewModel()
) {
    val uiState by gameViewModel.uiState.collectAsState()
    val coins = uiState.gameState.coins
    val context = LocalContext.current

    var wagerText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var resultWon by remember { mutableStateOf(false) }
    var resultDelta by remember { mutableStateOf(0L) }
    var playTrigger by remember { mutableStateOf(0) }

    // Animatables for coin spin/scale
    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }

    PrimaryGameCard {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            CoinGraphic(size = 96.dp)

            Text(text = "Gamble", modifier = Modifier.padding(top = 8.dp))

            Text(text = "Balance: ${NumberFormatter.formatNumber(coins)}")
            OutlinedTextField(
                value = wagerText,
                onValueChange = { wagerText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Wager (coins)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(
                onClick = {
                    val wager = wagerText.toLongOrNull() ?: 0L
                    if (wager <= 0L) {
                        Toast.makeText(context, "Enter a positive wager", Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    if (wager > coins) {
                        Toast.makeText(context, "Not enough coins", Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }

                    // Start animation flow
                    isProcessing = true
                    showResult = false
                    // record previous balance to compute delta
                    val prev = coins
                    gameViewModel.gamble(wager) { won, newBalance ->
                        // compute delta (positive if won, negative if lost)
                        val delta = newBalance - prev
                        resultWon = won
                        resultDelta = delta
                        // trigger the composable-scoped animation
                        playTrigger += 1

                        // also show a quick toast
                        val message = if (won) "You won! New balance: ${
                            NumberFormatter.formatNumber(newBalance)
                        }" else "You lost! New balance: ${NumberFormatter.formatNumber(newBalance)}"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isProcessing
            ) {
                Text("Play")
            }

            // Run animations in composable scope when playTrigger increments
            LaunchedEffect(playTrigger) {
                if (playTrigger <= 0) return@LaunchedEffect
                // reset states
                showResult = false
                // quick coin spin
                rotation.animateTo(720f, animationSpec = tween(700, easing = LinearEasing))
                scale.animateTo(1.25f, animationSpec = tween(250, easing = FastOutSlowInEasing))
                scale.animateTo(1f, animationSpec = spring())
                showResult = true
                // small pause so result is visible before resetting processing
                kotlinx.coroutines.delay(900)
                isProcessing = false
                wagerText = ""
                // reset rotation for next play
                rotation.snapTo(0f)
            }

            // Animated result text
            AnimatedVisibility(
                visible = showResult,
                enter = fadeIn(animationSpec = tween(300)) + scaleIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(200)) + scaleOut(animationSpec = tween(200))
            ) {
                val color = if (resultWon) MaterialTheme.colorScheme.primary else Color.Red
                val sign = if (resultDelta >= 0L) "+" else ""
                Text(
                    text = "$sign${NumberFormatter.formatNumber(resultDelta.coerceAtLeast(0L))} ${if (resultWon) "won" else "lost"}",
                    color = color,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
                )
            }
        }
    }
}

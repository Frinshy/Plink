package de.frinshy.plink.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Utility class for managing haptic feedback and audio cues throughout the game.
 * Provides standardized feedback for different game actions and events.
 */
class GameFeedbackManager(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * Light haptic feedback for button presses and UI interactions
     */
    fun lightHaptic() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(50)
            }
        }
    }

    /**
     * Medium haptic feedback for successful actions
     */
    fun mediumHaptic() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(100)
            }
        }
    }

    /**
     * Strong haptic feedback for big wins or important events
     */
    fun strongHaptic() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(200)
            }
        }
    }

    /**
     * Celebration haptic pattern for big wins
     */
    fun celebrationHaptic() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, 100, 100, 150, 100, 100)
                val amplitudes = intArrayOf(0, 120, 0, 180, 0, 120)
                vib.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                val pattern = longArrayOf(0, 100, 100, 150, 100, 100)
                vib.vibrate(pattern, -1)
            }
        }
    }

    /**
     * Coin flip haptic - simulates the physical sensation of a coin flip
     */
    fun coinFlipHaptic() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(80)
            }
        }
    }

    /**
     * Loss haptic - gentle feedback for negative outcomes
     */
    fun lossHaptic() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, 80, 50, 80)
                val amplitudes = intArrayOf(0, 100, 0, 60)
                vib.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                val pattern = longArrayOf(0, 80, 50, 80)
                vib.vibrate(pattern, -1)
            }
        }
    }

    // Audio hooks - these would be implemented when audio system is added
    fun playCoinFlipSound() {
        // TODO: Implement coin flip sound effect
        // Could use MediaPlayer or SoundPool for sound effects
    }

    fun playWinSound() {
        // TODO: Implement win sound effect
        // Celebration chime or positive audio cue
    }

    fun playLossSound() {
        // TODO: Implement loss sound effect
        // Gentle negative audio cue
    }

    fun playBigWinSound() {
        // TODO: Implement big win sound effect
        // Extended celebration with fanfare
    }

    fun playButtonClickSound() {
        // TODO: Implement button click sound
        // Subtle UI feedback sound
    }
}

/**
 * Composable helper to get a GameFeedbackManager instance
 */
@Composable
fun rememberGameFeedbackManager(): GameFeedbackManager {
    val context = LocalContext.current
    return remember { GameFeedbackManager(context) }
}
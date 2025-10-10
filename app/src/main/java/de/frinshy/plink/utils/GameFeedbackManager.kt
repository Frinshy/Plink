package de.frinshy.plink.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext


@Suppress("DEPRECATION")
class GameFeedbackManager(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun lightHaptic() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vib.vibrate(50)
            }
        }
    }


    fun mediumHaptic() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vib.vibrate(100)
            }
        }
    }

    fun celebrationHaptic() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, 100, 100, 150, 100, 100)
                val amplitudes = intArrayOf(0, 120, 0, 180, 0, 120)
                vib.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
            } else {
                val pattern = longArrayOf(0, 100, 100, 150, 100, 100)
                vib.vibrate(pattern, -1)
            }
        }
    }

    fun coinFlipHaptic() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vib.vibrate(80)
            }
        }
    }

    fun lossHaptic() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, 80, 50, 80)
                val amplitudes = intArrayOf(0, 100, 0, 60)
                vib.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
            } else {
                val pattern = longArrayOf(0, 80, 50, 80)
                vib.vibrate(pattern, -1)
            }
        }
    }

    fun playCoinFlipSound() {

    }

    fun playWinSound() {

    }

    fun playLossSound() {

    }

    fun playBigWinSound() {

    }

    fun playButtonClickSound() {

    }
}


@Composable
fun rememberGameFeedbackManager(): GameFeedbackManager {
    val context = LocalContext.current
    return remember { GameFeedbackManager(context) }
}
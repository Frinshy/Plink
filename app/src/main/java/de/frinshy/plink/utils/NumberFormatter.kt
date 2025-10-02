package de.frinshy.plink.utils

import java.text.NumberFormat
import java.util.Locale

/**
 * Utility object for formatting numbers in the game.
 * Provides consistent number formatting across the entire app.
 */
object NumberFormatter {

    /**
     * Format large numbers for display with better formatting.
     *
     * @param number The number to format
     * @return Formatted string (e.g., "1.2K", "3.4M", "5.6B", "7.8T")
     */
    fun formatNumber(number: Long): String {
        return when {
            number >= 1_000_000_000_000 -> String.format(
                Locale.getDefault(),
                "%.1fT",
                number / 1_000_000_000_000.0
            )

            number >= 1_000_000_000 -> String.format(
                Locale.getDefault(),
                "%.1fB",
                number / 1_000_000_000.0
            )

            number >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM", number / 1_000_000.0)
            number >= 1_000 -> String.format(Locale.getDefault(), "%.1fK", number / 1_000.0)
            else -> NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
        }
    }

    /**
     * Format numbers with full precision for detailed views.
     *
     * @param number The number to format
     * @return Formatted string with proper thousand separators
     */
    fun formatNumberPrecise(number: Long): String {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
    }

    /**
     * Format currency values with consistent precision.
     *
     * @param amount The amount to format
     * @return Formatted currency string
     */
    fun formatCurrency(amount: Long): String {
        return formatNumber(amount) + " coins"
    }

    /**
     * Format percentages for upgrade displays.
     *
     * @param percentage The percentage as a decimal (e.g., 0.15 for 15%)
     * @return Formatted percentage string (e.g., "15%")
     */
    fun formatPercentage(percentage: Double): String {
        return String.format(Locale.getDefault(), "%.1f%%", percentage * 100)
    }

    /**
     * Format time duration in seconds to human-readable format.
     *
     * @param seconds The duration in seconds
     * @return Formatted time string (e.g., "2m 30s", "1h 15m", "3d 2h")
     */
    fun formatDuration(seconds: Long): String {
        return when {
            seconds < 60 -> "${seconds}s"
            seconds < 3600 -> {
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                if (remainingSeconds > 0) "${minutes}m ${remainingSeconds}s" else "${minutes}m"
            }

            seconds < 86400 -> {
                val hours = seconds / 3600
                val remainingMinutes = (seconds % 3600) / 60
                if (remainingMinutes > 0) "${hours}h ${remainingMinutes}m" else "${hours}h"
            }

            else -> {
                val days = seconds / 86400
                val remainingHours = (seconds % 86400) / 3600
                if (remainingHours > 0) "${days}d ${remainingHours}h" else "${days}d"
            }
        }
    }
}
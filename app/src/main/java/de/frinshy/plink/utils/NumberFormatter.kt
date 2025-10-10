package de.frinshy.plink.utils

import java.text.NumberFormat
import java.util.Locale

object NumberFormatter {

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
}
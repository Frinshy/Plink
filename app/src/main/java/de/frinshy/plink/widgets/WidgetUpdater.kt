package de.frinshy.plink.widgets

import android.content.Context
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Small helper to trigger Glance widget updates from app code.
 * Keeps the update call in one place so callers don't need to reference
 * Glance classes directly.
 */
object WidgetUpdater {
    /**
     * Update all instances of the CoinsWidget. This calls into Glance's
     * updateAll which will schedule a worker if necessary.
     */
    suspend fun updateAllCoins(context: Context) {
        // run on IO dispatcher since Glance update may perform I/O
        withContext(Dispatchers.IO) {
            CoinsWidget().updateAll(context)
        }
    }
}

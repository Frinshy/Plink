package de.frinshy.plink.widgets

import android.content.Context
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object WidgetUpdater {

    suspend fun updateAllCoins(context: Context) {
        withContext(Dispatchers.IO) {
            CoinsWidget().updateAll(context)
        }
    }
}

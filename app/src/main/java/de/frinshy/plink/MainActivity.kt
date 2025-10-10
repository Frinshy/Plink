package de.frinshy.plink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import de.frinshy.plink.ui.AppRoot
import de.frinshy.plink.viewmodel.GameViewModel
import de.frinshy.plink.widgets.WidgetUpdater
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            AppRoot(gameViewModel = gameViewModel, application = application)
        }
    }

    override fun onResume() {
        super.onResume()


        lifecycleScope.launch {
            WidgetUpdater.updateAllCoins(applicationContext)
        }
    }
}
package de.frinshy.plink.ui

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.frinshy.plink.PlinkNavigation
import de.frinshy.plink.Routes
import de.frinshy.plink.data.SettingsRepository
import de.frinshy.plink.data.ThemeMode
import de.frinshy.plink.ui.components.GameTopAppBar
import de.frinshy.plink.ui.theme.PlinkTheme
import de.frinshy.plink.utils.GameFeedbackManager
import de.frinshy.plink.viewmodel.GameViewModel
import kotlinx.coroutines.launch

@Composable
fun AppRoot(gameViewModel: GameViewModel, application: Application) {
    val settings = remember { SettingsRepository(application.applicationContext) }
    val themeMode by settings.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val debugEnabled by settings.isDebugMenuEnabled.collectAsState(initial = false)

    var titleTaps by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val feedback = remember { GameFeedbackManager(ctx) }

    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    PlinkTheme(darkTheme = dark) {
        val navController = rememberNavController()
        val backEntry by navController.currentBackStackEntryAsState()
        val route = backEntry?.destination?.route

        Scaffold(
            topBar = {
                val title = when (route) {
                    Routes.MAIN -> "Plink"
                    Routes.SHOP -> "Shop"
                    Routes.GAMBLE -> "Gamble"
                    Routes.SETTINGS -> "Settings"
                    Routes.DEBUG -> "Debug"
                    else -> "Plink"
                }

                GameTopAppBar(title = title, onTitleClick = {
                    titleTaps++
                    feedback.lightHaptic()
                    if (titleTaps >= 7) {
                        titleTaps = 0
                        feedback.mediumHaptic()
                        scope.launch {
                            val prev = debugEnabled
                            settings.toggleDebugMenu()
                            val msg =
                                if (!prev) "Debug menu enabled! Check the bottom navigation." else "Debug menu disabled."
                            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            },
            bottomBar = {
                BottomNavBar(
                    navController = navController,
                    currentRoute = route,
                    showDebugMenu = debugEnabled
                )
            }
        ) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) { PlinkNavigation(navController = navController, gameViewModel = gameViewModel) }
        }
    }
}

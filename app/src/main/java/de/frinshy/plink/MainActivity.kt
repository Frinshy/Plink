package de.frinshy.plink

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.frinshy.plink.data.SettingsRepository
import de.frinshy.plink.data.ThemeMode
import de.frinshy.plink.navigation.PlinkNavigation
import de.frinshy.plink.navigation.PlinkRoutes
import de.frinshy.plink.ui.components.GameTopAppBar
import de.frinshy.plink.ui.theme.PlinkTheme
import de.frinshy.plink.utils.GameFeedbackManager
import de.frinshy.plink.viewmodel.GameViewModel
import de.frinshy.plink.widgets.WidgetUpdater
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Settings repository for persisted UI preferences
            val settingsRepo = SettingsRepository(applicationContext)
            val themeMode by settingsRepo.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val isDebugMenuEnabled by settingsRepo.isDebugMenuEnabled.collectAsState(initial = false)

            // Remember tap counter and coroutine scope for debug toggle
            var titleTapCount by remember { mutableStateOf(0) }
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            val feedbackManager = remember { GameFeedbackManager(context) }

            // Resolve boolean for PlinkTheme: when SYSTEM, use system setting
            val useDark = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }

            PlinkTheme(darkTheme = useDark) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                // Always render content below the top app bar (no overlap)
                application?.applicationInfo?.flags?.and(ApplicationInfo.FLAG_DEBUGGABLE) != 0
                // Show debug menu only if user has enabled it via hidden shortcut (always require 7-tap activation)
                val showDebugMenu = isDebugMenuEnabled
                val uiState by gameViewModel.uiState.collectAsState()
                val isLoading = uiState.isLoading

                Scaffold(
                    topBar = {
                        // Map current navigation route to a simple title
                        val title = when (currentRoute) {
                            PlinkRoutes.MAIN -> "Plink"
                            PlinkRoutes.SHOP -> "Shop"
                            PlinkRoutes.GAMBLE -> "Gamble"
                            PlinkRoutes.SETTINGS -> "Settings"
                            PlinkRoutes.DEBUG -> "Debug"
                            else -> "Plink"
                        }

                        GameTopAppBar(
                            title = title,
                            onTitleClick = {
                                // Hidden debug toggle: 7 taps on title
                                titleTapCount++
                                feedbackManager.lightHaptic()

                                if (titleTapCount >= 7) {
                                    titleTapCount = 0
                                    feedbackManager.mediumHaptic()
                                    coroutineScope.launch {
                                        val currentState = isDebugMenuEnabled
                                        settingsRepo.toggleDebugMenu()
                                        val newState = !currentState
                                        val message = if (newState) {
                                            "Debug menu enabled! Check the bottom navigation."
                                        } else {
                                            "Debug menu disabled."
                                        }
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentRoute == PlinkRoutes.MAIN,
                                onClick = {
                                    navController.navigate(PlinkRoutes.MAIN) {
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                label = { Text("Home") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == PlinkRoutes.SHOP,
                                onClick = {
                                    navController.navigate(PlinkRoutes.SHOP) {
                                        launchSingleTop = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = null
                                    )
                                },
                                label = { Text("Shop") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == PlinkRoutes.GAMBLE,
                                onClick = {
                                    navController.navigate(PlinkRoutes.GAMBLE) {
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Star, contentDescription = null) },
                                label = { Text("Gamble") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == PlinkRoutes.SETTINGS,
                                onClick = {
                                    navController.navigate(PlinkRoutes.SETTINGS) {
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Build, contentDescription = null) },
                                label = { Text("Settings") }
                            )

                            // Show debug item if debug build OR if manually enabled
                            if (showDebugMenu) {
                                NavigationBarItem(
                                    selected = currentRoute == PlinkRoutes.DEBUG,
                                    onClick = {
                                        navController.navigate(PlinkRoutes.DEBUG) {
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            Icons.Default.BugReport,
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text("Debug") }
                                )
                            }
                        }
                    }
                ) { padding ->
                    // Apply scaffold insets
                    val start = padding.calculateLeftPadding(layoutDirection = LayoutDirection.Ltr)
                    val end = padding.calculateRightPadding(layoutDirection = LayoutDirection.Ltr)
                    val bottom = padding.calculateBottomPadding()
                    val top = padding.calculateTopPadding()

                    // Always apply status bar and top padding so the top app bar
                    // occupies space and content is laid out below it.
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = start,
                                end = end,
                                top = top,
                                bottom = bottom
                            )
                    ) {
                        // Main navigation host
                        PlinkNavigation(
                            navController = navController,
                            gameViewModel = gameViewModel
                        )
                        // Full-screen loading overlay when app isLoading
                        if (isLoading) {
                            // Overlay scrim + centered progress
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Loading...",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // When returning to the app, refresh widgets so they reflect any
        // changes that may have occurred while the app was backgrounded.
        lifecycleScope.launch {
            WidgetUpdater.updateAllCoins(applicationContext)
        }
    }
}
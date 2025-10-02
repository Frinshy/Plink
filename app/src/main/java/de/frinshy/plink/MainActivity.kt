package de.frinshy.plink

import android.content.pm.ApplicationInfo
import android.os.Bundle
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.frinshy.plink.data.SettingsRepository
import de.frinshy.plink.data.ThemeMode
import de.frinshy.plink.navigation.PlinkNavigation
import de.frinshy.plink.navigation.PlinkRoutes
import de.frinshy.plink.ui.theme.PlinkTheme
import de.frinshy.plink.viewmodel.GameViewModel

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
                val isDebugBuild =
                    application?.applicationInfo?.flags?.and(ApplicationInfo.FLAG_DEBUGGABLE) != 0
                val uiState by gameViewModel.uiState.collectAsState()
                val isLoading = uiState.isLoading

                Scaffold(
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
                                selected = currentRoute == PlinkRoutes.SETTINGS,
                                onClick = {
                                    navController.navigate(PlinkRoutes.SETTINGS) {
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Build, contentDescription = null) },
                                label = { Text("Settings") }
                            )

                            // Show debug item only in debug builds
                            if (isDebugBuild) {
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
                    val start =
                        padding.calculateLeftPadding(layoutDirection = LayoutDirection.Ltr)
                    val end =
                        padding.calculateRightPadding(layoutDirection = LayoutDirection.Ltr)
                    val bottom = padding.calculateBottomPadding()

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(start = start, end = end, bottom = bottom)
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
}
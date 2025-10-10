package de.frinshy.plink

import android.app.Application
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.frinshy.plink.data.SettingsRepository
import de.frinshy.plink.ui.screens.DebugScreen
import de.frinshy.plink.ui.screens.GambleScreen
import de.frinshy.plink.ui.screens.MainScreen
import de.frinshy.plink.ui.screens.SettingsScreen
import de.frinshy.plink.ui.screens.ShopScreen
import de.frinshy.plink.viewmodel.GameViewModel

object Routes {
    const val MAIN = "main"
    const val SHOP = "shop"
    const val DEBUG = "debug"
    const val SETTINGS = "settings"
    const val GAMBLE = "gamble"
}

private object AppTransitions {
    private const val SLIDE_DURATION = 280
    private const val TAB_DURATION = 200

    private val tabRoutes =
        setOf(Routes.MAIN, Routes.SHOP, Routes.DEBUG, Routes.GAMBLE, Routes.SETTINGS)

    val slideIn = slideInHorizontally { it } + fadeIn(tween(SLIDE_DURATION))
    val slideOut = slideOutHorizontally { -it } + fadeOut(tween(SLIDE_DURATION))
    val slideBack = slideInHorizontally { -it } + fadeIn(tween(SLIDE_DURATION))
    val slideBackOut = slideOutHorizontally { it } + fadeOut(tween(SLIDE_DURATION))

    val tabIn = fadeIn(tween(TAB_DURATION)) + scaleIn(
        initialScale = 0.96f,
        animationSpec = tween(TAB_DURATION)
    )
    val tabOut = fadeOut(tween(TAB_DURATION)) + scaleOut(
        targetScale = 0.96f,
        animationSpec = tween(TAB_DURATION)
    )

    fun isTabNavigation(from: String?, to: String?) = from in tabRoutes && to in tabRoutes
}

@Composable
fun PlinkNavigation(
    navController: NavHostController,
    gameViewModel: GameViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MAIN,
        enterTransition = { AppTransitions.slideIn },
        exitTransition = { AppTransitions.slideOut },
        popEnterTransition = { AppTransitions.slideBack },
        popExitTransition = { AppTransitions.slideBackOut }
    ) {
        composable(
            route = Routes.MAIN,
            enterTransition = {
                if (AppTransitions.isTabNavigation(
                        initialState.destination.route,
                        targetState.destination.route
                    )
                ) {
                    AppTransitions.tabIn
                } else AppTransitions.slideIn
            },
            exitTransition = {
                if (AppTransitions.isTabNavigation(
                        initialState.destination.route,
                        targetState.destination.route
                    )
                ) {
                    AppTransitions.tabOut
                } else AppTransitions.slideBackOut
            }
        ) {
            MainScreen(
                onNavigateToShop = {
                    navController.navigate(Routes.SHOP) { launchSingleTop = true }
                },
                gameViewModel = gameViewModel
            )
        }

        composable(Routes.GAMBLE) {
            GambleScreen(gameViewModel = gameViewModel)
        }

        composable(Routes.SHOP) {
            ShopScreen(
                gameViewModel = gameViewModel
            )
        }

        composable(Routes.DEBUG) {
            DebugScreen(gameViewModel = gameViewModel)
        }

        composable(Routes.SETTINGS) {
            val context = gameViewModel.getApplication<Application>().applicationContext
            SettingsScreen(settingsRepository = SettingsRepository(context))
        }
    }
}
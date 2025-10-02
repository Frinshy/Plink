package de.frinshy.plink.navigation

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
import de.frinshy.plink.ui.screens.MainScreen
import de.frinshy.plink.ui.screens.SettingsScreen
import de.frinshy.plink.ui.screens.ShopScreen
import de.frinshy.plink.viewmodel.GameViewModel

/**
 * Navigation routes for the Plink app
 */
object PlinkRoutes {
    const val MAIN = "main"
    const val SHOP = "shop"
    const val DEBUG = "debug"
    const val SETTINGS = "settings"
}

/**
 * Material 3 navigation animations
 */
private const val TRANSITION_DURATION = 300

private val slideInFromRight = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(TRANSITION_DURATION)
) + fadeIn(animationSpec = tween(TRANSITION_DURATION))

private val slideOutToLeft = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(TRANSITION_DURATION)
) + fadeOut(animationSpec = tween(TRANSITION_DURATION))

private val slideInFromLeft = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(TRANSITION_DURATION)
) + fadeIn(animationSpec = tween(TRANSITION_DURATION))

private val slideOutToRight = slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(TRANSITION_DURATION)
) + fadeOut(animationSpec = tween(TRANSITION_DURATION))

private val bottomNavRoutes = setOf(
    PlinkRoutes.MAIN,
    PlinkRoutes.SHOP,
    PlinkRoutes.DEBUG,
    PlinkRoutes.SETTINGS
)

private val bottomEnter =
    fadeIn(animationSpec = tween(150)) + scaleIn(initialScale = 0.98f, animationSpec = tween(150))
private val bottomExit =
    fadeOut(animationSpec = tween(120)) + scaleOut(targetScale = 0.98f, animationSpec = tween(120))

/**
 * Main navigation host for the Plink app with Material 3 transitions
 */
@Composable
fun PlinkNavigation(
    navController: NavHostController,
    gameViewModel: GameViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = PlinkRoutes.MAIN,
        enterTransition = { slideInFromRight },
        exitTransition = { slideOutToLeft },
        popEnterTransition = { slideInFromLeft },
        popExitTransition = { slideOutToRight }
    ) {
        composable(
            route = PlinkRoutes.MAIN,
            enterTransition = {
                // If switching between bottom-nav routes, use a subtle fade; otherwise slide
                when (initialState.destination.route) {
                    in bottomNavRoutes -> if (targetState.destination.route in bottomNavRoutes) bottomEnter else slideInFromRight
                    else -> slideInFromRight
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    in bottomNavRoutes -> if (initialState.destination.route in bottomNavRoutes) bottomExit else slideOutToRight
                    else -> slideOutToRight
                }
            }
        ) {
            MainScreen(
                onNavigateToShop = {
                    navController.navigate(PlinkRoutes.SHOP) {
                        launchSingleTop = true
                    }
                },
                gameViewModel = gameViewModel
            )
        }

        composable(
            route = PlinkRoutes.SHOP,
            enterTransition = {
                when (initialState.destination.route) {
                    in bottomNavRoutes -> if (targetState.destination.route in bottomNavRoutes) bottomEnter else slideInFromRight
                    else -> slideInFromRight
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    in bottomNavRoutes -> if (initialState.destination.route in bottomNavRoutes) bottomExit else slideOutToRight
                    else -> slideOutToRight
                }
            },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            ShopScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                gameViewModel = gameViewModel
            )
        }

        composable(
            route = PlinkRoutes.DEBUG,
            enterTransition = {
                when (initialState.destination.route) {
                    in bottomNavRoutes -> if (targetState.destination.route in bottomNavRoutes) bottomEnter else slideInFromRight
                    else -> slideInFromRight
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    in bottomNavRoutes -> if (initialState.destination.route in bottomNavRoutes) bottomExit else slideOutToRight
                    else -> slideOutToRight
                }
            },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            DebugScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                gameViewModel = gameViewModel
            )
        }

        composable(
            route = PlinkRoutes.SETTINGS,
            enterTransition = {
                when (initialState.destination.route) {
                    in bottomNavRoutes -> if (targetState.destination.route in bottomNavRoutes) bottomEnter else slideInFromRight
                    else -> slideInFromRight
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    in bottomNavRoutes -> if (initialState.destination.route in bottomNavRoutes) bottomExit else slideOutToRight
                    else -> slideOutToRight
                }
            },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            // Provide SettingsRepository using application context via GameViewModel's application
            val context = gameViewModel.getApplication<Application>().applicationContext
            val settingsRepo = SettingsRepository(context)
            SettingsScreen(
                settingsRepository = settingsRepo
            )
        }
    }
}
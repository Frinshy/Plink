package de.frinshy.plink.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import de.frinshy.plink.Routes

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String?, showDebugMenu: Boolean) {
    NavigationBar {
        fun navigateTo(route: String): Unit = navController.navigate(route)

        NavigationBarItem(
            selected = currentRoute == Routes.MAIN,
            onClick = { navigateTo(Routes.MAIN) },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentRoute == Routes.SHOP,
            onClick = { navigateTo(Routes.SHOP) },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
            label = { Text("Shop") }
        )

        NavigationBarItem(
            selected = currentRoute == Routes.GAMBLE,
            onClick = { navigateTo(Routes.GAMBLE) },
            icon = { Icon(Icons.Default.Star, contentDescription = null) },
            label = { Text("Gamble") }
        )

        NavigationBarItem(
            selected = currentRoute == Routes.SETTINGS,
            onClick = { navigateTo(Routes.SETTINGS) },
            icon = { Icon(Icons.Default.Build, contentDescription = null) },
            label = { Text("Settings") }
        )

        if (showDebugMenu) {
            NavigationBarItem(
                selected = currentRoute == Routes.DEBUG,
                onClick = { navigateTo(Routes.DEBUG) },
                icon = { Icon(Icons.Default.BugReport, contentDescription = null) },
                label = { Text("Debug") }
            )
        }
    }
}

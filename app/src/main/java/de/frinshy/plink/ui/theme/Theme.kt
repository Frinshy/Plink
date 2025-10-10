package de.frinshy.plink.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color.primaryLight,
    onPrimary = Color.onPrimaryLight,
    primaryContainer = Color.primaryContainerLight,
    onPrimaryContainer = Color.onPrimaryContainerLight,

    secondary = Color.secondaryLight,
    onSecondary = Color.onSecondaryLight,
    secondaryContainer = Color.secondaryContainerLight,
    onSecondaryContainer = Color.onSecondaryContainerLight,

    tertiary = Color.tertiaryLight,
    onTertiary = Color.onTertiaryLight,
    tertiaryContainer = Color.tertiaryContainerLight,
    onTertiaryContainer = Color.onTertiaryContainerLight,

    error = Color.errorLight,
    onError = Color.onErrorLight,
    errorContainer = Color.errorContainerLight,
    onErrorContainer = Color.onErrorContainerLight,

    background = Color.backgroundLight,
    onBackground = Color.onSurfaceLight,

    surface = Color.surfaceLight,
    onSurface = Color.onSurfaceLight,
    surfaceVariant = Color.surfaceContainerLight,
    onSurfaceVariant = Color.onSurfaceVariantLight,

    surfaceContainer = Color.surfaceContainerLight,
    surfaceContainerHigh = Color.surfaceContainerHighLight,
    surfaceContainerHighest = Color.surfaceContainerHighestLight,

    outline = Color.outlineLight,
    outlineVariant = Color.outlineVariantLight,

    inverseSurface = Color.inverseSurfaceLight,
    inverseOnSurface = Color.inverseOnSurfaceLight,
    inversePrimary = Color.inversePrimaryLight,

    surfaceTint = Color.primaryLight,
    scrim = Color.onSurfaceLight.copy(alpha = 0.8f)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color.primaryDark,
    onPrimary = Color.onPrimaryDark,
    primaryContainer = Color.primaryContainerDark,
    onPrimaryContainer = Color.onPrimaryContainerDark,

    secondary = Color.secondaryDark,
    onSecondary = Color.onSecondaryDark,
    secondaryContainer = Color.secondaryContainerDark,
    onSecondaryContainer = Color.onSecondaryContainerDark,

    tertiary = Color.tertiaryDark,
    onTertiary = Color.onTertiaryDark,
    tertiaryContainer = Color.tertiaryContainerDark,
    onTertiaryContainer = Color.onTertiaryContainerDark,

    error = Color.errorDark,
    onError = Color.onErrorDark,
    errorContainer = Color.errorContainerDark,
    onErrorContainer = Color.onErrorContainerDark,

    background = Color.backgroundDark,
    onBackground = Color.onSurfaceDark,

    surface = Color.surfaceDark,
    onSurface = Color.onSurfaceDark,
    surfaceVariant = Color.surfaceContainerDark,
    onSurfaceVariant = Color.onSurfaceVariantDark,

    surfaceContainer = Color.surfaceContainerDark,
    surfaceContainerHigh = Color.surfaceContainerHighDark,
    surfaceContainerHighest = Color.surfaceContainerHighestDark,

    outline = Color.outlineDark,
    outlineVariant = Color.outlineVariantDark,

    inverseSurface = Color.inverseSurfaceDark,
    inverseOnSurface = Color.inverseOnSurfaceDark,
    inversePrimary = Color.inversePrimaryDark,

    surfaceTint = Color.primaryDark,
    scrim = Color.onSurfaceDark.copy(alpha = 0.8f)
)

@Composable
fun PlinkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TextStyle.Typography,
        shapes = gameShapes,
        content = content
    )
}
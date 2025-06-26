package com.serova.parkingapp.presentation.ui.theme

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.serova.parkingapp.domain.model.settings.AppTheme
import com.serova.parkingapp.domain.usecase.theme.GetThemeFlowUseCase

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFF018786),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFF018786),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun ParkingAppTheme(
    appTheme: AppTheme,
    getThemeFlowUseCase: GetThemeFlowUseCase,
    content: @Composable () -> Unit
) {
    val currentAppTheme by getThemeFlowUseCase().collectAsState(appTheme)
    val darkTheme = when (currentAppTheme) {
        AppTheme.SYSTEM -> isSystemInDarkThemeExtended()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }

    val transition = updateTransition(targetState = darkTheme, label = "ThemeTransition")
    val colorScheme = transition.animateColorScheme()

    val view = LocalView.current
    val window = (view.context as Activity).window

    LaunchedEffect(colorScheme) {
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
private fun Transition<Boolean>.animateColorScheme(): ColorScheme {

    // region Primary colors

    val primary by animateColor("primary") { isDark ->
        if (isDark) DarkColorScheme.primary else LightColorScheme.primary
    }
    val onPrimary by animateColor("onPrimary") { isDark ->
        if (isDark) DarkColorScheme.onPrimary else LightColorScheme.onPrimary
    }
    val primaryContainer by animateColor("primaryContainer") { isDark ->
        if (isDark) DarkColorScheme.primaryContainer else LightColorScheme.primaryContainer
    }
    val onPrimaryContainer by animateColor("onPrimaryContainer") { isDark ->
        if (isDark) DarkColorScheme.onPrimaryContainer else LightColorScheme.onPrimaryContainer
    }
    val inversePrimary by animateColor("inversePrimary") { isDark ->
        if (isDark) DarkColorScheme.inversePrimary else LightColorScheme.inversePrimary
    }

    // endregion

    // region Secondary colors

    val secondary by animateColor("secondary") { isDark ->
        if (isDark) DarkColorScheme.secondary else LightColorScheme.secondary
    }
    val onSecondary by animateColor("onSecondary") { isDark ->
        if (isDark) DarkColorScheme.onSecondary else LightColorScheme.onSecondary
    }
    val secondaryContainer by animateColor("secondaryContainer") { isDark ->
        if (isDark) DarkColorScheme.secondaryContainer else LightColorScheme.secondaryContainer
    }
    val onSecondaryContainer by animateColor("onSecondaryContainer") { isDark ->
        if (isDark) DarkColorScheme.onSecondaryContainer else LightColorScheme.onSecondaryContainer
    }

    // endregion

    // region Tertiary colors

    val tertiary by animateColor("tertiary") { isDark ->
        if (isDark) DarkColorScheme.tertiary else LightColorScheme.tertiary
    }
    val onTertiary by animateColor("onTertiary") { isDark ->
        if (isDark) DarkColorScheme.onTertiary else LightColorScheme.onTertiary
    }
    val tertiaryContainer by animateColor("tertiaryContainer") { isDark ->
        if (isDark) DarkColorScheme.tertiaryContainer else LightColorScheme.tertiaryContainer
    }
    val onTertiaryContainer by animateColor("onTertiaryContainer") { isDark ->
        if (isDark) DarkColorScheme.onTertiaryContainer else LightColorScheme.onTertiaryContainer
    }

    // endregion

    // region Background and surface colors

    val background by animateColor("background") { isDark ->
        if (isDark) DarkColorScheme.background else LightColorScheme.background
    }
    val onBackground by animateColor("onBackground") { isDark ->
        if (isDark) DarkColorScheme.onBackground else LightColorScheme.onBackground
    }
    val surface by animateColor("surface") { isDark ->
        if (isDark) DarkColorScheme.surface else LightColorScheme.surface
    }
    val onSurface by animateColor("onSurface") { isDark ->
        if (isDark) DarkColorScheme.onSurface else LightColorScheme.onSurface
    }
    val surfaceVariant by animateColor("surfaceVariant") { isDark ->
        if (isDark) DarkColorScheme.surfaceVariant else LightColorScheme.surfaceVariant
    }
    val onSurfaceVariant by animateColor("onSurfaceVariant") { isDark ->
        if (isDark) DarkColorScheme.onSurfaceVariant else LightColorScheme.onSurfaceVariant
    }
    val surfaceTint by animateColor("surfaceTint") { isDark ->
        if (isDark) DarkColorScheme.surfaceTint else LightColorScheme.surfaceTint
    }

    // endregion

    // region Inverse colors

    val inverseSurface by animateColor("inverseSurface") { isDark ->
        if (isDark) DarkColorScheme.inverseSurface else LightColorScheme.inverseSurface
    }
    val inverseOnSurface by animateColor("inverseOnSurface") { isDark ->
        if (isDark) DarkColorScheme.inverseOnSurface else LightColorScheme.inverseOnSurface
    }

    // endregion

    // region Error colors

    val error by animateColor("error") { isDark ->
        if (isDark) DarkColorScheme.error else LightColorScheme.error
    }
    val onError by animateColor("onError") { isDark ->
        if (isDark) DarkColorScheme.onError else LightColorScheme.onError
    }
    val errorContainer by animateColor("errorContainer") { isDark ->
        if (isDark) DarkColorScheme.errorContainer else LightColorScheme.errorContainer
    }
    val onErrorContainer by animateColor("onErrorContainer") { isDark ->
        if (isDark) DarkColorScheme.onErrorContainer else LightColorScheme.onErrorContainer
    }

    // endregion

    // region Outline and scrim

    val outline by animateColor("outline") { isDark ->
        if (isDark) DarkColorScheme.outline else LightColorScheme.outline
    }
    val outlineVariant by animateColor("outlineVariant") { isDark ->
        if (isDark) DarkColorScheme.outlineVariant else LightColorScheme.outlineVariant
    }
    val scrim by animateColor("scrim") { isDark ->
        if (isDark) DarkColorScheme.scrim else LightColorScheme.scrim
    }

    // endregion

    // region Surface container colors
    val surfaceDim by animateColor("surfaceDim") { isDark ->
        if (isDark) DarkColorScheme.surfaceDim else LightColorScheme.surfaceDim
    }
    val surfaceBright by animateColor("surfaceBright") { isDark ->
        if (isDark) DarkColorScheme.surfaceBright else LightColorScheme.surfaceBright
    }
    val surfaceContainer by animateColor("surfaceContainer") { isDark ->
        if (isDark) DarkColorScheme.surfaceContainer else LightColorScheme.surfaceContainer
    }
    val surfaceContainerHigh by animateColor("surfaceContainerHigh") { isDark ->
        if (isDark) DarkColorScheme.surfaceContainerHigh else LightColorScheme.surfaceContainerHigh
    }
    val surfaceContainerHighest by animateColor("surfaceContainerHighest") { isDark ->
        if (isDark) DarkColorScheme.surfaceContainerHighest else LightColorScheme.surfaceContainerHighest
    }
    val surfaceContainerLow by animateColor("surfaceContainerLow") { isDark ->
        if (isDark) DarkColorScheme.surfaceContainerLow else LightColorScheme.surfaceContainerLow
    }
    val surfaceContainerLowest by animateColor("surfaceContainerLowest") { isDark ->
        if (isDark) DarkColorScheme.surfaceContainerLowest else LightColorScheme.surfaceContainerLowest
    }

    // endregion

    return ColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        inversePrimary = inversePrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = onTertiaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        surfaceTint = surfaceTint,
        inverseSurface = inverseSurface,
        inverseOnSurface = inverseOnSurface,
        error = error,
        onError = onError,
        errorContainer = errorContainer,
        onErrorContainer = onErrorContainer,
        outline = outline,
        outlineVariant = outlineVariant,
        scrim = scrim,
        surfaceBright = surfaceBright,
        surfaceDim = surfaceDim,
        surfaceContainer = surfaceContainer,
        surfaceContainerHigh = surfaceContainerHigh,
        surfaceContainerHighest = surfaceContainerHighest,
        surfaceContainerLow = surfaceContainerLow,
        surfaceContainerLowest = surfaceContainerLowest
    )
}

@Composable
private fun Transition<Boolean>.animateColor(
    label: String,
    targetValueFromState: (Boolean) -> Color
) = animateColor(
    transitionSpec = { tween(durationMillis = 300) },
    label = label
) { targetValueFromState(it) }

@Composable
private fun isSystemInDarkThemeExtended(): Boolean {
    val config = LocalConfiguration.current
    return config.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}
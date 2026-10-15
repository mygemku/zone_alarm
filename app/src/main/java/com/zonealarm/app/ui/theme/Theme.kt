package com.zonealarm.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

/**
 * ZoneAlarm design system theme.
 * Auto-adjusts between Dark (OLED-friendly) and Light (paper-clean)
 * according to the Android system theme.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Amber500,
    onPrimary = OnAmberDark,
    primaryContainer = Amber900,
    onPrimaryContainer = Amber400,
    secondary = Indigo400,
    onSecondary = Slate950,
    secondaryContainer = Indigo900,
    onSecondaryContainer = Indigo100,
    tertiary = Amber400,
    onTertiary = OnAmberDark,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate400,
    outline = Slate700,
    outlineVariant = Slate800,
    error = Error,
    onError = Slate950,
)

private val LightColorScheme = lightColorScheme(
    primary = Amber700,
    onPrimary = OnAmberLight,
    primaryContainer = Amber100,
    onPrimaryContainer = Amber800,
    secondary = Indigo600,
    onSecondary = OnIndigo,
    secondaryContainer = Indigo100,
    onSecondaryContainer = Indigo900,
    tertiary = Amber600,
    onTertiary = OnAmberLight,
    background = Slate50,
    onBackground = Slate900,
    surface = Color(0xFFFFFFFF),
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate300,
    outlineVariant = Slate200,
    error = Error,
    onError = OnIndigo,
)

private val ZoneAlarmShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun ZoneAlarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZoneAlarmTypography,
        shapes = ZoneAlarmShapes,
        content = content,
    )
}

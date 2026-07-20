package com.example.medclerkmobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Teal700,
    onPrimary = Color.White,
    primaryContainer = Teal100,
    onPrimaryContainer = Teal800,
    secondary = Teal600,
    onSecondary = Color.White,
    secondaryContainer = Teal50,
    onSecondaryContainer = Teal800,
    background = Neutral50,
    onBackground = Neutral900,
    surface = Color.White,
    onSurface = Neutral900,
    surfaceVariant = Neutral100,
    onSurfaceVariant = Neutral500,
    outline = Neutral200,
    outlineVariant = Neutral100,
    error = Red600,
    onError = Color.White,
    errorContainer = Red100,
    onErrorContainer = Red700,
)

private val DarkColorScheme = darkColorScheme(
    primary = Teal400,
    onPrimary = Neutral950,
    primaryContainer = Teal800,
    onPrimaryContainer = Teal100,
    secondary = Teal400,
    background = Neutral950,
    onBackground = Neutral100,
    surface = Neutral900,
    onSurface = Neutral100,
    surfaceVariant = Neutral800,
    onSurfaceVariant = Neutral400,
    outline = Neutral700,
    error = Red600,
    onError = Color.White,
)

/**
 * Dynamic color (Material You) is intentionally not offered here: the app has a fixed
 * teal/neutral brand identity ported from the design prototype, and letting the system
 * wallpaper palette override it would defeat that.
 */
@Composable
fun MedClerkMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = MedClerkShapes,
        content = content
    )
}

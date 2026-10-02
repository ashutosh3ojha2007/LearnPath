package com.edustudycraft.newdemoappl.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    primaryContainer = IndigoContainer,
    onPrimaryContainer = IndigoDark,
    secondary = Teal,
    onSecondary = Color.White,
    secondaryContainer = TealContainer,
    onSecondaryContainer = Teal,
    tertiary = Amber,
    onTertiary = Color.White,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = Amber,
    background = Page,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = IndigoContainer,
    onSurfaceVariant = Slate,
    outline = Line,
    error = Danger,
    onError = Color.White,
    errorContainer = DangerContainer,
    onErrorContainer = Danger,
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoNight,
    onPrimary = IndigoDark,
    primaryContainer = Indigo,
    onPrimaryContainer = IndigoContainer,
    secondary = TealContainer,
    onSecondary = Teal,
    tertiary = AmberContainer,
    onTertiary = Amber,
    tertiaryContainer = Amber,
    onTertiaryContainer = AmberContainer,
    background = PageNight,
    onBackground = InkNight,
    surface = SurfaceNight,
    onSurface = InkNight,
    surfaceVariant = IndigoDark,
    onSurfaceVariant = IndigoContainer,
    error = DangerContainer,
    onError = Danger,
    errorContainer = Danger,
    onErrorContainer = DangerContainer,
)

@Composable
fun NewDemoAppLTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}

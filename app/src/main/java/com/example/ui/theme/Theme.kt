package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Win10FluentColorScheme = darkColorScheme(
    primary = Win10Cyan,
    onPrimary = Color(0xFF001F2A),
    primaryContainer = Win10Blue,
    onPrimaryContainer = Color.White,
    secondary = Win10Emerald,
    onSecondary = Color(0xFF002114),
    tertiary = Win10Amber,
    background = Win10DarkNavy,
    onBackground = Win10TextPrimary,
    surface = Win10WindowBg,
    onSurface = Win10TextPrimary,
    surfaceVariant = Win10SurfaceGlass,
    onSurfaceVariant = Win10TextSecondary,
    error = Win10Crimson
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = Win10FluentColorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = GeometricPrimary,
    onPrimary = GeometricOnPrimary,
    background = GeometricBackground,
    onBackground = GeometricOnSurface,
    surface = GeometricSurface,
    onSurface = GeometricOnSurface,
    surfaceVariant = GeometricSurfaceVariant,
    onSurfaceVariant = GeometricOnSurfaceVariant,
    error = GeometricError,
    onError = GeometricOnError,
    outline = GeometricOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

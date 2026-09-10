new_content = """package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

val LightColorScheme = lightColorScheme(
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

val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = GeometricError,
    onError = GeometricOnError,
    outline = DarkOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme by default as requested
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
"""

with open('app/src/main/java/com/example/ui/theme/Theme.kt', 'w') as f:
    f.write(new_content)

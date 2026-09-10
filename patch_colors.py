with open('app/src/main/java/com/example/ui/theme/Color.kt', 'r') as f:
    content = f.read()

content += """
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkSurfaceVariant = Color(0xFF2D2D2D)
val DarkPrimary = Color(0xFFD0BCFF)
val DarkOnPrimary = Color(0xFF381E72)
val DarkOnSurface = Color(0xFFE6E1E5)
val DarkOnSurfaceVariant = Color(0xFFCAC4D0)
val DarkOutline = Color(0xFF938F99)
"""

with open('app/src/main/java/com/example/ui/theme/Color.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/theme/Theme.kt', 'r') as f:
    theme_content = f.read()

theme_content = theme_content.replace(
    'import androidx.compose.material3.lightColorScheme',
    'import androidx.compose.material3.lightColorScheme\nimport androidx.compose.material3.darkColorScheme'
)

theme_content = theme_content.replace(
    'private val LightColorScheme = lightColorScheme(',
    'val LightColorScheme = lightColorScheme('
)

dark_scheme = """
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
"""

theme_content = theme_content.replace(
    '@Composable',
    dark_scheme + '\n@Composable'
)

theme_content = theme_content.replace(
    'colorScheme = LightColorScheme',
    'colorScheme = DarkColorScheme'
)

with open('app/src/main/java/com/example/ui/theme/Theme.kt', 'w') as f:
    f.write(theme_content)


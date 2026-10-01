package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = MintContainer,
    onPrimaryContainer = EmeraldDark,
    secondary = GoldPrimary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFBF2DC),
    onSecondaryContainer = GoldDark,
    tertiary = AmberAccent,
    background = CreamBackground,
    onBackground = Color(0xFF1E2820),
    surface = ParchmentSurface,
    onSurface = Color(0xFF1E2820),
    surfaceVariant = Color(0xFFF0EAE1),
    onSurfaceVariant = Color(0xFF49454F),
    error = ErrorRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldLight,
    onPrimary = Color.Black,
    primaryContainer = DarkGreenCard,
    onPrimaryContainer = MintContainer,
    secondary = GoldLight,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF382E18),
    onSecondaryContainer = GoldLight,
    tertiary = AmberAccent,
    background = DarkGreenBackground,
    onBackground = Color(0xFFE4E9E5),
    surface = DarkGreenSurface,
    onSurface = Color(0xFFE4E9E5),
    surfaceVariant = DarkGreenCard,
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF435A48),
    outlineVariant = Color(0xFF2B3D2F),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun QuranLearningTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

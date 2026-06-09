package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentOrange,
    onPrimary = Color.Black,
    secondary = SecondaryText,
    background = Background,
    surface = Surface,
    onBackground = PrimaryText,
    onSurface = PrimaryText,
    surfaceVariant = CardBg,
    outline = BorderColor
)

@Composable
fun DurbinTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    DurbinTheme(content = content)
}

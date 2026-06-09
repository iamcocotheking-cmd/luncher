package net.kdt.pojavlaunch.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DurbinDarkColorScheme = darkColorScheme(
    primary = DurbinAccentOrange,
    onPrimary = Color.Black,
    secondary = DurbinSecondaryText,
    background = DurbinBackground,
    surface = DurbinSurface,
    onBackground = DurbinPrimaryText,
    onSurface = DurbinPrimaryText,
    surfaceVariant = DurbinCardBg,
    outline = DurbinBorderColor
)

@Composable
fun DurbinTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DurbinDarkColorScheme,
        typography = DurbinTypography,
        content = content
    )
}

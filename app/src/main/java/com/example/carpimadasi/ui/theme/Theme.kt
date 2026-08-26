package com.example.carpimadasi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    onPrimary = Color.White,
    primaryContainer = SkyBlueLight,
    onPrimaryContainer = DarkSlate,
    secondary = AmberSun,
    onSecondary = Color.White,
    secondaryContainer = AmberWarm,
    onSecondaryContainer = DarkSlate,
    tertiary = EmeraldGreen,
    onTertiary = Color.White,
    background = Color.White,
    onBackground = DarkSlate,
    surface = Color.White,
    onSurface = DarkSlate
)

@Composable
fun CarpimAdasiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

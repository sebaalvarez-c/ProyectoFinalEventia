package com.example.proyectofinaleventia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Navy80,
    onPrimary = Color(0xFF0B1730),
    primaryContainer = NavyContainer80,
    onPrimaryContainer = Color(0xFFD9E5F5),
    secondary = Steel80,
    background = Surface80,
    surface = Surface80
)

private val LightColorScheme = lightColorScheme(
    primary = Navy40,
    onPrimary = Color.White,
    primaryContainer = NavyContainer40,
    onPrimaryContainer = Navy40,
    secondary = Steel40,
    background = Surface40,
    surface = Surface40
)

@Composable
fun EventiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}

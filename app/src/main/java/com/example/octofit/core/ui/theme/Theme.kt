package com.example.octofit.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
private data class OctofitPalette(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
)

private val LightPalette = OctofitPalette(
    primary = Color(0xFF1B6EF3),
    secondary = Color(0xFF0B1424),
    tertiary = Color(0xFF16B3AC),
)

private val DarkPalette = OctofitPalette(
    primary = Color(0xFF9FC2FF),
    secondary = Color(0xFFE2E6F4),
    tertiary = Color(0xFF78DCD5),
)

@Composable
fun OctofitTheme(
    useDarkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val palette = if (useDarkTheme) DarkPalette else LightPalette
    val colorScheme = if (useDarkTheme) {
        darkColorScheme(
            primary = palette.primary,
            secondary = palette.secondary,
            tertiary = palette.tertiary,
        )
    } else {
        lightColorScheme(
            primary = palette.primary,
            secondary = palette.secondary,
            tertiary = palette.tertiary,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

package com.syrous.pacman.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = yellow,
    secondary = orange,
    tertiary = fieryOrange,
    background = black,
    surface = black,
    onPrimary = grey,
    onSecondary = black,
    onTertiary = black,
    onBackground = white,
    onSurface = blue
)

val GameControlActionButtonScheme =  ButtonColors(
    containerColor = red,
    contentColor = white,
    disabledContainerColor = red,
    disabledContentColor = grey
)

@Composable
fun PacmanTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
package com.test.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MvmColorScheme = darkColorScheme(
    primary = MvmGold,
    secondary = MvmButton,
    tertiary = MvmVerse,
    background = MvmBackground,
    surface = MvmCard,
    onPrimary = MvmBackground,
    onSecondary = MvmText,
    onTertiary = MvmBackground,
    onBackground = MvmText,
    onSurface = MvmText
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = MvmColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

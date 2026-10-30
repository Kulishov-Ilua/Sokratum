package ru.kulishov.sokratum

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val lightTheme = lightColorScheme(
    surface = Color(247, 250, 248),
    surfaceContainer = Color(242, 242, 242),
    primary = Color(82, 66, 126)
)

val darkTheme = darkColorScheme(
    surface = Color(25, 25, 25),
    surfaceContainer = Color(38, 38, 38),
    primary = Color(184, 178, 252)
)

@Composable
fun SokratumTheme(content: @Composable () -> Unit) {
    val currentColorScheme =
        if (isSystemInDarkTheme()) darkTheme else lightTheme
    MaterialTheme(
        content = content,
        colorScheme = currentColorScheme
    )
}
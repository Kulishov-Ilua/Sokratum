package ru.kulishov.sokratum

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.kulishov.sokratum.di.initKoin

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Sokratum",
    ) {
        initKoin()
        App()
    }
}
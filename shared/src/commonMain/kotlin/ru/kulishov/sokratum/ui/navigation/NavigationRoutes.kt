package ru.kulishov.sokratum.ui.navigation

import kotlinx.serialization.Serializable

sealed class NavigationRoutes{
    @Serializable
    object ModelScreen: NavigationRoutes()

    @Serializable
    object HomeScreen: NavigationRoutes()
}



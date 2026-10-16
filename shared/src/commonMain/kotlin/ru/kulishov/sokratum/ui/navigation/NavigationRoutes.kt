package ru.kulishov.sokratum.ui.navigation

import kotlinx.serialization.Serializable
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.model.SpaceRedactTransfer

sealed class NavigationRoutes{
    @Serializable
    object AuthScreen: NavigationRoutes()

    @Serializable
    object HomeScreen: NavigationRoutes()

    @Serializable
    object SettingScreen: NavigationRoutes()

    @Serializable
    data class SpaceRedactScreen(val spaceId: Long,
                                 val spaceName: String)
    @Serializable
    data class ChatRedactScreen(val chatId: Long,
                                 val spaceId: Long)
    @Serializable
    data class SpaceScreen(val spaceId:Long, val spaceName:String)


    @Serializable
    data class ChatScreen(val chatId: Long, val spaceId:Long, val chatName:String, val message:String = "")
}



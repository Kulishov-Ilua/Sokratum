package ru.kulishov.sokratum.data.remote.model.chats

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequest(
    val chatId:Int,
    val model:String,
    val text:String
)

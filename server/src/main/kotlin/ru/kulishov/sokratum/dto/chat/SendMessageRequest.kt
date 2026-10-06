package ru.kulishov.sokratum.dto.chat

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequest(
    val chatId:Int,
    val model:String,
    val text:String
)

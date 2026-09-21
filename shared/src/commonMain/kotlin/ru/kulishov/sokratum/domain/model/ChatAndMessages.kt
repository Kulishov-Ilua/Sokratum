package ru.kulishov.sokratum.domain.model

data class ChatAndMessages(
    val chat: Chat,
    val messages: List<Message>
)

package ru.kulishov.sokratum.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ChatAndMessages(
    val id: Int,
    val spaceId: Int,
    val name: String,
    val systemPrompt: String,
    val lastOpen: LocalDateTime,
    val messages: MessagesAndUpdateTime
)
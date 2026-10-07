package ru.kulishov.sokratum.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MessagesAndUpdateTime(
    val messages: List<Message>,
    val updateTime: LocalDateTime
)
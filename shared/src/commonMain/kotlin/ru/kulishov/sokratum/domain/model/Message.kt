package ru.kulishov.sokratum.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Long,
    val idChat: Int,
    val isModel: Boolean,
    val text: String,
    val sendTime: LocalDateTime
)
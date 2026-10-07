package ru.kulishov.sokratum.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Long?,
    val idChat: Long,
    val isModel: Boolean,
    val text: String,
    val sendTime: Long
)

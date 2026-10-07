package ru.kulishov.sokratum.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class SpaceAndChats(
    val id: Int,
    val name: String,
    val lastOpen: LocalDateTime,
    val chats: List<Chat>
)
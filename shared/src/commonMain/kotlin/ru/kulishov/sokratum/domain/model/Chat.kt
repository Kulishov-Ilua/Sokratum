package ru.kulishov.sokratum.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: Long,
    val spaceId: Long,
    val name: String,
    val systemPrompt: String,
    val lastOpen: LocalDateTime
)

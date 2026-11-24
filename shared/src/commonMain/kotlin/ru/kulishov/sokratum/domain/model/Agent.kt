package ru.kulishov.sokratum.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Agent(
    val id: Int,
    val chatId: Int,
    val name: String,
    val systemPrompt: String
)

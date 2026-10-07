package ru.kulishov.sokratum.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: Long,
    val idSpace: Long,
    val name: String,
    val systemPrompt: String,
    val lastOpen: Long
)

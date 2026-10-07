package ru.kulishov.sokratum.ai

import kotlinx.serialization.Serializable

@Serializable
data class AIModel(
    val id: Int,
    val name: String,
    val url: String,
    val key: String,
    val timeout: Long
)

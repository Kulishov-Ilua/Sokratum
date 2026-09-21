package ru.kulishov.sokratum.domain.model

data class Chat(
    val id: Long,
    val idSpace: Long,
    val name: String,
    val systemPrompt: String,
    val lastOpen: Long
)

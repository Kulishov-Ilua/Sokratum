package ru.kulishov.sokratum.domain.model

data class Message(
    val id: Long?,
    val idChat: Long,
    val isModel: Boolean,
    val text: String,
    val sendTime: Long
)

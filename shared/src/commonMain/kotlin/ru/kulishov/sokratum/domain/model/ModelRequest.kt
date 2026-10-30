package ru.kulishov.sokratum.domain.model

data class ModelRequest(
    val model: Model,
    val chatId:Long,
    val systemPrompt: String,
    val temperature:Double,
    val path: String,
    val text: String
)

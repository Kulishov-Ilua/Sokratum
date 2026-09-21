package ru.kulishov.sokratum.domain.model

data class Model(
    val id: Int,
    val name: String,
    val url: String,
    val key: String,
    val timeout: Long
)
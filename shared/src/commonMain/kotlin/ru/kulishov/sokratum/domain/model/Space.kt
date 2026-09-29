package ru.kulishov.sokratum.domain.model

data class Space(
    val id: Long,
    val name: String,
    val path: String,
    val storage: String,
    val lastOpen: Long
)

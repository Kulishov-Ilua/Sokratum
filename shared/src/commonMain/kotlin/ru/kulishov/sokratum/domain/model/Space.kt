package ru.kulishov.sokratum.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Space(
    val id: Long,
    val name: String,
    val lastOpen: Long
)

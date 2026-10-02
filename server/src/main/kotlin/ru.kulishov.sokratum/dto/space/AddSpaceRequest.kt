package ru.kulishov.sokratum.dto.space

import kotlinx.serialization.Serializable

@Serializable
data class AddSpaceRequest(
    val name: String,
)

package ru.kulishov.sokratum.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SpaceRedactTransfer(
    val spaceId: Long,
    val spaceName: String
)

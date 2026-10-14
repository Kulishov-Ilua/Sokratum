package ru.kulishov.sokratum.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class File(
    val id: String,
    val spaceId:Int,
    val name: String,
    val size:Long,
    val contentType: String,
    val path: String,
    val uploadedAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null
)


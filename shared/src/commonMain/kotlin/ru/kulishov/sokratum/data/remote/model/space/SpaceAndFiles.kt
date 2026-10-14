package ru.kulishov.sokratum.data.remote.model.space

import kotlinx.serialization.Serializable
import ru.kulishov.sokratum.domain.model.File
import ru.kulishov.sokratum.domain.model.Space

@Serializable
data class SpaceAndFiles(
    val space: Space,
    val files:List<File>
)

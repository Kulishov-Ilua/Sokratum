package ru.kulishov.sokratum.dto.space

import kotlinx.serialization.Serializable
import ru.kulishov.sokratum.db.file.File
import ru.kulishov.sokratum.db.file.Files
import ru.kulishov.sokratum.db.space.Space

@Serializable
data class SpaceAndFiles(
    val space: Space,
    val files:List<File>
)

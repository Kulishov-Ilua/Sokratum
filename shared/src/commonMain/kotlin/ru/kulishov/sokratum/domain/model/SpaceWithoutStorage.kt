package ru.kulishov.sokratum.domain.model

data class SpaceWithoutStorage(
    val id: Long,
    val name: String,
    val path: String,
    val lastOpen: Long
)
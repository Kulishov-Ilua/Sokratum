package ru.kulishov.sokratum.data.local.data.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "spaces")
data class SpaceEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val lastOpen: Long
)

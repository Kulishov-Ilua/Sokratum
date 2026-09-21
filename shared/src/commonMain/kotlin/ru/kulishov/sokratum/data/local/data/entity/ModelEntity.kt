package ru.kulishov.sokratum.data.local.data.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "models")
data class ModelEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val url: String,
    val key: String,
    val timeout: Long
)
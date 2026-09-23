package ru.kulishov.sokratum.data.local.data.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val idChat: Long,
    val isModel: Boolean,
    val text: String,
    val sendTime: Long
)

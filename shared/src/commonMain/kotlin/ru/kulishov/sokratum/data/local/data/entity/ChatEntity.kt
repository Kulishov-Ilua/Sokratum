package ru.kulishov.sokratum.data.local.data.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: Long,
    val idSpace: Long,
    val name: String,
    val systemPrompt: String,
    val lastOpen: Long
)

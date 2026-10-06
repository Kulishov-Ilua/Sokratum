package ru.kulishov.sokratum.db.chat

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.datetime
import ru.kulishov.sokratum.db.message.MessagesAndUpdateTime
import ru.kulishov.sokratum.db.space.Spaces


object Chats : Table() {
    val id = integer("id").autoIncrement()
    val spaceId = integer("space_id").references(Spaces.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 255)
    val systemPrompt = varchar("system_prompt", 10000)
    var lastOpen = datetime("last_open")

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Chat(
    val id: Int,
    val spaceId: Int,
    val name: String,
    val systemPrompt: String,
    val lastOpen: LocalDateTime
)

@Serializable
data class ChatAndMessages(
    val id: Int,
    val spaceId: Int,
    val name: String,
    val systemPrompt: String,
    val lastOpen: LocalDateTime,
    val messages: MessagesAndUpdateTime
)
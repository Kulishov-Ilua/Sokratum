package ru.kulishov.sokratum.db.agent

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import ru.kulishov.sokratum.db.chat.Chats

object Agents : Table() {
    val id = integer("id").autoIncrement()
    val chatId = integer("chat_id").references(Chats.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 120)
    val systemPrompt = varchar("system_prompt", 10000)

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Agent(
    val id: Int,
    val chatId: Int,
    val name: String,
    val systemPrompt: String
)

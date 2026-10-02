package ru.kulishov.sokratum.db.message

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.datetime
import ru.kulishov.sokratum.db.chat.Chats


object Messages : Table() {
    val id = long("id").autoIncrement()
    val idChat = integer("id_chat").references(Chats.id, onDelete = ReferenceOption.CASCADE)
    val isModel = bool("is_model")
    val text = varchar("text", 50000)
    val sendTime = datetime("send_time")

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Message(
    val id: Long,
    val idChat: Int,
    val isModel: Boolean,
    val text: String,
    val sendTime: LocalDateTime
)

@Serializable
data class MessagesAndUpdateTime(
    val messages: List<Message>,
    val updateTime: LocalDateTime
)
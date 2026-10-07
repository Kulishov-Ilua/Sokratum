package ru.kulishov.sokratum.db.space

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.datetime
import ru.kulishov.sokratum.db.chat.Chat

object Spaces : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    var lastOpen = datetime("last_open")

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Space(
    val id: Int,
    val name: String,
    val lastOpen: LocalDateTime
)

@Serializable
data class SpaceAndChats(
    val id: Int,
    val name: String,
    val lastOpen: LocalDateTime,
    val chats: List<Chat>
)
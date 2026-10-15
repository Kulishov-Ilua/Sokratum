package ru.kulishov.sokratum.db.message

import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import ru.kulishov.sokratum.db.query
import java.time.LocalDateTime

class MessageDao {
    fun rowToMessage(row: ResultRow) = Message(
        id = row[Messages.id],
        idChat = row[Messages.idChat],
        isModel = row[Messages.isModel],
        text = row[Messages.text],
        sendTime = row[Messages.sendTime].toKotlinLocalDateTime()
    )

    suspend fun addMessage(message: Message): Message? = query {
        val insert = Messages.insert {
            it[Messages.idChat] = message.idChat
            it[Messages.isModel] = message.isModel
            it[Messages.text] = message.text
            it[Messages.sendTime] = LocalDateTime.now()
        }.resultedValues?.map { rowToMessage(it) }?.firstOrNull() ?: return@query null

        return@query insert
    }

    suspend fun updateMessageList(
        chatId: Int,
        lastMessage: Long
    ): MessagesAndUpdateTime = query {
        val messages = Messages.selectAll()
            .where { (Messages.idChat eq chatId) and (Messages.id greater lastMessage) }
            .map { rowToMessage(it) }
        return@query MessagesAndUpdateTime(messages, LocalDateTime.now().toKotlinLocalDateTime())
    }
}
package ru.kulishov.sokratum.db.chat

import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import ru.kulishov.sokratum.db.message.Message
import ru.kulishov.sokratum.db.message.Messages
import ru.kulishov.sokratum.db.message.MessagesAndUpdateTime
import ru.kulishov.sokratum.db.query
import ru.kulishov.sokratum.db.space.Spaces
import java.time.LocalDateTime

class ChatDao {
    fun rowToChat(row: ResultRow) = Chat(
        id = row[Chats.id],
        spaceId = row[Chats.spaceId],
        name = row[Chats.name],
        systemPrompt = row[Chats.systemPrompt],
        lastOpen = row[Chats.lastOpen].toKotlinLocalDateTime()
    )

    fun rowToMessage(row: ResultRow) = Message(
        id = row[Messages.id],
        idChat = row[Messages.idChat],
        isModel = row[Messages.isModel],
        text = row[Messages.text],
        sendTime = row[Messages.sendTime].toKotlinLocalDateTime()
    )

    suspend fun getLastChats(): List<Chat> = query {
        return@query Chats.selectAll().orderBy(Chats.lastOpen, SortOrder.DESC).limit(10)
            .map { rowToChat(it) }
    }

    suspend fun getChat(id:Int): Chat? = query {
        return@query Chats.selectAll().where{ Chats.id eq id }.map { rowToChat(it) }.firstOrNull()
    }
    suspend fun getChatAndMessages(chatId: Int): ChatAndMessages? = query {
        val chat =
            Chats.selectAll().where { Chats.id eq chatId }.map { rowToChat(it) }.firstOrNull()
        if (chat != null) {
            Spaces.update({ Spaces.id eq chat.spaceId }) {
                it[Spaces.lastOpen] = LocalDateTime.now()
            }
            Chats.update({ Chats.id eq chat.id }) { it[Chats.lastOpen] = LocalDateTime.now() }
            val messages =
                Messages.selectAll().where { Messages.idChat eq chat.id }.map { rowToMessage(it) }
            return@query ChatAndMessages(
                id = chat.id,
                spaceId = chat.spaceId,
                name = chat.name,
                systemPrompt = chat.systemPrompt,
                lastOpen = chat.lastOpen,
                messages = MessagesAndUpdateTime(
                    messages,
                    LocalDateTime.now().toKotlinLocalDateTime()
                )
            )
        } else return@query null
    }

    suspend fun addChat(chat: Chat): Chat? = query {
        val chat = Chats.insert {
            it[Chats.name] = chat.name
            it[Chats.spaceId] = chat.spaceId
            it[Chats.systemPrompt] = chat.systemPrompt
            it[Chats.lastOpen] = LocalDateTime.now()
        }.resultedValues?.map { rowToChat(it) }?.firstOrNull() ?: return@query null
        return@query chat
    }

    suspend fun updateChat(chat: Chat): Chat? = query {
        val chatNew = Chats.update({ Chats.id eq chat.id }) {
            it[Chats.name] = chat.name
            it[Chats.spaceId] = chat.spaceId
            it[Chats.systemPrompt] = chat.systemPrompt
        }.let {
            Chats.selectAll().where { Chats.id eq chat.id }.map { rowToChat(it) }.singleOrNull()
        }
        return@query chatNew
    }

    suspend fun deleteChat(chat: Chat): Boolean = query {
        Chats.deleteWhere { Chats.id eq chat.id }
        return@query true
    }


}
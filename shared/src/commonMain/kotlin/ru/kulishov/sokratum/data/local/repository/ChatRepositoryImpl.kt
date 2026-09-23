package ru.kulishov.sokratum.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.kulishov.sokratum.data.local.dao.ChatDao
import ru.kulishov.sokratum.data.local.dao.MessageDao
import ru.kulishov.sokratum.data.local.data.mapper.ChatMapper
import ru.kulishov.sokratum.data.local.data.mapper.MessageMapper
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.ChatAndMessages
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.repository.ChatRepository
import kotlin.time.Clock

class ChatRepositoryImpl(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao
) : ChatRepository {
    override fun getChatsData(spaceId: Long): Flow<List<Chat>> =
        chatDao.getChatsData(spaceId).map { entity -> entity.map { ChatMapper.toDomain(it) } }

    override fun getLastChats(): Flow<List<Chat>> =
        chatDao.getLastChats().map { entity -> entity.map { ChatMapper.toDomain(it) } }

    override suspend fun addChat(chat: Chat): Chat? {
        val id = chatDao.addChat(ChatMapper.toEntity(chat))
        val findChat = chatDao.getChat(id)
        if (findChat == null) return null
        else return ChatMapper.toDomain(findChat)
    }

    override suspend fun updateChat(chat: Chat): Chat? {
        val id = chatDao.updateChat(ChatMapper.toEntity(chat))
        val findChat = chatDao.getChat(id)
        if (findChat == null) return null
        else return ChatMapper.toDomain(findChat)
    }

    override suspend fun deleteChat(chat: Chat) = chatDao.deleteChat(ChatMapper.toEntity(chat))

    override suspend fun getChat(chatId: Long): ChatAndMessages? {
        val findChat = chatDao.getChat(chatId)
        if (findChat == null) return null
        else {
            val messages = messageDao.getChatMessages(chatId)
            return ChatMapper.toChatAndMessages(findChat, messages)
        }
    }

    override suspend fun addMessage(
        message: Message
    ): Message? {
        val newMessage = message.copy(sendTime = Clock.System.now().epochSeconds)
        val id = messageDao.addMessage(message)
        val findMessage = messageDao.getMessage(id)
        if (findMessage == null) return null
        else return MessageMapper.toDomain(findMessage)
    }

    override fun updateMessages(
        chatId: Long,
        updateTime: Long
    ): Flow<List<Message>> = messageDao.updateMessages(chatId, updateTime)
        .map { entity -> entity.map { MessageMapper.toDomain(it) } }
}
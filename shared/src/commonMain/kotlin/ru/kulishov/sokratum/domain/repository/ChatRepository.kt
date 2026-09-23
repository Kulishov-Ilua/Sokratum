package ru.kulishov.sokratum.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.ChatAndMessages
import ru.kulishov.sokratum.domain.model.Message

interface ChatRepository {
    fun getChatsData(spaceId: Long): Flow<List<Chat>>
    fun getLastChats(): Flow<List<Chat>>
    suspend fun addChat(chat: Chat): Chat?
    suspend fun updateChat(chat: Chat): Chat?
    suspend fun deleteChat(chat: Chat)
    suspend fun getChat(chatId: Long): ChatAndMessages?
    suspend fun addMessage(message: Message): Message?
    fun updateMessages(chatId: Long, updateTime: Long): Flow<List<Message>>
}
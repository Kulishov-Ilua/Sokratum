package ru.kulishov.sokratum.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.ChatAndMessages
import ru.kulishov.sokratum.domain.model.Message

interface ChatRepository {

    suspend fun getLastChats():List<Chat>
    suspend fun addChat(chat: Chat): Chat?
    suspend fun updateChat(chat: Chat): Chat?
    suspend fun deleteChat(chat: Chat)
    suspend fun getChat(chatId: Long): ChatAndMessages?
    suspend fun addMessage(message: Message): Message?
    suspend fun updateMessages(chatId: Long, updateTime: Long): List<Message>
}
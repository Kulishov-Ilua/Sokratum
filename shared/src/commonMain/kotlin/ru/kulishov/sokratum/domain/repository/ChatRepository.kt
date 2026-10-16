package ru.kulishov.sokratum.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.data.remote.model.chats.SendMessageRequest
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.ChatAndMessages
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.RefreshResult

interface ChatRepository {

     fun getLastChats():Flow<RefreshResult<Chat>>
    suspend fun addChat(chat: Chat): Chat?
    suspend fun updateChat(chat: Chat): Chat?
    suspend fun deleteChat(chat: Chat)
     fun getChat(chatId: Long, spaceId:Long, message:String?): Flow<RefreshResult<Message>>
    suspend fun getChatById(chatId: Long): Chat?
    suspend fun sendMessage(message: SendMessageRequest): Message?
    suspend fun updateMessages(chatId: Long, updateTime: Long): List<Message>

    fun messages(chatId: Int,spaceId:Long, afterMessageId: Long?, outgoing: Flow<Message>): Flow<RefreshResult<Message>>
    suspend fun send(message: Message)


}
package ru.kulishov.sokratum.domain.usecase.chat

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.ChatAndMessages
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.repository.ChatRepository

class GetChatUseCase(
    private val repository: ChatRepository
) {
     operator fun invoke(chatId: Long, spaceId:Long, message:String?): Flow<RefreshResult<Message>> = repository.getChat(chatId,spaceId,message)
}
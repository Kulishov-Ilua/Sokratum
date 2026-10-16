package ru.kulishov.sokratum.domain.usecase.chat

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.repository.ChatRepository

class MessagesUseCase(
    private val repository: ChatRepository
) {
     operator fun invoke(chatId: Int,spaceId:Long, afterMessageId: Long?, outgoing: Flow<Message>): Flow<RefreshResult<Message>> = repository.messages(chatId,spaceId,afterMessageId,outgoing)
}
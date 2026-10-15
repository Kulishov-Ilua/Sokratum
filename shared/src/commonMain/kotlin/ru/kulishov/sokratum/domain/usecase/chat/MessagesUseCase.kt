package ru.kulishov.sokratum.domain.usecase.chat

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.repository.ChatRepository

class MessagesUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatId: String, afterMessageId: Long?, outgoing: Flow<Message>): Flow<Message> = repository.messages(chatId,afterMessageId,outgoing)
}
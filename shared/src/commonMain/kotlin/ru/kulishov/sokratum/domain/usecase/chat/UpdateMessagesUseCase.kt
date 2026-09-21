package ru.kulishov.sokratum.domain.usecase.chat

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.repository.ChatRepository

class UpdateMessagesUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(chatId: Long, updateTime: Long): Flow<List<Message>> =
        repository.updateMessages(chatId, updateTime)
}
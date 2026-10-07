package ru.kulishov.sokratum.domain.usecase.chat

import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.repository.ChatRepository

class AddMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(message: Message): Message? =
        repository.addMessage(message)
}
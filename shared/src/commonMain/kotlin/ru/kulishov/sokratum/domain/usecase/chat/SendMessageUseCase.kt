package ru.kulishov.sokratum.domain.usecase.chat

import ru.kulishov.sokratum.data.remote.model.chats.SendMessageRequest
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.ModelRequest
import ru.kulishov.sokratum.domain.repository.ChatRepository
import ru.kulishov.sokratum.domain.repository.ModelRequestRepository

class SendMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(message: SendMessageRequest): Message? =
        repository.sendMessage(message)
}
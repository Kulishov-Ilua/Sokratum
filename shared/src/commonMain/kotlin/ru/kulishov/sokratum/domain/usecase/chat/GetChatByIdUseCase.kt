package ru.kulishov.sokratum.domain.usecase.chat

import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.repository.ChatRepository

class GetChatByIdUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatId:Long): Chat? = repository.getChatById(chatId)
}
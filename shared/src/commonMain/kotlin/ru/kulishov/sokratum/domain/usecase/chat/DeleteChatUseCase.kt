package ru.kulishov.sokratum.domain.usecase.chat

import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.repository.ChatRepository

class DeleteChatUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chat: Chat) = repository.deleteChat(chat)
}
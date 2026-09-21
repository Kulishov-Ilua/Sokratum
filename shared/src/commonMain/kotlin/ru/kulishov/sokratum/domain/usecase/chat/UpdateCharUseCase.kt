package ru.kulishov.sokratum.domain.usecase.chat

import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.repository.ChatRepository

class UpdateCharUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chat: Chat): Chat? = repository.updateChat(chat)
}
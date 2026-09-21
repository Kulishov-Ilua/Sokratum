package ru.kulishov.sokratum.domain.usecase.chat

import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.ChatAndMessages
import ru.kulishov.sokratum.domain.repository.ChatRepository

class GetChatUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatId: Long): ChatAndMessages? = repository.getChat(chatId)
}
package ru.kulishov.sokratum.domain.usecase.chat

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.repository.ChatRepository

class GetLastChatsUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(): List<Chat> = repository.getLastChats()
}
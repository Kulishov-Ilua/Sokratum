package ru.kulishov.sokratum.domain.usecase.chat

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.repository.ChatRepository

//class GetChatDataUseCase(
//    private val repository: ChatRepository
//) {
//    suspend operator fun invoke(spaceId: Long): List<Chat> = repository.getChat(spaceId)
//}
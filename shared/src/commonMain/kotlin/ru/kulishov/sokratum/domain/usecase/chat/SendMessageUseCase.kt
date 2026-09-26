package ru.kulishov.sokratum.domain.usecase.chat

import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.ModelRequest
import ru.kulishov.sokratum.domain.repository.ChatRepository
import ru.kulishov.sokratum.domain.repository.ModelRequestRepository

class SendMessageUseCase(
    private val modelRequestRepository: ModelRequestRepository,
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(message: ModelRequest): Message? {
        val answer = modelRequestRepository.sendMessage(message)
        if(answer!=null) chatRepository.addMessage(Message(
            id = null,
            idChat = message.chatId,
            isModel = true,
            text = answer.text,
            sendTime = answer.sendTime
        ))
        return answer
    }

}
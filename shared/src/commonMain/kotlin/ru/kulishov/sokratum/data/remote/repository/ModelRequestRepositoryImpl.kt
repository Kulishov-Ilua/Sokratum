package ru.kulishov.sokratum.data.remote.repository

import ru.kulishov.sokratum.data.remote.ApiClient
import ru.kulishov.sokratum.data.remote.model.chats.SendMessageRequest
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.ModelRequest
import ru.kulishov.sokratum.domain.repository.ModelRequestRepository

class ModelRequestRepositoryImpl : ModelRequestRepository {

    override suspend fun sendMessage(message: ModelRequest): Message? =
        ApiClient.post(
            "/sendMessage",
            SendMessageRequest(
                chatId = message.chatId.toInt(),
                model = message.model.name,
                temperature = message.temperature,
                text = message.text
            )
        )

    override suspend fun modelRequest(request: ModelRequest): String? =
        ApiClient.post("/sendMessage", request.text)
}

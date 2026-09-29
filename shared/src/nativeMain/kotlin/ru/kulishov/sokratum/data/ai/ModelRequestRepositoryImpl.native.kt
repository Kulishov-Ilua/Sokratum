package ru.kulishov.sokratum.data.ai

import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.ModelRequest
import ru.kulishov.sokratum.domain.repository.ModelRequestRepository

actual class ModelRequestRepositoryImpl actual constructor() :
    ModelRequestRepository {
    actual override suspend fun sendMessage(message: ModelRequest): Message? {
        TODO("Not yet implemented")
    }

    actual override suspend fun modelRequest(request: ModelRequest): String? {
        TODO("Not yet implemented")
    }

}
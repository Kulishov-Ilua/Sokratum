package ru.kulishov.sokratum.data.ai

import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.ModelRequest
import ru.kulishov.sokratum.domain.repository.ModelRequestRepository

expect class ModelRequestRepositoryImpl(): ModelRequestRepository {
     override suspend fun sendMessage(message: ModelRequest): Message?
}
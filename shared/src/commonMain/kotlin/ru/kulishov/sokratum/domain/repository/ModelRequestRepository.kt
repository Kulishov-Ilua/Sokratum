package ru.kulishov.sokratum.domain.repository

import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.ModelRequest

interface ModelRequestRepository {
    suspend fun sendMessage(message: ModelRequest): Message?
    suspend fun modelRequest(request: ModelRequest): String?
}
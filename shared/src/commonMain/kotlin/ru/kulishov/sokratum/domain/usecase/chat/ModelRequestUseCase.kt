package ru.kulishov.sokratum.domain.usecase.chat

import ru.kulishov.sokratum.domain.model.ModelRequest
import ru.kulishov.sokratum.domain.repository.ModelRequestRepository

class ModelRequestUseCase(
    private val repository: ModelRequestRepository
) {
    suspend operator fun invoke(message: ModelRequest): String? = repository.modelRequest(message)
}
package ru.kulishov.sokratum.domain.usecase.auth

import ru.kulishov.sokratum.domain.repository.AuthRepository

class GetModelsUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): List<String> = repository.getModels()
}
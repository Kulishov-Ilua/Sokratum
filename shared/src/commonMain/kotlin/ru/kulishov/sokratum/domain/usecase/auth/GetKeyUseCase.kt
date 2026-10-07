package ru.kulishov.sokratum.domain.usecase.auth

import ru.kulishov.sokratum.domain.repository.AuthRepository

class GetKeyUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): String? = repository.getKey()
}
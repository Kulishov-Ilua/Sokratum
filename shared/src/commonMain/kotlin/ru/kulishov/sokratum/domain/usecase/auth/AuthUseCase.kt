package ru.kulishov.sokratum.domain.usecase.auth

import io.ktor.http.HttpStatusCode
import ru.kulishov.sokratum.data.remote.model.auth.AuthRequest
import ru.kulishov.sokratum.domain.repository.AuthRepository

class AuthUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(authData: AuthRequest): HttpStatusCode = repository.auth(authData)
}
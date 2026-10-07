package ru.kulishov.sokratum.domain.usecase.auth

import io.ktor.http.HttpStatusCode
import ru.kulishov.sokratum.data.remote.model.auth.RegistrationRequest
import ru.kulishov.sokratum.domain.repository.AuthRepository

class RegistrationUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(registrationData: RegistrationRequest): HttpStatusCode = repository.registration(registrationData)
}
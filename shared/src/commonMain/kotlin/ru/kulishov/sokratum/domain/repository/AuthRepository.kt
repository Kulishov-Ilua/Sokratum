package ru.kulishov.sokratum.domain.repository

import io.ktor.http.HttpStatusCode
import ru.kulishov.sokratum.data.remote.model.auth.AuthRequest
import ru.kulishov.sokratum.data.remote.model.auth.DeviceDataResponse
import ru.kulishov.sokratum.data.remote.model.auth.RegistrationRequest

interface AuthRepository {
    suspend fun auth(authData: AuthRequest): HttpStatusCode
    suspend fun registration(registrationData: RegistrationRequest): HttpStatusCode
    suspend fun getDevicesData():List<DeviceDataResponse>

    suspend fun getKey(): String?
}
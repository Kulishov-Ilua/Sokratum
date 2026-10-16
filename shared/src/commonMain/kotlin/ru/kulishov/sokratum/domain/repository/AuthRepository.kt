package ru.kulishov.sokratum.domain.repository

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.data.remote.model.auth.AuthRequest
import ru.kulishov.sokratum.data.remote.model.auth.DeviceDataResponse
import ru.kulishov.sokratum.data.remote.model.auth.RegistrationRequest
import ru.kulishov.sokratum.domain.model.RefreshResult

interface AuthRepository {
    suspend fun auth(authData: AuthRequest): HttpStatusCode
    suspend fun registration(registrationData: RegistrationRequest): HttpStatusCode
     fun getDevicesData():Flow<RefreshResult<DeviceDataResponse>>

    suspend fun getKey(): String?

    suspend fun getModels(): List<String>
}
package ru.kulishov.sokratum.data.remote.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.IOException
import ru.kulishov.sokratum.data.remote.ApiClient
import ru.kulishov.sokratum.data.remote.KtorClient.JWT_KEY
import ru.kulishov.sokratum.data.remote.KtorClient.username
import ru.kulishov.sokratum.data.remote.model.auth.AuthRequest
import ru.kulishov.sokratum.data.remote.model.auth.AuthResponse
import ru.kulishov.sokratum.data.remote.model.auth.DeviceDataResponse
import ru.kulishov.sokratum.data.remote.model.auth.RegistrationRequest
import ru.kulishov.sokratum.domain.model.RefreshError
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.repository.AuthRepository

class AuthRepositoryImpl : AuthRepository {

    override suspend fun auth(authData: AuthRequest): HttpStatusCode {
        return try {
            val response = ApiClient.postRaw("/login", authData) ?: return HttpStatusCode.InternalServerError
            if (response.status == HttpStatusCode.OK) {
                val body: AuthResponse = response.body()
                JWT_KEY = body.token
                username = body.login
            } else {
                println("Error response: ${response.bodyAsText()}")
            }
            response.status
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println("Error: ${e.message}")
            HttpStatusCode.InternalServerError
        }
    }

    override suspend fun registration(registrationData: RegistrationRequest): HttpStatusCode {
        return try {
            val response = ApiClient.postRaw("/registration", registrationData)
                ?: return HttpStatusCode.InternalServerError
            if (response.status != HttpStatusCode.OK) {
                println("Error response: ${response.bodyAsText()}")
            }
            response.status
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println("Error: ${e.message}")
            HttpStatusCode.InternalServerError
        }
    }

    override fun getDevicesData(): Flow<RefreshResult<DeviceDataResponse>> = flow {
        try {
            val body: List<DeviceDataResponse>? = ApiClient.get("/devices")
            if (body != null) {
                emit(RefreshResult.Fresh(body))
            } else {
                emit(RefreshResult.Error)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            emit(RefreshResult.Error)
        } catch (e: ClientRequestException) {
            println("HTTP error: ${e.response.status}")
            emit(RefreshResult.Error)
        } catch (e: ServerResponseException) {
            emit(RefreshResult.Error)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(RefreshResult.Error)
        }
    }

    override suspend fun getKey(): String? =
        ApiClient.get("/getRegistrationKey")

    override suspend fun getModels(): List<String> =
        ApiClient.get("/getModels") ?: emptyList()
}

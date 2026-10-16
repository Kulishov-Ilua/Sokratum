package ru.kulishov.sokratum.data.remote.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.IOException
import ru.kulishov.sokratum.data.local.data.mapper.SpaceMapper
import ru.kulishov.sokratum.data.remote.KtorClient.BASE_URL
import ru.kulishov.sokratum.data.remote.KtorClient.JWT_KEY
import ru.kulishov.sokratum.data.remote.KtorClient.client
import ru.kulishov.sokratum.data.remote.KtorClient.username
import ru.kulishov.sokratum.data.remote.model.auth.AuthRequest
import ru.kulishov.sokratum.data.remote.model.auth.AuthResponse
import ru.kulishov.sokratum.data.remote.model.auth.DeviceDataResponse
import ru.kulishov.sokratum.data.remote.model.auth.RegistrationRequest
import ru.kulishov.sokratum.domain.model.RefreshError
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.repository.AuthRepository

class AuthRepositoryImpl() : AuthRepository {
    override suspend fun auth(authData: AuthRequest): HttpStatusCode {
        var ret = HttpStatusCode.InternalServerError
        try {
            val response = client.post("${BASE_URL}/login") {
                contentType(ContentType.Application.Json)
                setBody(authData)
            }

            if (response.status == HttpStatusCode.OK) {
                val body: AuthResponse = response.body()
                JWT_KEY = body.token
                username = body.login
            } else {
                println("Error response: ${response.bodyAsText()}")
            }

            ret = response.status
        } catch (e: ClientRequestException) {
            println("HTTP error: ${e.response.status}")
            val errorBody = e.response.bodyAsText()
            println("Error body: $errorBody")
            ret = e.response.status
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
        return ret
    }

    override suspend fun registration(registrationData: RegistrationRequest): HttpStatusCode {
        var ret = HttpStatusCode.InternalServerError
        try {
            val response = client.post("${BASE_URL}/registration") {
                contentType(ContentType.Application.Json)
                setBody(registrationData)
            }

            if (response.status == HttpStatusCode.OK) {

            } else {
                println("Error response: ${response.bodyAsText()}")
            }

            ret = response.status
        } catch (e: ClientRequestException) {
            println("HTTP error: ${e.response.status}")
            val errorBody = e.response.bodyAsText()
            println("Error body: $errorBody")
            ret = e.response.status
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
        return ret
    }


    override  fun getDevicesData(): Flow<RefreshResult<DeviceDataResponse>> = flow{
        try {
            val response = client.get("${BASE_URL}/devices"){
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
            }
                val body: List<DeviceDataResponse> = response.body()
                emit(RefreshResult.Fresh(body))

        }
        catch (e: CancellationException){
            throw e
        }
        catch (e: IOException){
            emit(RefreshResult.Error)
        }
        catch (e: ClientRequestException){
            val error = if (e.response.status == HttpStatusCode.Unauthorized) {
                RefreshError.Unauthorized
            } else {
                RefreshError.Server(e.response.status.value)
            }
            emit(RefreshResult.Error)
        }
        catch (e: ServerResponseException){
                emit(RefreshResult.Error)
        }
        catch (e: Exception){
            e.printStackTrace()
            emit(RefreshResult.Error)
        }
    }

    override suspend fun getKey(): String? {
        var ret:String? = null
        try {
            val response = client.get("${BASE_URL}/getRegistrationKey"){
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
            }
            if (response.status == HttpStatusCode.OK) {
                ret = response.body()
            }
        }catch (e: ClientRequestException) {
            println("HTTP error: ${e.response.status}")
            val errorBody = e.response.bodyAsText()
            println("Error body: $errorBody")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
        return ret
    }

    override suspend fun getModels(): List<String> {
        var ret = emptyList<String>()
        try {
            val response = client.get("${BASE_URL}/getModels"){
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
            }
            if (response.status == HttpStatusCode.OK) {
                ret = response.body()
            }
        }catch (e: ClientRequestException) {
            println("HTTP error: ${e.response.status}")
            val errorBody = e.response.bodyAsText()
            println("Error body: $errorBody")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
        return ret
    }
}

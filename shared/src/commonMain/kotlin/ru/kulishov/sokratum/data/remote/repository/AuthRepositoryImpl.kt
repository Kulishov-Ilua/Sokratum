package ru.kulishov.sokratum.data.remote.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import ru.kulishov.sokratum.data.remote.KtorClient.BASE_URL
import ru.kulishov.sokratum.data.remote.KtorClient.JWT_KEY
import ru.kulishov.sokratum.data.remote.KtorClient.client
import ru.kulishov.sokratum.data.remote.model.auth.AuthRequest
import ru.kulishov.sokratum.data.remote.model.auth.AuthResponse
import ru.kulishov.sokratum.data.remote.model.auth.DeviceDataResponse
import ru.kulishov.sokratum.data.remote.model.auth.RegistrationRequest
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


    override suspend fun getDevicesData(): List<DeviceDataResponse> {
        var ret = emptyList<DeviceDataResponse>()
        try {
            val response = client.get("${BASE_URL}/devices"){
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
            }
            if (response.status == HttpStatusCode.OK) {
                ret = response.body()
            }
        } catch (e: ClientRequestException) {
            println("HTTP error: ${e.response.status}")
            val errorBody = e.response.bodyAsText()
            println("Error body: $errorBody")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
        return ret
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
}

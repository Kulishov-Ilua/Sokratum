package ru.kulishov.sokratum.data.remote.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
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
import ru.kulishov.sokratum.data.remote.model.chats.SendMessageRequest
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.ModelRequest
import ru.kulishov.sokratum.domain.repository.ModelRequestRepository

class ModelRequestRepositoryImpl(): ModelRequestRepository {
    override suspend fun sendMessage(message: ModelRequest): Message? {
        var ret: Message? = null
        try {
            val response = client.post("${BASE_URL}/sendMessage") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
                setBody(SendMessageRequest(
                    chatId = message.chatId.toInt(),
                    model = message.model.name,
                    temperature = message.temperature,
                    text = message.text
                ))
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

    override suspend fun modelRequest(request: ModelRequest): String? {
        var ret: String? = null
        try {
            val response = client.post("${BASE_URL}/sendMessage") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
                setBody(request.text)
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
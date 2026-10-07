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
import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.data.remote.KtorClient.BASE_URL
import ru.kulishov.sokratum.data.remote.KtorClient.JWT_KEY
import ru.kulishov.sokratum.data.remote.KtorClient.client
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.ChatAndMessages
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.repository.ChatRepository

class ChatRepositoryImpl(): ChatRepository {
    override suspend fun getLastChats(): List<Chat> {
        var ret = emptyList<Chat>()
        try {
            val response = client.get("${BASE_URL}/getLastChats") {
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

    override suspend fun addChat(chat: Chat): Chat? {
        var ret:Chat? = null
        try {
            val response = client.post("${BASE_URL}/addChat") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
                setBody(chat)
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

    override suspend fun updateChat(chat: Chat): Chat? {
        var ret:Chat? = null
        try {
            val response = client.post("${BASE_URL}/updateChat") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
                setBody(chat)
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

    override suspend fun deleteChat(chat: Chat) {
        try {
            val response = client.post("${BASE_URL}/deleteChat") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
                setBody(chat)
            }
            if (response.status == HttpStatusCode.OK) {
            }
        }catch (e: ClientRequestException) {
            println("HTTP error: ${e.response.status}")
            val errorBody = e.response.bodyAsText()
            println("Error body: $errorBody")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

    }

    override suspend fun getChat(chatId: Long): ChatAndMessages? {
        var ret: ChatAndMessages? = null
        try {
            val response = client.get("${BASE_URL}/getChatAndMessages/$chatId") {
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

    override suspend fun addMessage(message: Message): Message? {
        TODO("Not yet implemented")
    }

    override suspend fun updateMessages(
        chatId: Long,
        updateTime: Long
    ): List<Message> {
        TODO("Not yet implemented")
    }


}
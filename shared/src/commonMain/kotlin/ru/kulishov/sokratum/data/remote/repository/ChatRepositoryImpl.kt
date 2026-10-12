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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.io.IOException
import ru.kulishov.sokratum.data.local.dao.ChatDao
import ru.kulishov.sokratum.data.local.data.mapper.ChatMapper
import ru.kulishov.sokratum.data.local.data.mapper.SpaceMapper
import ru.kulishov.sokratum.data.remote.KtorClient.BASE_URL
import ru.kulishov.sokratum.data.remote.KtorClient.JWT_KEY
import ru.kulishov.sokratum.data.remote.KtorClient.client
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.ChatAndMessages
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.RefreshError
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.repository.ChatRepository

class ChatRepositoryImpl(
    private val chatDao: ChatDao
) : ChatRepository {
    override fun getLastChats(): Flow<RefreshResult<Chat>> = flow {
        val cached = chatDao.getLastChats()

        if (cached.isNotEmpty()) {
            emit(RefreshResult.Cached(cached.map { ChatMapper.toDomain(it) }))
        }
        try{
            val response = client.get("${BASE_URL}/getAllChats"){
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
            }
            val body:List<Chat> = response.body()
            chatDao.replaceAll(body.map { ChatMapper.toEntity(it) })
            emit(RefreshResult.Fresh(if(body.size>2) body.subList(0,2) else body))
        }
        catch (e: CancellationException){
            throw e
        }
        catch (e: IOException){
            if(cached.isNotEmpty()){
                emit(RefreshResult.Offline(cached.map { ChatMapper.toDomain(it) }, RefreshError.Network))
            }else{
                emit(RefreshResult.Error)
            }
        }
        catch (e: ClientRequestException){
            val error = if (e.response.status == HttpStatusCode.Unauthorized) {
                RefreshError.Unauthorized
            } else {
                RefreshError.Server(e.response.status.value)
            }
            if(cached.isNotEmpty()){
                emit(RefreshResult.Offline(cached.map { ChatMapper.toDomain(it) }, error))
            }else{
                emit(RefreshResult.Error)
            }
        }
        catch (e: ServerResponseException){
            if(cached.isNotEmpty()){
                emit(RefreshResult.Offline(cached.map { ChatMapper.toDomain(it) }, RefreshError.Server(e.response.status.value)))
            }else{
                emit(RefreshResult.Error)
            }
        }
        catch (e: Exception){
            e.printStackTrace()
            emit(RefreshResult.Error)
        }
    }

    override suspend fun addChat(chat: Chat): Chat? {
        var ret: Chat? = null
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
        } catch (e: ClientRequestException) {
            println("HTTP error: ${e.response.status}")
            val errorBody = e.response.bodyAsText()
            println("Error body: $errorBody")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
        return ret
    }

    override suspend fun updateChat(chat: Chat): Chat? {
        var ret: Chat? = null
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
        } catch (e: ClientRequestException) {
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
        } catch (e: ClientRequestException) {
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
        } catch (e: ClientRequestException) {
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
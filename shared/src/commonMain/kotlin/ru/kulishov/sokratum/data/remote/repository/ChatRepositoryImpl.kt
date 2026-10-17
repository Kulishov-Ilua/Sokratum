package ru.kulishov.sokratum.data.remote.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import ru.kulishov.sokratum.data.ai.AIModels
import ru.kulishov.sokratum.data.local.dao.ChatDao
import ru.kulishov.sokratum.data.local.dao.MessageDao
import ru.kulishov.sokratum.data.local.data.mapper.ChatMapper
import ru.kulishov.sokratum.data.local.data.mapper.MessageMapper
import ru.kulishov.sokratum.data.local.data.mapper.SpaceMapper
import ru.kulishov.sokratum.data.remote.KtorClient.BASE_URL
import ru.kulishov.sokratum.data.remote.KtorClient.JWT_KEY
import ru.kulishov.sokratum.data.remote.KtorClient.client
import ru.kulishov.sokratum.data.remote.model.chats.SendMessageRequest
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.ChatAndMessages
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.RefreshError
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.repository.ChatRepository
import kotlin.time.Clock
import kotlin.time.Instant

class ChatRepositoryImpl(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao
) : ChatRepository {

    override fun getLastChats(): Flow<RefreshResult<Chat>> = flow {
        val cached = chatDao.getLastChats()

        if (cached.isNotEmpty()) {
            emit(RefreshResult.Cached(cached.map { ChatMapper.toDomain(it) }))
        }
        try {
            val response = client.get("${BASE_URL}/getAllChats") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
            }
            val body: List<Chat> = response.body()
            chatDao.replaceAll(body.map { ChatMapper.toEntity(it) })
            emit(RefreshResult.Fresh(if (body.size > 2) body.subList(0, 3) else body))
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            if (cached.isNotEmpty()) {
                emit(
                    RefreshResult.Offline(
                        cached.map { ChatMapper.toDomain(it) },
                        RefreshError.Network
                    )
                )
            } else {
                emit(RefreshResult.Error)
            }
        } catch (e: ClientRequestException) {
            val error = if (e.response.status == HttpStatusCode.Unauthorized) {
                RefreshError.Unauthorized
            } else {
                RefreshError.Server(e.response.status.value)
            }
            if (cached.isNotEmpty()) {
                emit(RefreshResult.Offline(cached.map { ChatMapper.toDomain(it) }, error))
            } else {
                emit(RefreshResult.Error)
            }
        } catch (e: ServerResponseException) {
            if (cached.isNotEmpty()) {
                emit(
                    RefreshResult.Offline(
                        cached.map { ChatMapper.toDomain(it) },
                        RefreshError.Server(e.response.status.value)
                    )
                )
            } else {
                emit(RefreshResult.Error)
            }
        } catch (e: Exception) {
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

    override  fun getChat(chatId: Long, spaceId:Long,  message:String?): Flow<RefreshResult<Message>> = flow{
        var newChatId = chatId
        if(chatId<0){
            if(spaceId<0){
                emit(RefreshResult.Error)
                return@flow
            }else{
                val chat=addChat(Chat(-1,spaceId, Clock.System.now().toString(),"", Clock.System.now().toLocalDateTime(
                    TimeZone.UTC) ))
                if(chat!=null){
                    chatDao.addChat(ChatMapper.toEntity(chat))
                    newChatId=chat.id
                    emit(RefreshResult.Fresh(emptyList()))
                }else{
                    emit(RefreshResult.Error)
                    return@flow
                }
            }
        }else {
            val cached = messageDao.getMessages(chatId.toLong())
            if (cached.isNotEmpty()) {
                emit(RefreshResult.Cached(cached.map { MessageMapper.toDomain(it) }))
            }

            try{
                val response = client.get("${BASE_URL}/getChatAndMessages/$chatId") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                    }
                }
                val ret: ChatAndMessages = response.body()
                chatDao.addChat(ChatMapper.toEntity(Chat(
                    id = ret.id.toLong(),
                    spaceId =ret.spaceId.toLong(),
                    name = ret.name,
                    systemPrompt = ret.systemPrompt,
                    lastOpen = Instant.fromEpochSeconds(ret.lastOpen).toLocalDateTime(TimeZone.UTC)
                )))
                messageDao.replaceAll(chatId.toLong(), ret.messages.messages.map { MessageMapper.toEntity(it) } )
                emit(RefreshResult.Fresh(ret.messages.messages))

            }catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                if (cached.isNotEmpty()) {
                    emit(
                        RefreshResult.Offline(
                            cached.map { MessageMapper.toDomain(it) },
                            RefreshError.Network
                        )
                    )
                } else {
                    emit(RefreshResult.Error)
                }
            } catch (e: ClientRequestException) {
                val error = if (e.response.status == HttpStatusCode.Unauthorized) {
                    RefreshError.Unauthorized
                } else {
                    RefreshError.Server(e.response.status.value)
                }
                if (cached.isNotEmpty()) {
                    emit(RefreshResult.Offline(cached.map { MessageMapper.toDomain(it) }, error))
                } else {
                    emit(RefreshResult.Error)
                }
                return@flow
            } catch (e: ServerResponseException) {
                if (cached.isNotEmpty()) {
                    emit(
                        RefreshResult.Offline(
                            cached.map { MessageMapper.toDomain(it) },
                            RefreshError.Server(e.response.status.value)
                        )
                    )
                } else {
                    emit(RefreshResult.Error)
                }
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(RefreshResult.Error)
                return@flow
            }
        }
        if(message!=null){
            try {
                val newMessage = Message(
                    id = -1,
                    idChat = newChatId.toInt(),
                    isModel = false,
                    text = message,
                    sendTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                )
                messageDao.addMessage(MessageMapper.toEntity(newMessage))
                val messageResponse = sendMessage(SendMessageRequest(
                    chatId = newChatId.toInt(),
                    model = AIModels.model.name,
                    text = message
                ))
                if(messageResponse!=null){
                    messageDao.addMessage(MessageMapper.toEntity(messageResponse))
                    emit(RefreshResult.Fresh(listOf(newMessage,messageResponse)))
                }
            }catch (e: ClientRequestException) {
                println("HTTP error: ${e.response.status}")
                val errorBody = e.response.bodyAsText()
                println("Error body: $errorBody")
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    override suspend fun getChatById(chatId: Long): Chat? {
        var ret: Chat? = null
        try {
            val response = client.get("${BASE_URL}/getChat/$chatId") {
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

    override suspend fun sendMessage(message: SendMessageRequest): Message? {
        var ret: Message?=null
        try {
            val response = client.post("${BASE_URL}/sendMessage") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
                setBody(message)
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

    override suspend fun updateMessages(
        chatId: Long,
        updateId: Long,
    ): List<Message> {
        TODO("Not yet implemented")
    }

    override fun messages(
        chatId: Int,
        spaceId:Long,
        afterMessageId: Long?,
        outgoing: Flow<Message>
    ): Flow<RefreshResult<Message>> = flow {
        var cursor = afterMessageId?:-1L
        if(chatId<0){
            if(spaceId<0){
                emit(RefreshResult.Error)
                return@flow
            }else{
                val chat=addChat(Chat(-1,spaceId, Clock.System.now().toString(),"", Clock.System.now().toLocalDateTime(
                    TimeZone.UTC) ))
                if(chat!=null){
                    chatDao.addChat(ChatMapper.toEntity(chat))
                }else{
                    emit(RefreshResult.Error)
                    return@flow
                }
            }
        }else{
            val cached = messageDao.getMessages(chatId.toLong())
            if (cached.isNotEmpty()) {
                emit(RefreshResult.Cached(cached.map { MessageMapper.toDomain(it) }))
            }


            try{
                val response = client.get("${BASE_URL}/getChatAndMessages/$chatId") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                    }
                }
                val ret: ChatAndMessages = response.body()
                chatDao.addChat(ChatMapper.toEntity(Chat(
                    id = ret.id.toLong(),
                    spaceId =ret.spaceId.toLong(),
                    name = ret.name,
                    systemPrompt = ret.systemPrompt,
                    lastOpen = Instant.fromEpochSeconds(ret.lastOpen).toLocalDateTime(TimeZone.UTC)
                )))
                messageDao.replaceAll(chatId.toLong(), ret.messages.messages.map { MessageMapper.toEntity(it) } )
                emit(RefreshResult.Fresh(ret.messages.messages))
                cursor = ret.lastOpen
            }catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                if (cached.isNotEmpty()) {
                    emit(
                        RefreshResult.Offline(
                            cached.map { MessageMapper.toDomain(it) },
                            RefreshError.Network
                        )
                    )
                } else {
                    emit(RefreshResult.Error)
                }
            } catch (e: ClientRequestException) {
                val error = if (e.response.status == HttpStatusCode.Unauthorized) {
                    RefreshError.Unauthorized
                } else {
                    RefreshError.Server(e.response.status.value)
                }
                if (cached.isNotEmpty()) {
                    emit(RefreshResult.Offline(cached.map { MessageMapper.toDomain(it) }, error))
                } else {
                    emit(RefreshResult.Error)
                }
                return@flow
            } catch (e: ServerResponseException) {
                if (cached.isNotEmpty()) {
                    emit(
                        RefreshResult.Offline(
                            cached.map { MessageMapper.toDomain(it) },
                            RefreshError.Server(e.response.status.value)
                        )
                    )
                } else {
                    emit(RefreshResult.Error)
                }
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(RefreshResult.Error)
                return@flow
            }
        }


        try {
            client.webSocket(
                urlString = ("${BASE_URL}/messages/$chatId/$cursor")
            ){
                coroutineScope {
                    val reader = launch {
                        for(frame in incoming){
                            if(frame is Frame.Text){
                                val message = Json.decodeFromString<Message>(frame.readText())
                                messageDao.addMessage(MessageMapper.toEntity(message))
                                emit(RefreshResult.Fresh(listOf(message)))
                            }
                        }
                    }
                    val writer = launch {
                        outgoing.collect { msg ->
                            send(Frame.Text(Json.encodeToString(msg)))
                        }
                    }
                    joinAll(reader,writer)
                }
            }
        }catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun send(message: Message) {
        TODO()
    }


}
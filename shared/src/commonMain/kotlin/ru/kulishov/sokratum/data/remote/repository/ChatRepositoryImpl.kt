package ru.kulishov.sokratum.data.remote.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import ru.kulishov.sokratum.data.ai.AIModels
import ru.kulishov.sokratum.data.local.dao.ChatDao
import ru.kulishov.sokratum.data.local.dao.MessageDao
import ru.kulishov.sokratum.data.local.data.mapper.ChatMapper
import ru.kulishov.sokratum.data.local.data.mapper.MessageMapper
import ru.kulishov.sokratum.data.remote.ApiClient
import ru.kulishov.sokratum.data.remote.KtorClient.BASE_URL
import ru.kulishov.sokratum.data.remote.KtorClient.JWT_KEY
import ru.kulishov.sokratum.data.remote.KtorClient.client
import ru.kulishov.sokratum.data.remote.authHeader
import ru.kulishov.sokratum.data.remote.fetchWithCache
import ru.kulishov.sokratum.data.remote.model.chats.SendMessageRequest
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.ChatAndMessages
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.MessagesAndUpdateTime
import ru.kulishov.sokratum.domain.model.RefreshError
import ru.kulishov.sokratum.domain.model.RefreshResult
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
        val cachedDomain = cached.map { ChatMapper.toDomain(it) }
        fetchWithCache(cachedDomain, { emit(it) }) {
            val body: List<Chat> = client.get("${BASE_URL}/getAllChats") {
                headers { append(HttpHeaders.Authorization, "Bearer $JWT_KEY") }
            }.body()
            chatDao.replaceAll(body.map { ChatMapper.toEntity(it) })
            if (body.size > 2) body.subList(0, 3) else body
        }
    }

    override suspend fun addChat(chat: Chat): Chat? =
        ApiClient.post("/addChat", chat)

    override suspend fun updateChat(chat: Chat): Chat? =
        ApiClient.post("/updateChat", chat)

    override suspend fun deleteChat(chat: Chat) {
        ApiClient.postRaw("/deleteChat", chat)
    }

    override fun getChat(chatId: Long, spaceId: Long, message: String?): Flow<RefreshResult<Message>> = flow {
        var newChatId = chatId
        if (chatId < 0) {
            if (spaceId < 0) {
                emit(RefreshResult.Error)
                return@flow
            }
            val chat = addChat(
                Chat(
                    -1, spaceId, Clock.System.now().toString(), "",
                    Clock.System.now().toLocalDateTime(TimeZone.UTC)
                )
            )
            if (chat != null) {
                chatDao.addChat(ChatMapper.toEntity(chat))
                newChatId = chat.id
                emit(RefreshResult.Fresh(emptyList()))
            } else {
                emit(RefreshResult.Error)
                return@flow
            }
        } else {
            val cached = messageDao.getMessages(chatId)
            if (cached.isNotEmpty()) {
                emit(RefreshResult.Cached(cached.map { MessageMapper.toDomain(it) }))
            }
            val cachedDomain = cached.map { MessageMapper.toDomain(it) }
            fetchWithCache(cachedDomain, { emit(it) }) {
                val ret: ChatAndMessages = client.get("${BASE_URL}/getChatAndMessages/$chatId") {
                    headers { append(HttpHeaders.Authorization, "Bearer $JWT_KEY") }
                }.body()
                chatDao.addChat(
                    ChatMapper.toEntity(
                        Chat(
                            id = ret.id.toLong(),
                            spaceId = ret.spaceId.toLong(),
                            name = ret.name,
                            systemPrompt = ret.systemPrompt,
                            lastOpen = Instant.fromEpochSeconds(ret.lastOpen).toLocalDateTime(TimeZone.UTC)
                        )
                    )
                )
                messageDao.replaceAll(chatId, ret.messages.messages.map { MessageMapper.toEntity(it) })
                ret.messages.messages
            } ?: return@flow
        }
        if (message != null) {
            val modelName = AIModels.model?.name
            if (modelName == null) {
                emit(RefreshResult.Error)
                return@flow
            }
            try {
                val newMessage = Message(
                    id = -1,
                    idChat = newChatId.toInt(),
                    isModel = false,
                    text = message,
                    sendTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                )
                messageDao.addMessage(MessageMapper.toEntity(newMessage))
                val messageResponse = sendMessage(
                    SendMessageRequest(
                        chatId = newChatId.toInt(),
                        model = modelName,
                        temperature = AIModels.temperature,
                        text = message
                    )
                )
                if (messageResponse != null) {
                    messageDao.addMessage(MessageMapper.toEntity(messageResponse))
                    emit(RefreshResult.Fresh(listOf(newMessage, messageResponse)))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    override suspend fun getChatById(chatId: Long): Chat? =
        ApiClient.get("/getChat/$chatId")

    override suspend fun sendMessage(message: SendMessageRequest): Message? =
        ApiClient.post("/sendMessage", message)

    override suspend fun updateMessages(
        chatId: Long,
        updateId: Long,
    ): MessagesAndUpdateTime? =
        ApiClient.get("/loadMessage/$chatId/$updateId")

    override fun messages(
        chatId: Int,
        spaceId: Long,
        afterMessageId: Long?,
        outgoing: Flow<SendMessageRequest>
    ): Flow<RefreshResult<Message>> = channelFlow {
        var effectiveChatId = chatId
        if (effectiveChatId < 0) {
            if (spaceId < 0) {
                send(RefreshResult.Error)
                return@channelFlow
            }
            val chat = addChat(
                Chat(
                    -1, spaceId, Clock.System.now().toString(), "",
                    Clock.System.now().toLocalDateTime(TimeZone.UTC)
                )
            )
            if (chat == null) {
                send(RefreshResult.Error)
                return@channelFlow
            }
            chatDao.addChat(ChatMapper.toEntity(chat))
            effectiveChatId = chat.id.toInt()
        }

        var cursor = afterMessageId ?: -1L

        if (chatId >= 0) {
            val cached = messageDao.getMessages(effectiveChatId.toLong())
            if (cached.isNotEmpty()) {
                send(RefreshResult.Cached(cached.map { MessageMapper.toDomain(it) }))
                cursor = maxOf(cursor, cached.map { it.id ?: -1L }.max())
            }
            val cachedDomain = cached.map { MessageMapper.toDomain(it) }
            val initial = fetchWithCache(cachedDomain, { send(it) }) {
                val ret: ChatAndMessages = client.get("${BASE_URL}/getChatAndMessages/$effectiveChatId") {
                    headers { append(HttpHeaders.Authorization, "Bearer $JWT_KEY") }
                }.body()
                chatDao.addChat(
                    ChatMapper.toEntity(
                        Chat(
                            id = ret.id.toLong(),
                            spaceId = ret.spaceId.toLong(),
                            name = ret.name,
                            systemPrompt = ret.systemPrompt,
                            lastOpen = Instant.fromEpochSeconds(ret.lastOpen).toLocalDateTime(TimeZone.UTC)
                        )
                    )
                )
                messageDao.replaceAll(
                    effectiveChatId.toLong(),
                    ret.messages.messages.map { MessageMapper.toEntity(it) })
                ret.messages.messages
            } ?: return@channelFlow
            cursor = maxOf(cursor, initial.map { it.id }.maxOrNull() ?: -1L)
        }

        // WebSocket: отправка и приём сообщений, с переподключением при обрыве
        val wsBaseUrl = BASE_URL.replaceFirst("http", "ws")
        var reconnectDelayMs = 1_000L
        while (currentCoroutineContext().isActive) {
            try {
                // Догружаем сообщения, пропущенные за время обрыва
                if (cursor >= 0) {
                    val missed = updateMessages(effectiveChatId.toLong(), cursor)
                    if (missed != null) {
                        missed.messages.forEach { messageDao.addMessage(MessageMapper.toEntity(it)) }
                        if (missed.messages.isNotEmpty()) {
                            send(RefreshResult.Fresh(missed.messages))
                            cursor = maxOf(cursor, missed.messages.map { it.id }.max())
                        }
                    }
                }

                client.webSocket(
                    urlString = "$wsBaseUrl/messages/$effectiveChatId/$cursor",
                    request = { authHeader() }
                ) {
                    reconnectDelayMs = 1_000L
                    coroutineScope {
                        val writer = launch {
                            outgoing.collect { request ->
                                send(
                                    Frame.Text(
                                        Json.encodeToString(
                                            request.copy(chatId = effectiveChatId)
                                        )
                                    )
                                )
                            }
                        }
                        try {
                            for (frame in incoming) {
                                if (frame is Frame.Text) {
                                    val message = Json.decodeFromString<Message>(frame.readText())
                                    messageDao.addMessage(MessageMapper.toEntity(message))
                                    cursor = maxOf(cursor, message.id)
                                    send(RefreshResult.Fresh(listOf(message)))
                                }
                            }
                        } finally {
                            writer.cancel()
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(reconnectDelayMs)
            reconnectDelayMs = (reconnectDelayMs * 2).coerceAtMost(30_000L)
        }
    }

    override suspend fun send(message: Message) {
        // Метод сохранён ради совместимости с ChatRepository; отправка идёт
        // через WebSocket-канал (см. messages()).
    }
}

package ru.kulishov.sokratum.data.remote.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.IOException
import ru.kulishov.sokratum.data.ai.AIModels
import ru.kulishov.sokratum.data.local.dao.ChatDao
import ru.kulishov.sokratum.data.local.dao.MessageDao
import ru.kulishov.sokratum.data.local.data.mapper.ChatMapper
import ru.kulishov.sokratum.data.local.data.mapper.MessageMapper
import ru.kulishov.sokratum.data.remote.ApiClient
import ru.kulishov.sokratum.data.remote.KtorClient.BASE_URL
import ru.kulishov.sokratum.data.remote.KtorClient.JWT_KEY
import ru.kulishov.sokratum.data.remote.KtorClient.client
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
        fetchWithCache(cachedDomain) {
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
            fetchWithCache(cachedDomain) {
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
        outgoing: Flow<Message>
    ): Flow<RefreshResult<Message>> = flow {
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
            if (chat == null) {
                emit(RefreshResult.Error)
                return@flow
            }
            chatDao.addChat(ChatMapper.toEntity(chat))
            return@flow
        }
        val cached = messageDao.getMessages(chatId.toLong())
        if (cached.isNotEmpty()) {
            emit(RefreshResult.Cached(cached.map { MessageMapper.toDomain(it) }))
        }
        val cachedDomain = cached.map { MessageMapper.toDomain(it) }
        fetchWithCache(cachedDomain) {
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
            messageDao.replaceAll(chatId.toLong(), ret.messages.messages.map { MessageMapper.toEntity(it) })
            ret.messages.messages
        }
    }

    override suspend fun send(message: Message) {
        // Метод сохранён ради совместимости с ChatRepository; серверного эндпоинта
        // для прямой отправки сообщения без AI-ответа нет.
    }
}

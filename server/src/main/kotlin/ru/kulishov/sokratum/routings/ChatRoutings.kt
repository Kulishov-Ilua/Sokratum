package ru.kulishov.sokratum.routings

import dev.langchain4j.agentic.AgenticServices
import dev.langchain4j.invocation.InvocationParameters
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.exposedLogger
import ru.kulishov.sokratum.ai.AIModels
import ru.kulishov.sokratum.ai.ExpertAgent
import ru.kulishov.sokratum.ai.SearchTools
import ru.kulishov.sokratum.db.chat.Chat
import ru.kulishov.sokratum.db.chat.ChatDao
import ru.kulishov.sokratum.db.message.Message
import ru.kulishov.sokratum.db.message.MessageDao
import ru.kulishov.sokratum.dto.chat.SendMessageRequest
import java.time.LocalDateTime

private val wsJson = Json { ignoreUnknownKeys = true }

fun Application.configureChatRoutings(
    chatDao: ChatDao,
    messageDao: MessageDao
) {
    routing {
        authenticate("auth-jwt") {
            get("/getLastChats") {
                call.respond(HttpStatusCode.OK, chatDao.getLastChats())
            }

            get("/getAllChats") {
                call.respond(HttpStatusCode.OK, chatDao.getAllChats())
            }
            get("/getChat/{chatId}") {
                val chatId = call.parameters["chatId"]?.toIntOrNull()
                if (chatId != null) {
                    val chat = chatDao.getChat(chatId)
                    if (chat != null) call.respond(HttpStatusCode.OK, chat)
                    else call.respond(HttpStatusCode.NotFound)
                } else call.respond(HttpStatusCode.BadRequest)
            }


            get("/getChatAndMessages/{chatId}") {
                val chatId = call.parameters["chatId"]?.toIntOrNull()
                if (chatId != null) {
                    val chatAndMessages = chatDao.getChatAndMessages(chatId)
                    if (chatAndMessages != null) call.respond(HttpStatusCode.OK, chatAndMessages)
                    else call.respond(HttpStatusCode.NotFound)
                } else call.respond(HttpStatusCode.BadRequest)
            }

            post("/addChat") {
                val body = call.receive<Chat>()
                val respond = chatDao.addChat(body)
                if (respond != null) call.respond(HttpStatusCode.OK, respond)
                else call.respond(HttpStatusCode.Conflict)
            }

            post("/updateChat") {
                val body = call.receive<Chat>()
                val respond = chatDao.updateChat(body)
                if (respond != null) call.respond(HttpStatusCode.OK, respond)
                else call.respond(HttpStatusCode.Conflict)
            }

            post("/deleteChat") {
                val body = call.receive<Chat>()
                val respond = chatDao.deleteChat(body)
                if (respond) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.Conflict)
            }

            get("/getModels") {
                call.respond(HttpStatusCode.OK, AIModels.aiModels.map { it.name })
            }

            post("/sendMessage") {
                val body = call.receive<SendMessageRequest>()
                val newMessage = messageDao.addMessage(
                    Message(
                        id = -1,
                        idChat = body.chatId,
                        isModel = false,
                        text = body.text,
                        sendTime = LocalDateTime.now().toKotlinLocalDateTime()
                    )
                )
                if (newMessage != null) {
                    call.respond(newMessage)
                    launch(Dispatchers.IO) {
                        try {
                            val answer = generateAnswer(chatDao, body)
                            if (answer != null) {
                                messageDao.addMessage(
                                    Message(
                                        id = -1,
                                        idChat = body.chatId,
                                        isModel = true,
                                        text = answer,
                                        sendTime = LocalDateTime.now().toKotlinLocalDateTime()
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            exposedLogger.error(e.message)
                        }
                    }
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            webSocket("/messages/{chatId}/{cursor}") {
                val chatId = call.parameters["chatId"]?.toIntOrNull()
                val cursor = call.parameters["cursor"]?.toLongOrNull()
                if (chatId == null || cursor == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid chatId or cursor"))
                    return@webSocket
                }

                // Догружаем сообщения, пропущенные с момента последнего подключения
                val missed = messageDao.updateMessageList(chatId, cursor)
                missed.messages.forEach { send(Frame.Text(wsJson.encodeToString(it))) }

                try {
                    for (frame in incoming) {
                        if (frame !is Frame.Text) continue
                        val request = try {
                            wsJson.decodeFromString<SendMessageRequest>(frame.readText())
                        } catch (e: Exception) {
                            exposedLogger.warn("Некорректный фрейм в чате $chatId", e)
                            continue
                        }
                        if (request.chatId != chatId) continue

                        val saved = messageDao.addMessage(
                            Message(
                                id = -1,
                                idChat = request.chatId,
                                isModel = false,
                                text = request.text,
                                sendTime = LocalDateTime.now().toKotlinLocalDateTime()
                            )
                        )
                        if (saved != null) {
                            send(Frame.Text(wsJson.encodeToString(saved)))
                        }

                        val answer = withContext(Dispatchers.IO) {
                            try {
                                generateAnswer(chatDao, request)
                            } catch (e: Exception) {
                                exposedLogger.error(e.message)
                                null
                            }
                        }
                        if (answer != null) {
                            val savedAnswer = messageDao.addMessage(
                                Message(
                                    id = -1,
                                    idChat = request.chatId,
                                    isModel = true,
                                    text = answer,
                                    sendTime = LocalDateTime.now().toKotlinLocalDateTime()
                                )
                            )
                            if (savedAnswer != null) {
                                send(Frame.Text(wsJson.encodeToString(savedAnswer)))
                            }
                        }
                    }
                } catch (e: Exception) {
                    exposedLogger.warn("WebSocket чата $chatId завершён с ошибкой", e)
                }
            }

            post("/request") {
                val body = call.receive<String>()
                val models = AIModels.aiModels
                if (models.isNotEmpty() && models.first() != null) {
                    val answer = AIModels.getAiModel(models.first().name, 0.6)?.chat(body) ?: ""
                    if (answer.isNotBlank()) {
                        call.respond(HttpStatusCode.OK, answer)
                    } else call.respond(HttpStatusCode.InternalServerError)
                } else call.respond(HttpStatusCode.NotFound)
            }


            get("/loadMessage/{chatId}/{cursor}") {
                var chatId = call.parameters["chatId"]?.toIntOrNull()
                val cursor = call.parameters["cursor"]?.toLongOrNull()
                if (chatId != null && cursor != null) {
                    call.respond(messageDao.updateMessageList(chatId, cursor))
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

        }
    }
}

/**
 * Генерирует ответ модели для сообщения пользователя.
 * Общая логика для POST /sendMessage и WebSocket-канала чата.
 */
private suspend fun generateAnswer(chatDao: ChatDao, request: SendMessageRequest): String? =
    withContext(Dispatchers.IO) {
        val findModel = AIModels.getAiModel(request.model, request.temperature)
        val findChat = chatDao.getChat(request.chatId)
        if (findModel == null || findChat == null) return@withContext null

        val agent = AgenticServices
            .agentBuilder(ExpertAgent::class.java)
            .chatModel(findModel)
            .outputKey("response")
            .systemMessage(
                if (findChat.systemPrompt.isNotBlank()) findChat.systemPrompt
                else "Ответь на вопрос пользователя, используй инструмент searchKnowledge для поиска информации"
            )
            .tools(SearchTools(findChat.spaceId))
            .chatMemoryProvider { MessageWindowChatMemory.withMaxMessages(20) }
            .build()

        InvocationParameters.from(mapOf("spaceId" to findChat.spaceId))
        agent.chat(request.chatId, request.text)
    }

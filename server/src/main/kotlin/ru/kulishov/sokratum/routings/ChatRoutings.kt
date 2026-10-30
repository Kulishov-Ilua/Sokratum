package ru.kulishov.sokratum.routings

import dev.langchain4j.agentic.AgenticServices
import dev.langchain4j.invocation.InvocationParameters
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.rag.query.Query
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.cancel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.exposedLogger
import ru.kulishov.sokratum.ai.AIModels
import ru.kulishov.sokratum.ai.ExpertAgent
import ru.kulishov.sokratum.ai.SearchTools
import ru.kulishov.sokratum.ai.rag.RAG
import ru.kulishov.sokratum.db.chat.Chat
import ru.kulishov.sokratum.db.chat.ChatDao
import ru.kulishov.sokratum.db.message.Message
import ru.kulishov.sokratum.db.message.MessageDao
import ru.kulishov.sokratum.dto.chat.SendMessageRequest
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock

fun Application.configureChatRoutings(
    chatDao: ChatDao,
    messageDao: MessageDao
) {
    val json = Json { ignoreUnknownKeys = true }
    val rooms = ConcurrentHashMap<Int, MutableSet<DefaultWebSocketServerSession>>()
    val roomMutexes = ConcurrentHashMap<Int, Mutex>()
    fun mutexFor(chatId: Int): Mutex =
        roomMutexes.computeIfAbsent(chatId) { Mutex() }

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
                messageDao.addMessage(
                    Message(
                        id = -1,
                        idChat = body.chatId,
                        isModel = false,
                        text = body.text,
                        sendTime = LocalDateTime.now().toKotlinLocalDateTime()
                    )
                )
                val findModel = AIModels.getAiModel(body.model, body.temperature)
                val findChat = chatDao.getChat(body.chatId)
                if (findModel != null && findChat != null) {
                    //val retriever = RAG.getRetriever(findChat.spaceId)
                    var agent = AgenticServices
                        .agentBuilder(ExpertAgent::class.java)
                        .chatModel(findModel)
                        .outputKey("response")
                        .systemMessage(if (findChat.systemPrompt.isNotBlank()) findChat.systemPrompt else "Ответь на вопрос пользователя, используй инструмент searchKnowledge для поиска информации")
                        .tools(SearchTools(findChat.spaceId))
                        .chatMemoryProvider { chatId ->
                            MessageWindowChatMemory.withMaxMessages(20)
                        }

                        //.contentRetriever(retriever)
                        .build()

                    val params = InvocationParameters.from(mapOf("spaceId" to findChat.spaceId))
                    val answer = agent.chat(body.chatId,body.text)


                    //val answer = agent.chat(body.text)
                    if (answer != null) {
                        val message = messageDao.addMessage(
                            Message(
                                id = -1,
                                idChat = body.chatId,
                                isModel = true,
                                text = answer,
                                sendTime = LocalDateTime.now().toKotlinLocalDateTime()
                            )
                        )
                        if (message != null) {
                            call.respond(HttpStatusCode.OK, message)
                        } else {
                            call.respond(HttpStatusCode.NotModified)
                        }
                    } else {
                        call.respond(HttpStatusCode.RequestTimeout)
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            post("/request") {
                val body = call.receive<String>()
                val models = AIModels.aiModels
                if (models.isNotEmpty() && models.first() != null) {
                    val answer = AIModels.getAiModel(models.first().name,0.6)?.chat(body) ?: ""
                    if (answer.isNotBlank()) {
                        call.respond(HttpStatusCode.OK, answer)
                    } else call.respond(HttpStatusCode.InternalServerError)
                } else call.respond(HttpStatusCode.NotFound)
            }

            webSocket("/messages/{chatId}/{cursor}") {
                var chatId = call.parameters["chatId"]?.toIntOrNull()
                val cursor = call.parameters["cursor"]?.toLongOrNull()

                if (chatId != null && cursor != null) {
                    val missed = messageDao.updateMessageList(chatId, cursor)
                    missed.messages.forEach { send(Frame.Text(json.encodeToString(it))) }

                    val room = rooms.computeIfAbsent(chatId) { ConcurrentHashMap.newKeySet() }
                    val mutex = mutexFor(chatId)
                    try {
                        for (frame in incoming) {
                            when (frame) {
                                is Frame.Text -> {
                                    val message = json.decodeFromString<SendMessageRequest>(frame.readText())

                                    if (message.chatId != chatId) continue

                                    val saved = messageDao.addMessage(Message(
                                        id = -1,
                                        idChat = message.chatId,
                                        isModel = false,
                                        text = message.text,
                                        sendTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                                    ))

                                    val broadcast = Frame.Text(json.encodeToString(saved))
                                    val recipients = mutex.withLock { room.toList() }
                                    recipients.forEach { session ->
                                        runCatching { session.send(broadcast) }
                                    }

                                    val findModel = AIModels.getAiModel(message.model, message.temperature)
                                    val findChat = chatDao.getChat(chatId)
                                    if (findModel != null && findChat != null) {
                                        val retriever = RAG.getRetriever(findChat.spaceId)


                                        var agent = AgenticServices
                                            .agentBuilder(ExpertAgent::class.java)
                                            .chatModel(findModel)
                                            .outputKey("response")
                                            .systemMessage(if (findChat.systemPrompt.isNotBlank()) findChat.systemPrompt else "Ответь на вопрос пользователя")
                                            .contentRetriever(retriever)
                                            .build()

                                        val params = InvocationParameters.from(mapOf("spaceId" to findChat.spaceId))
                                        val answer = agent.chat(findChat.id,message.text)
                                        if (answer != null) {
                                            val modelMessage = messageDao.addMessage(
                                                Message(
                                                    id = -1,
                                                    idChat = chatId,
                                                    isModel = true,
                                                    text = answer,
                                                    sendTime = LocalDateTime.now()
                                                        .toKotlinLocalDateTime()
                                                )
                                            )
                                            if (modelMessage != null) {
                                                val newMessage = messageDao.addMessage(modelMessage)
                                                if(newMessage!=null){
                                                    val aiFrame = Frame.Text(json.encodeToString(newMessage))
                                                    recipients.forEach { session ->
                                                        runCatching { session.send(aiFrame) }
                                                    }
                                                }
                                            }
                                        }
                                    }


                                }

                                is Frame.Close -> break
                                else -> {}
                            }
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        synchronized(room) {
                            room.remove(this)
                            if (room.isEmpty()) rooms.remove(chatId)
                        }
                    }
                } else {
                    cancel("Invalid chatId or cursor")
                    return@webSocket
                }
            }

        }
    }
}
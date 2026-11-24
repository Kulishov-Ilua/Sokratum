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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalDateTime
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
                if(newMessage!=null){
                    call.respond(newMessage)
                    launch(Dispatchers.IO) {
                        try {
                            val findModel = AIModels.getAiModel(body.model, body.temperature)
                            val findChat = chatDao.getChat(body.chatId)
                            if (findModel != null && findChat != null) {
                                var agent = AgenticServices
                                    .agentBuilder(ExpertAgent::class.java)
                                    .chatModel(findModel)
                                    .outputKey("response")
                                    .systemMessage(if (findChat.systemPrompt.isNotBlank()) findChat.systemPrompt else "Ответь на вопрос пользователя, используй инструмент searchKnowledge для поиска информации")
                                    .tools(SearchTools(findChat.spaceId))
                                    .chatMemoryProvider { chatId ->
                                        MessageWindowChatMemory.withMaxMessages(20)
                                    }

                                    .build()

                                val params =
                                    InvocationParameters.from(mapOf("spaceId" to findChat.spaceId))
                                val answer = agent.chat(body.chatId, body.text)


                                //val answer = agent.chat(body.text)
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
                            }

                        }catch (e: Exception){
                            exposedLogger.error(e.message)
                        }

                    }

                }else{
                    call.respond(HttpStatusCode.InternalServerError)
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


            get("/loadMessage/{chatId}/{cursor}"){
                var chatId = call.parameters["chatId"]?.toIntOrNull()
                val cursor = call.parameters["cursor"]?.toLongOrNull()
                if (chatId != null && cursor != null) {
                    call.respond(messageDao.updateMessageList(chatId,cursor))
                }else{
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

        }
    }
}
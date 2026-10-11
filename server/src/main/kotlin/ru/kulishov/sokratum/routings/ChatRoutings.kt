package ru.kulishov.sokratum.routings

import dev.langchain4j.agentic.AgenticServices
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.datetime.toKotlinLocalDateTime
import ru.kulishov.sokratum.ai.AIModels
import ru.kulishov.sokratum.ai.ExpertAgent
import ru.kulishov.sokratum.ai.rag.RAG
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
                call.respond(HttpStatusCode.OK, AIModels.models.keys)
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
                val findModel = AIModels.models[body.model]
                val findChat = chatDao.getChat(body.chatId)
                if (findModel != null && findChat != null) {
                    val retriever = RAG.getRetriever(findChat.spaceId)

                    var agent = AgenticServices
                        .agentBuilder(ExpertAgent::class.java)
                        .chatModel(findModel)
                        .outputKey("response")
                        .systemMessage(if (findChat.systemPrompt.isNotBlank()) findChat.systemPrompt else "Ответь на вопрос пользователя")
                        .contentRetriever(retriever)
                        .build()
                    val answer = agent.chat(body.text)
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
                val models = AIModels.models.values
                if(models.isNotEmpty()&&models.first()!=null){
                    val answer = models.first()?.chat(body)?: ""
                    if(answer.isNotBlank()){
                        call.respond(HttpStatusCode.OK, answer)
                    }else call.respond(HttpStatusCode.InternalServerError)
                }else call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
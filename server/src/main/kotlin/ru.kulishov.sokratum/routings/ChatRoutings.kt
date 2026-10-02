package ru.kulishov.sokratum.routings

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import ru.kulishov.sokratum.db.chat.Chat
import ru.kulishov.sokratum.db.chat.ChatDao

fun Application.configureChatRoutings(
    chatDao: ChatDao
) {
    routing {
        authenticate("auth-jwt") {
            get("/getLastChats") {
                call.respond(HttpStatusCode.OK, chatDao.getLastChats())
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
        }
    }
}
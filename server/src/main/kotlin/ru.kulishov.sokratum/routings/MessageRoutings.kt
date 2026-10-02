package ru.kulishov.sokratum.routings

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import ru.kulishov.sokratum.db.message.Message
import ru.kulishov.sokratum.db.message.MessageDao

fun Application.configureMessageRoutings(
    messageDao: MessageDao
) {
    routing {
        authenticate("auth-jwt") {
            post("/addSpace") {
                val body = call.receive<Message>()
                val respond = messageDao.addMessage(body)
                if (respond != null) call.respond(HttpStatusCode.OK, respond)
                else call.respond(HttpStatusCode.Conflict)
            }
        }
    }
}
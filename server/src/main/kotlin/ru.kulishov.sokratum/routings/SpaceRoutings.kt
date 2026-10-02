package ru.kulishov.sokratum.routings

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import ru.kulishov.sokratum.db.space.Space
import ru.kulishov.sokratum.db.space.SpaceDao

fun Application.configureSpaceRouting(
    spaceDao: SpaceDao
){
    routing {
        authenticate("auth-jwt") {
            get("/getSpaces") {
                val spaces = spaceDao.getSpaces()
                call.respond(HttpStatusCode.OK,spaces)
            }
            get("/getSpaceAndChats/{spaceId}"){
                val spaceId = call.parameters["spaceId"]?.toIntOrNull()
                if(spaceId!=null){
                    val spaceAndChats = spaceDao.getSpaceAndChats(spaceId)
                    if(spaceAndChats!=null) call.respond(HttpStatusCode.OK, spaceAndChats)
                    else call.respond(HttpStatusCode.NotFound)
                }else call.respond(HttpStatusCode.BadRequest)
            }
            post("/addSpace") {
                val body = call.receive<Space>()
                val respond = spaceDao.addSpace(body)
                if(respond!=null) call.respond(HttpStatusCode.OK, respond)
                else call.respond(HttpStatusCode.Conflict)
            }

            post("/updateSpace") {
                val body = call.receive<Space>()
                val respond = spaceDao.updateSpace(body)
                if(respond!=null) call.respond(HttpStatusCode.OK, respond)
                else call.respond(HttpStatusCode.Conflict)
            }

            post("/deleteSpace") {
                val body = call.receive<Space>()
                val respond = spaceDao.deleteSpace(body)
                if(respond) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.Conflict)
            }
        }
    }
}
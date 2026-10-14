package ru.kulishov.sokratum.routings

import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import ru.kulishov.sokratum.ai.rag.FileStorrage
import ru.kulishov.sokratum.db.file.File
import ru.kulishov.sokratum.db.file.FileDao
import kotlin.uuid.Uuid

fun Application.configureFileRoutings(
    fileDao: FileDao
) {
    routing {
        authenticate("auth-jwt") {
            get("/getFilesData/{spaceId}") {
                val spaceId = call.parameters["spaceId"]?.toIntOrNull()
                if (spaceId != null) {
                    val files = fileDao.getSpaceFiles(spaceId)
                    if (files != null) call.respond(HttpStatusCode.OK, files)
                    else call.respond(HttpStatusCode.NotFound)
                } else call.respond(HttpStatusCode.BadRequest)
            }

        }
    }
}
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

            post("/addFile/{spaceId}") {
                val spaceId = call.parameters["spaceId"]?.toIntOrNull()
                if (spaceId != null) {
                    var saved: File? = null

                    call.receiveMultipart().forEachPart { part ->
                        if (part is PartData.FileItem) {
                            val id = Uuid.random()
                            val originalName = part.originalFileName ?: "unnamed"
                            val contentType =
                                part.contentType?.toString() ?: "application/octet-stream"

                            val size = FileStorrage.save(id, part.provider())

                            saved = fileDao.addFile(
                                id = id,
                                spaceId= spaceId,
                                name = originalName,
                                size = size,
                                contentType = contentType,
                                path = FileStorrage.fileOf(id).absolutePath
                            )
                        }
                        part.dispose()
                    }

                    val doc =
                        saved ?: return@post call.respond(HttpStatusCode.BadRequest, "no file")
                    call.respond(HttpStatusCode.Created, doc)
                } else call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
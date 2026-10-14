    package ru.kulishov.sokratum.routings

import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.jetbrains.exposed.v1.core.exposedLogger
import ru.kulishov.sokratum.ai.rag.FileStorrage
import ru.kulishov.sokratum.db.file.File
import ru.kulishov.sokratum.db.file.FileDao
import ru.kulishov.sokratum.db.space.Space
import ru.kulishov.sokratum.db.space.SpaceDao
import ru.kulishov.sokratum.dto.space.SpaceAndFiles
import kotlin.uuid.Uuid

    fun Application.configureSpaceRouting(
    spaceDao: SpaceDao,
    fileDao: FileDao
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
            get("/getSpaceAndFiles/{spaceId}"){
                val spaceId = call.parameters["spaceId"]?.toIntOrNull()
                if(spaceId!=null&&spaceId>0){
                    val space = spaceDao.getSpaceById(spaceId)
                    val files = fileDao.getSpaceFiles(spaceId)
                    if(space!=null) call.respond(HttpStatusCode.OK, SpaceAndFiles(space,files))
                    else call.respond(HttpStatusCode.NotFound)
                }else call.respond(HttpStatusCode.BadRequest)
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
                    val space = spaceDao.getSpaceById(spaceId)
                    val files = fileDao.getSpaceFiles(spaceId)
                    if(space!=null) call.respond(HttpStatusCode.OK, SpaceAndFiles(space,files))
                    else call.respond(HttpStatusCode.NotFound)
                } else call.respond(HttpStatusCode.BadRequest)
            }

            post("/deleteFile/{spaceId}/{fileId}"){
                val spaceId = call.parameters["spaceId"]?.toIntOrNull()
                val fileId = call.parameters["fileId"]
                if(spaceId==null||fileId==null){
                    call.respond(HttpStatusCode.BadRequest)
                }else{
                    val ok = fileDao.deleteFile(Uuid.parse(fileId))
                    if(ok){
                        val space = spaceDao.getSpaceById(spaceId)
                        val files = fileDao.getSpaceFiles(spaceId)
                        if(space!=null) call.respond(HttpStatusCode.OK, SpaceAndFiles(space,files))
                        else call.respond(HttpStatusCode.NotFound)
                    }else{
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
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
package ru.kulishov.sokratum

import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import kotlinx.serialization.json.Json
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import ru.kulishov.sokratum.ai.AIModels
import ru.kulishov.sokratum.ai.rag.FileStorrage
import ru.kulishov.sokratum.db.DBFactory
import ru.kulishov.sokratum.db.DBFactory.chatDao
import ru.kulishov.sokratum.db.DBFactory.fileDao
import ru.kulishov.sokratum.db.DBFactory.messageDao
import ru.kulishov.sokratum.db.DBFactory.spaceDao
import ru.kulishov.sokratum.db.DBFactory.userDao
import ru.kulishov.sokratum.db.user.UserDao
import ru.kulishov.sokratum.jwt.JWTConfig
import ru.kulishov.sokratum.routings.configureAuthRouting
import ru.kulishov.sokratum.routings.configureChatRoutings
import ru.kulishov.sokratum.routings.configureFileRoutings
import ru.kulishov.sokratum.routings.configureSpaceRouting

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)

        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Accept)
        allowHeader(HttpHeaders.Origin)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    JWTConfig.init(environment)
    DBFactory.init(environment)
    AIModels.init(environment)
    FileStorrage.init()

    configureSecurity(environment)
    configureAuthRouting(userDao)
    configureChatRoutings(chatDao, messageDao)
    configureSpaceRouting(spaceDao)
    configureFileRoutings(fileDao)



    routing {
        get("/"){
            call.respond("Hello Sokratum!!")
        }

    }

}
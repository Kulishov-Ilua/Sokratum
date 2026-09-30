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

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
    // embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
    //   .start(wait = true)
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
//    JwtConfig.init(environment)
//    DBFactory.init(environment)
//    val userDao = UserDao()
//    val courseDao = CourseDao()
//    val taskDao = TaskDao()
//    val fileDao = FileDao()
//    val chatDao = ChatDao()
//    configureSecurity(environment)
//    configureAuthRouting(userDao)
//    configureCourseRouting(courseDao, userDao, fileDao)
//    configureTaskRouting(taskDao)
//    configureChatRouting(chatDao, fileDao)
//    AIAgent.init(environment)
    routing {
        get("/"){
            call.respond("Hello ktor")
        }
    }

}
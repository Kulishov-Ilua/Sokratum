package ru.kulishov.sokratum.routings

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import ru.kulishov.sokratum.db.user.UserDao
import ru.kulishov.sokratum.dto.AuthResponse
import ru.kulishov.sokratum.dto.LoginRequest
import ru.kulishov.sokratum.dto.RegistrationRequest
import ru.kulishov.sokratum.jwt.JWTConfig
import java.time.LocalDateTime

fun Application.configureAuthRouting(
    userDao: UserDao
){
    routing {
        get("/regeneratekey"){
            if((LocalDateTime.now().second- JWTConfig.lastUpdateKey)>15){
                JWTConfig.lastUpdateKey= LocalDateTime.now().second
                JWTConfig.generateRegistrationKey()
                call.respond(HttpStatusCode.OK)
            }else{
                call.respond(HttpStatusCode.Locked)
            }

        }
        post("/deletekey"){
            JWTConfig.deleteRegistrationKey()
            call.respond(HttpStatusCode.OK)
        }
        post("/registration"){
            val request = call.receive<RegistrationRequest>()
            if(JWTConfig.checkRegistrationKey(request.key)){
                val statement = userDao.createUser(request.device,"admin",request.login, request.password)
                if(statement){
                    call.respond(HttpStatusCode.OK)
                }else{
                    call.respond(HttpStatusCode.Locked)
                }
            }else{
                call.respond(HttpStatusCode.Locked)
            }
        }

        post("/login") {

            val request = call.receive<LoginRequest>()


            val user = userDao.authUser(request.login, request.password)

            if(user!=null){
                val token = JWTConfig.generateToken(user.login,user.role, user.id)
                call.respond(
                    HttpStatusCode.OK,
                    AuthResponse(
                        token = token,
                        login = user.login,
                        expiresIn = 3600000,
                        role = user.role,
                        userId = user.id
                    )
                )
            }else{
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("error" to "Invalid username or password")
                )
            }
        }
        authenticate("auth-jwt") {
            get("/getRegistrationKey"){
                JWTConfig.generateRegistrationKey()
                call.respond(HttpStatusCode.OK, JWTConfig.getRegistrationKey())
            }

            get("/devices"){
                call.respond(HttpStatusCode.OK, userDao.getDevicesData())
            }
        }
    }

}
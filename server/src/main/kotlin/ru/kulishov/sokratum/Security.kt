package ru.kulishov.sokratum

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond

fun Application.configureSecurity(environment: ApplicationEnvironment){
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT.require(
                    Algorithm.HMAC256(
                        environment.config.property("jwt_secret").getString()
                    )
                )
                    .withIssuer("Sokratum")
                    .build()
            )

            validate { credential ->
                val username = credential.payload.getClaim("login").asString()
                val role = credential.payload.getClaim("role").asString()


                if (username != null && !username.isBlank()) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }

            }
            challenge { defaultScheme, realm ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf(
                        "error" to "The token is invalid or expired",
                        "status" to 401
                    )
                )
            }

        }
    }
}
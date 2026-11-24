package ru.kulishov.sokratum.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime
import java.util.Date
import kotlin.uuid.Uuid.Companion.random


object JWTConfig {
    private var SECRET = ""
    private var algorithm: Algorithm? = null
    const val EXPIRES_IN = 3600000L

    private var RegistrationPassword: String? = null

    var lastUpdateKeyMillis: Long = 0
        private set

    fun canRegenerateKey(cooldownMillis: Long = 15_000): Boolean =
        System.currentTimeMillis() - lastUpdateKeyMillis >= cooldownMillis

    fun markKeyRegenerated() {
        lastUpdateKeyMillis = System.currentTimeMillis()
    }

    internal fun resetKeyCooldown() {
        lastUpdateKeyMillis = 0
    }

    fun generateRegistrationKey() {
        RegistrationPassword =
            LocalDateTime.now()
                .toKotlinLocalDateTime().nanosecond.toString() + random() + LocalDateTime.now()
                .toKotlinLocalDateTime().minute + random()
        println("New registration key: $RegistrationPassword")
    }

    fun deleteRegistrationKey(){
        RegistrationPassword=null
    }

    fun checkRegistrationKey(key: String): Boolean{
        if(RegistrationPassword!=null&&key==RegistrationPassword){
            deleteRegistrationKey()
            return true
        }
        return false
    }

    fun getRegistrationKey():String = RegistrationPassword?:""
    fun init(environment: ApplicationEnvironment) {
        SECRET = environment.config.property("jwt_secret").getString()
        algorithm = Algorithm.HMAC256(SECRET)
    }

    fun generateToken(login: String, role: String, userId: Int): String {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer("Sokratum")
            .withClaim("login", login)
            .withClaim("userId", userId)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + EXPIRES_IN))
            .withIssuedAt(Date())
            .sign(algorithm)
    }

}


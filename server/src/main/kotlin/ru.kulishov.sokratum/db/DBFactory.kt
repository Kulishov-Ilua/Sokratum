package ru.kulishov.sokratum.db

import io.ktor.server.application.ApplicationEnvironment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.kulishov.sokratum.db.user.Users

lateinit var keyCrypt: String
lateinit var salt: String

object DBFactory {
    fun init(environment: ApplicationEnvironment) {
        val url = environment.config.property("db.url").getString()
        val user = environment.config.property("db.user").getString()
        val password = environment.config.property("db.pass").getString()
        val pool = environment.config.property("db.pool").getString().toInt()

        keyCrypt = environment.config.property("db.key").getString()
        salt = environment.config.property("db.salt").getString()

        Database.connect(
            url = url,
            driver = "org.postgresql.Driver",
            user = user,
            password = password
        )


        transaction {
            SchemaUtils.create(Users)
        }
    }
}

suspend fun <T> query(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction { block() }
}
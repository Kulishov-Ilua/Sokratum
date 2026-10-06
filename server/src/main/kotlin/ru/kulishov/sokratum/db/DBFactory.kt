package ru.kulishov.sokratum.db

import io.ktor.server.application.ApplicationEnvironment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.kulishov.sokratum.db.chat.ChatDao
import ru.kulishov.sokratum.db.chat.Chats
import ru.kulishov.sokratum.db.file.FileDao
import ru.kulishov.sokratum.db.file.Files
import ru.kulishov.sokratum.db.message.MessageDao
import ru.kulishov.sokratum.db.message.Messages
import ru.kulishov.sokratum.db.space.SpaceDao
import ru.kulishov.sokratum.db.space.Spaces
import ru.kulishov.sokratum.db.user.UserDao
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
            SchemaUtils.create(Chats)
            SchemaUtils.create(Spaces)
            SchemaUtils.create(Messages)
            SchemaUtils.create(Files)

//            Spaces.insert {
//                it[Spaces.id] = 0
//                it[Spaces.name]=""
//            }

        }
    }

    val userDao = UserDao()
    val chatDao = ChatDao()

    val messageDao = MessageDao()

    val spaceDao = SpaceDao()

    val fileDao = FileDao()
}

suspend fun <T> query(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction { block() }
}
package ru.kulishov.sokratum.db.user

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.crypt.Algorithms
import org.jetbrains.exposed.v1.crypt.encryptedVarchar
import ru.kulishov.sokratum.db.keyCrypt
import ru.kulishov.sokratum.db.salt

object Users: Table(){
    val id = integer("id").autoIncrement()
    val login = varchar("login", 120)
    val role =varchar("role", 20)
    val password = encryptedVarchar(
        name = "password",
        cipherTextLength = 1024,
        encryptor = Algorithms.AES_256_PBE_CBC(keyCrypt, salt)
    )
    val device = varchar("device", 120)

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class User(
    val id:Int,
    val login:String,
    val role: String,
    val password:String,
    val device:String
)
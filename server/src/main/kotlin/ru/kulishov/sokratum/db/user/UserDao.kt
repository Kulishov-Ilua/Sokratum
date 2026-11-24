package ru.kulishov.sokratum.db.user

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import ru.kulishov.sokratum.db.query
import ru.kulishov.sokratum.dto.auth.DeviceDataResponse
import ru.kulishov.sokratum.security.PasswordHasher

class UserDao {
    fun rowToUser(row: ResultRow) = User(
        id = row[Users.id],
        login = row[Users.login],
        role = row[Users.role],
        password = row[Users.password],
        device = row[Users.device]
    )

    suspend fun authUser(login: String, password: String): User? = query {
        val user = Users.selectAll().where { Users.login eq login }.map { rowToUser(it) }
            .singleOrNull() ?: return@query null
        when {
            PasswordHasher.isHashed(user.password) ->
                if (PasswordHasher.verify(password, user.password)) user else null
            user.password == password -> {
                // Миграция устаревших паролей, хранившихся в открытом виде
                Users.update({ Users.id eq user.id }) {
                    it[Users.password] = PasswordHasher.hash(user.password)
                }
                user
            }
            else -> null
        }
    }

    suspend fun createUser(device: String, role: String, login: String, password: String): Boolean =
        query {
            val userFind = Users.selectAll().where { Users.login eq login }.map { rowToUser(it) }
                .singleOrNull()
            if (userFind == null) {
                Users.insert {
                    it[Users.role] = role
                    it[Users.login] = login
                    it[Users.password] = PasswordHasher.hash(password)
                    it[Users.device] = device
                }
                return@query true
            } else {
                return@query false
            }
        }

    suspend fun getDevicesData():List<DeviceDataResponse> = query {
        return@query Users.selectAll().map { DeviceDataResponse(
            id = it[Users.id],
            login = it[Users.login],
            device = it[Users.device]
        ) }
    }

}
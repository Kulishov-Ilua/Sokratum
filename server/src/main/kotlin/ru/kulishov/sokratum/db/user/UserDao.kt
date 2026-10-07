package ru.kulishov.sokratum.db.user

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import ru.kulishov.sokratum.db.query
import ru.kulishov.sokratum.dto.auth.DeviceDataResponse

class UserDao {
    fun rowToUser(row: ResultRow) = User(
        id = row[Users.id],
        login = row[Users.login],
        role = row[Users.role],
        password = row[Users.password],
        device = row[Users.device]
    )

    suspend fun authUser(login: String, password: String): User? = query {
        Users
            .selectAll()
            .where { (Users.login eq login) }
            .map {
                rowToUser(it)
            }
            .firstOrNull { it.password == password }
    }

    suspend fun createUser(device: String, role: String, login: String, password: String): Boolean =
        query {
            val userFind = Users.selectAll().where { Users.login eq login }.map { rowToUser(it) }
                .singleOrNull()
            if (userFind == null) {
                val statement = Users.insert {
                    it[Users.role] = role
                    it[Users.login] = login
                    it[Users.password] = password
                    it[Users.device]=device
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
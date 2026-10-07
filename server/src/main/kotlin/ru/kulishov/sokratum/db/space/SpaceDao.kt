package ru.kulishov.sokratum.db.space

import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import ru.kulishov.sokratum.db.chat.Chat
import ru.kulishov.sokratum.db.chat.Chats
import ru.kulishov.sokratum.db.query
import java.time.LocalDateTime

class SpaceDao {
    fun rowToSpace(row: ResultRow) = Space(
        id = row[Spaces.id],
        name = row[Spaces.name],
        lastOpen = row[Spaces.lastOpen].toKotlinLocalDateTime()
    )

    fun rowToChat(row: ResultRow) = Chat(
        id = row[Chats.id],
        spaceId = row[Chats.spaceId],
        name = row[Chats.name],
        systemPrompt = row[Chats.systemPrompt],
        lastOpen = row[Chats.lastOpen].toKotlinLocalDateTime()
    )

    suspend fun getSpaces(): List<Space> =
        query { Spaces.selectAll().orderBy(Spaces.lastOpen, SortOrder.DESC).map { rowToSpace(it) } }

    suspend fun getSpaceAndChats(spaceId: Int): SpaceAndChats? = query {
        val space =
            Spaces.selectAll().where { Spaces.id eq spaceId }.map { rowToSpace(it) }.firstOrNull()
        if (space != null) {
            val chats = Chats.selectAll().where(Chats.spaceId eq spaceId).map { rowToChat(it) }
            return@query SpaceAndChats(
                id = space.id,
                name = space.name,
                lastOpen = space.lastOpen,
                chats = chats
            )
        } else return@query null
    }

    suspend fun addSpace(space: Space): Space? = query {
        val insert = Spaces.insert {
            it[name] = space.name
            it[lastOpen] = LocalDateTime.now()
        }.resultedValues?.map { rowToSpace(it) }?.firstOrNull() ?: return@query null
        return@query insert

    }

    suspend fun updateSpace(space: Space): Space? = query {
        val ret = Spaces.update({ Spaces.id eq space.id }) {
            it[name] = space.name
        }.let {
            Spaces.selectAll().where { Spaces.id eq space.id }.map { rowToSpace(it) }.singleOrNull()
        }
        return@query ret
    }

    suspend fun deleteSpace(space: Space): Boolean = query {
        Spaces.deleteWhere { Spaces.id eq space.id }
        return@query true
    }


}
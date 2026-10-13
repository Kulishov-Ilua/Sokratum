package ru.kulishov.sokratum.data.local.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.kulishov.sokratum.data.local.data.entity.ChatEntity
import ru.kulishov.sokratum.data.local.data.entity.SpaceEntity
import ru.kulishov.sokratum.data.local.data.mapper.ChatMapper
import ru.kulishov.sokratum.data.local.data.mapper.SpaceMapper
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.model.SpaceAndChats
import kotlin.time.Clock

@Dao
interface SpaceDao {
    @Query("SELECT * FROM spaces ORDER BY lastOpen DESC")
    suspend fun getSpaces(): List<SpaceEntity>

    @Query("SELECT * FROM spaces WHERE id = :spaceId")
    suspend fun getSpaceById(spaceId: Long): SpaceEntity?

    @Query("DELETE FROM spaces")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SpaceEntity>)

    @Query("Select * from chats where idSpace = :spaceId")
    suspend fun getChatsData(spaceId: Long): List<ChatEntity>

    @Transaction
    suspend fun getSpaceAndChats(spaceId: Long): SpaceAndChats? {
        val space = getSpaceById(spaceId)
        val spaceParse = if(space!=null) SpaceMapper.toDomain(space) else Space(-100,"", Clock.System.now().toLocalDateTime(
            TimeZone.UTC))
        val chats = getChatsData(spaceId)
        return SpaceAndChats(
            id = spaceParse.id.toInt(),
            name = spaceParse.name,
            lastOpen = spaceParse.lastOpen,
            chats = chats.map { ChatMapper.toDomain(it) }
        )
    }

    @Transaction
    suspend fun replaceAll(items: List<SpaceEntity>) {
        deleteAll()
        insertAll(items)
    }

    @Query("SELECT * from spaces where id = :spaceId")
    suspend fun getSpace(spaceId: Long): SpaceEntity?

    @Insert
    suspend fun addSpace(space: SpaceEntity): Long

    @Update
    suspend fun updateSpace(space: SpaceEntity)

    @Delete
    suspend fun deleteSpace(space: SpaceEntity)
}
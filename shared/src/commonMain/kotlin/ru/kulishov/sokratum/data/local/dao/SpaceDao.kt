package ru.kulishov.sokratum.data.local.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.data.local.data.entity.SpaceEntity
import ru.kulishov.sokratum.domain.model.SpaceWithoutStorage

@Dao
interface SpaceDao {
    @Query("SELECT id, name, path, lastOpen FROM spaces ORDER BY lastOpen DESC")
    fun getSpaces(): Flow<List<SpaceWithoutStorage>>

    @Query("SELECT * from spaces where id = :spaceId")
    suspend fun getSpace(spaceId: Long): SpaceEntity?

    @Insert
    suspend fun addSpace(space: SpaceEntity): Long

    @Update
    suspend fun updateSpace(space: SpaceEntity): Long

    @Delete
    suspend fun deleteSpace(space: SpaceEntity)
}
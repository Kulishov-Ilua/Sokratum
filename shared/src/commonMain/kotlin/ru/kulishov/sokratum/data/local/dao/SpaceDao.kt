package ru.kulishov.sokratum.data.local.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.data.local.data.entity.SpaceEntity

@Dao
interface SpaceDao {
    @Query("SELECT * FROM spaces ORDER BY lastOpen DESC")
    suspend fun getSpaces(): List<SpaceEntity>

    @Query("DELETE FROM spaces")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SpaceEntity>)

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
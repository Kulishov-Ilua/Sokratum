package ru.kulishov.sokratum.data.local.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.data.local.data.entity.ModelEntity

@Dao
interface ModelDao {
    @Query("SELECT * from models")
    fun getModels(): Flow<List<ModelEntity>>

    @Query("SELECT * from models where id = :id")
    suspend fun getModel(id: Long): ModelEntity?

    @Insert
    suspend fun addModel(model: ModelEntity): Long

    @Delete
    suspend fun deleteModel(model: ModelEntity): Long
}
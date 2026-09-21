package ru.kulishov.sokratum.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.Model

interface ModelRepository {
    fun getModels(): Flow<List<Model>>
    suspend fun addModel(model: Model): Model?
    suspend fun deleteModel(model: Model)
}
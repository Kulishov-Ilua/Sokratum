package ru.kulishov.sokratum.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.kulishov.sokratum.data.local.dao.ModelDao
import ru.kulishov.sokratum.data.local.data.mapper.ModelMapper
import ru.kulishov.sokratum.domain.model.Model
import ru.kulishov.sokratum.domain.repository.ModelRepository

class ModelRepositoryImpl(
    private val modelDao: ModelDao
) : ModelRepository {
    override fun getModels(): Flow<List<Model>> =
        modelDao.getModels().map { entities -> entities.map { ModelMapper.toDomain(it) } }

    override suspend fun addModel(model: Model): Model? {
        val id = modelDao.addModel(ModelMapper.toEntity(model))
        val findModel = modelDao.getModel(id)
        return if (findModel != null) ModelMapper.toDomain(findModel) else null
    }

    override suspend fun deleteModel(model: Model) =
        modelDao.deleteModel(ModelMapper.toEntity(model))

}
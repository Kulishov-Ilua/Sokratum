package ru.kulishov.sokratum.domain.usecase.model

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.Model
import ru.kulishov.sokratum.domain.repository.ModelRepository

class GetModelsUseCase(
    private val repository: ModelRepository
) {
    operator fun invoke(): Flow<List<Model>> = repository.getModels()
}
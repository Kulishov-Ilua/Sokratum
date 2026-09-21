package ru.kulishov.sokratum.domain.usecase.space

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.repository.SpaceRepository

class GetSpacesDataUseCase(
    private val repository: SpaceRepository
) {
    operator fun invoke(): Flow<List<Space>> = repository.getSpacesData()
}
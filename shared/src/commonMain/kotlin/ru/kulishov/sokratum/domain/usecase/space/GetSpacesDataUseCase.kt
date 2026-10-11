package ru.kulishov.sokratum.domain.usecase.space

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.model.SpaceWithoutStorage
import ru.kulishov.sokratum.domain.repository.SpaceRepository

class GetSpacesDataUseCase(
    private val repository: SpaceRepository
) {
    operator fun invoke(): Flow<RefreshResult<Space>> = repository.getSpacesData()
}
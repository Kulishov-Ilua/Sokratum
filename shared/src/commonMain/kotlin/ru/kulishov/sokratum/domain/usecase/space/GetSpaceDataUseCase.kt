package ru.kulishov.sokratum.domain.usecase.space

import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.repository.SpaceRepository

class GetSpaceDataUseCase(
    private val repository: SpaceRepository
) {
    suspend operator fun invoke(id: Long): Space? = repository.getSpaceData(id)
}
package ru.kulishov.sokratum.domain.usecase.space

import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.repository.SpaceRepository

class AddSpaceUseCase(
    private val repository: SpaceRepository
) {
    suspend operator fun invoke(space: Space): Space? = repository.addSpace(space)
}
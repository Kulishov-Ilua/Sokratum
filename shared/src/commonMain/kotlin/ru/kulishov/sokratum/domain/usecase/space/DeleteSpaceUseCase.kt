package ru.kulishov.sokratum.domain.usecase.space

import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.repository.SpaceRepository

class DeleteSpaceUseCase(
    private val repository: SpaceRepository
) {
    suspend operator fun invoke(space: Space) = repository.deleteSpace(space)
}
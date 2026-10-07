package ru.kulishov.sokratum.domain.usecase.space

import ru.kulishov.sokratum.domain.model.SpaceAndChats
import ru.kulishov.sokratum.domain.repository.SpaceRepository

class GetSpaceAndChatsUseCase(
    private val repository: SpaceRepository
) {
    suspend operator fun invoke(id: Long): SpaceAndChats? = repository.getSpaceAndChats(id)
}
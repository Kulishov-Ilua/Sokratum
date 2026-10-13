package ru.kulishov.sokratum.domain.usecase.space

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.model.SpaceAndChats
import ru.kulishov.sokratum.domain.repository.SpaceRepository

class GetSpaceAndChatsUseCase(
    private val repository: SpaceRepository
) {
     operator fun invoke(id: Long): Flow<RefreshResult<SpaceAndChats>> = repository.getSpaceAndChats(id)
}
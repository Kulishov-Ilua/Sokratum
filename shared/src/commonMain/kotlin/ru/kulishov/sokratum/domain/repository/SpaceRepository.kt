package ru.kulishov.sokratum.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.model.SpaceWithoutStorage
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.model.SpaceAndChats

interface SpaceRepository {
    fun getSpacesData(): Flow<RefreshResult<Space>>
    suspend fun getSpaceAndChats(spaceId: Long): SpaceAndChats?
    suspend fun addSpace(space: Space): Space?
    suspend fun updateSpace(space: Space): Space?
    suspend fun deleteSpace(space: Space)
}
package ru.kulishov.sokratum.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.domain.model.SpaceWithoutStorage
import ru.kulishov.sokratum.domain.model.Space

interface SpaceRepository {
    fun getSpacesData(): Flow<List<SpaceWithoutStorage>>
    suspend fun getSpaceData(spaceId: Long): Space?
    suspend fun addSpace(space: Space): Space?
    suspend fun updateSpace(space: Space): Space?
    suspend fun deleteSpace(space: Space)
}
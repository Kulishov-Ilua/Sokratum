package ru.kulishov.sokratum.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.kulishov.sokratum.data.local.dao.SpaceDao
import ru.kulishov.sokratum.data.local.data.mapper.SpaceMapper
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.model.SpaceAndChats
import ru.kulishov.sokratum.domain.model.SpaceWithoutStorage
import ru.kulishov.sokratum.domain.repository.SpaceRepository

class SpaceRepositoryImpl(
    private val spaceDao: SpaceDao
) : SpaceRepository {
    override suspend fun getSpacesData(): List<Space> =
        spaceDao.getSpaces().map { SpaceMapper.toDomain(it) }

    override suspend fun getSpaceAndChats(spaceId: Long): SpaceAndChats? {
        //todo add db realization
        return null
//        val entity = spaceDao.getSpace(spaceId)
//        if (entity == null) return null
//        else return SpaceMapper.toDomain(entity)
    }

    override suspend fun addSpace(space: Space): Space? {
        val id = spaceDao.addSpace(SpaceMapper.toEntity(space))
        val findSpace = spaceDao.getSpace(id)
        if (findSpace == null) return null
        else return SpaceMapper.toDomain(findSpace)
    }

    override suspend fun updateSpace(space: Space): Space? {
        spaceDao.updateSpace(SpaceMapper.toEntity(space))
        val findSpace = spaceDao.getSpace(space.id)
        if (findSpace == null) return null
        else return SpaceMapper.toDomain(findSpace)
    }

    override suspend fun deleteSpace(space: Space) =
        spaceDao.deleteSpace(SpaceMapper.toEntity(space))
}
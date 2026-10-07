package ru.kulishov.sokratum.data.local.data.mapper

import ru.kulishov.sokratum.data.local.data.entity.SpaceEntity
import ru.kulishov.sokratum.domain.model.SpaceWithoutStorage
import ru.kulishov.sokratum.domain.model.Space

object SpaceMapper {
    fun toDomain(entity: SpaceEntity): Space = Space(
        id = entity.id,
        name = entity.name,
        lastOpen = entity.lastOpen
    )

    fun toEntity(domain: Space): SpaceEntity = SpaceEntity(
        id = domain.id,
        name = domain.name,
        lastOpen = domain.lastOpen
    )

    fun toDomainWithoutStorage(entity: SpaceWithoutStorage): SpaceWithoutStorage =
        SpaceWithoutStorage(
        id = entity.id,
        name = entity.name,
        path = entity.path,
        lastOpen = entity.lastOpen
    )
}
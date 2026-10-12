package ru.kulishov.sokratum.data.local.data.mapper

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import ru.kulishov.sokratum.data.local.data.entity.SpaceEntity
import ru.kulishov.sokratum.domain.model.SpaceWithoutStorage
import ru.kulishov.sokratum.domain.model.Space
import kotlin.time.Instant

object SpaceMapper {
    fun toDomain(entity: SpaceEntity): Space = Space(
        id = entity.id,
        name = entity.name,
        lastOpen = Instant.fromEpochSeconds(entity.lastOpen).toLocalDateTime(TimeZone.UTC)
    )

    fun toEntity(domain: Space): SpaceEntity = SpaceEntity(
        id = domain.id,
        name = domain.name,
        lastOpen = domain.lastOpen.toInstant(TimeZone.UTC).epochSeconds
    )

    fun toDomainWithoutStorage(entity: SpaceWithoutStorage): SpaceWithoutStorage =
        SpaceWithoutStorage(
        id = entity.id,
        name = entity.name,
        path = entity.path,
        lastOpen = entity.lastOpen
    )
}
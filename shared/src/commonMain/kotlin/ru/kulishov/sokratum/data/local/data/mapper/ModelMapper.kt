package ru.kulishov.sokratum.data.local.data.mapper

import ru.kulishov.sokratum.data.local.data.entity.ModelEntity
import ru.kulishov.sokratum.domain.model.Model

object ModelMapper {
    fun toDomain(entity: ModelEntity): Model = Model(
        id = entity.id,
        name = entity.name,
        url = entity.url,
        key = entity.key,
        timeout = entity.timeout
    )

    fun toEntity(domain: Model): ModelEntity = ModelEntity(
        id = domain.id,
        name = domain.name,
        url = domain.url,
        key = domain.key,
        timeout = domain.timeout
    )
}
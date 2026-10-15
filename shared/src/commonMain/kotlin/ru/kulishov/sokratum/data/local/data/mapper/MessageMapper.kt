package ru.kulishov.sokratum.data.local.data.mapper

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import ru.kulishov.sokratum.data.local.data.entity.MessageEntity
import ru.kulishov.sokratum.domain.model.Message
import kotlin.time.Instant

object MessageMapper {
    fun toDomain(entity: MessageEntity): Message = Message(
        id = entity.id?:-1,
        idChat = entity.idChat.toInt(),
        isModel = entity.isModel,
        text = entity.text,
        sendTime = Instant.fromEpochSeconds(entity.sendTime).toLocalDateTime(TimeZone.UTC)
    )

    fun toEntity(domain: Message): MessageEntity = MessageEntity(
        id = domain.id,
        idChat = domain.idChat.toLong(),
        isModel = domain.isModel,
        text = domain.text,
        sendTime = domain.sendTime.toInstant(TimeZone.UTC).epochSeconds
    )
}
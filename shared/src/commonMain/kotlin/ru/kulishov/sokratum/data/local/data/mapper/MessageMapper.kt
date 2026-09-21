package ru.kulishov.sokratum.data.local.data.mapper

import ru.kulishov.sokratum.data.local.data.entity.MessageEntity
import ru.kulishov.sokratum.domain.model.Message

object MessageMapper {
    fun toDomain(entity: MessageEntity): Message = Message(
        id = entity.id,
        idChat = entity.idChat,
        isModel = entity.isModel,
        text = entity.text,
        sendTime = entity.sendTime
    )

    fun toEntity(domain: Message): MessageEntity = MessageEntity(
        id = domain.id,
        idChat = domain.idChat,
        isModel = domain.isModel,
        text = domain.text,
        sendTime = domain.sendTime
    )
}
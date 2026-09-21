package ru.kulishov.sokratum.data.local.data.mapper

import ru.kulishov.sokratum.data.local.data.entity.ChatEntity
import ru.kulishov.sokratum.data.local.data.entity.MessageEntity
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.ChatAndMessages
import ru.kulishov.sokratum.domain.model.Message

object ChatMapper {
    fun toDomain(entity: ChatEntity): Chat = Chat(
        id = entity.id,
        idSpace = entity.idSpace,
        name = entity.name,
        systemPrompt = entity.systemPrompt,
        lastOpen = entity.lastOpen
    )

    fun toEntity(domain: Chat): ChatEntity = ChatEntity(
        id = domain.id,
        idSpace = domain.idSpace,
        name = domain.name,
        systemPrompt = domain.systemPrompt,
        lastOpen = domain.lastOpen
    )

    fun toChatAndMessages(entity: ChatEntity, messages: List<MessageEntity>): ChatAndMessages =
        ChatAndMessages(
            Chat(
                id = entity.id,
                idSpace = entity.idSpace,
                name = entity.name,
                systemPrompt = entity.systemPrompt,
                lastOpen = entity.lastOpen
            ),
            messages.map {
                Message(
                    id = it.id,
                    idChat = it.idChat,
                    isModel = it.isModel,
                    text = it.text,
                    sendTime = it.sendTime
                )
            }
        )
}
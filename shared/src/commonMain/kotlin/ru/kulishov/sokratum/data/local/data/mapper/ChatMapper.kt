package ru.kulishov.sokratum.data.local.data.mapper

import io.ktor.client.request.invoke
import io.ktor.util.reflect.instanceOf
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.parseOrNull
import kotlinx.datetime.toLocalDateTime
import ru.kulishov.sokratum.data.local.data.entity.ChatEntity
import ru.kulishov.sokratum.data.local.data.entity.MessageEntity
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.ChatAndMessages
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.MessagesAndUpdateTime
import kotlin.time.Clock

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
            id = entity.id.toInt(),
            spaceId = entity.idSpace.toInt(),
            name = entity.name,
            systemPrompt = entity.systemPrompt,
            lastOpen = LocalDateTime(0,0,0,0,0)
        ,
            MessagesAndUpdateTime(
                messages = messages.map {
                    Message(
                        id = it.id,
                        idChat = it.idChat,
                        isModel = it.isModel,
                        text = it.text,
                        sendTime = it.sendTime
                    )
                },
                updateTime = Clock.System.now().toLocalDateTime(timeZone = TimeZone.UTC)
            )

        )
}
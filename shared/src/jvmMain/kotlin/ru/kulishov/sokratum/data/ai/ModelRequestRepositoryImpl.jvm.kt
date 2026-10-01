package ru.kulishov.sokratum.data.ai

import dev.langchain4j.agentic.AgenticServices
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.ModelRequest
import ru.kulishov.sokratum.domain.repository.ModelRequestRepository
import kotlin.time.Clock

actual class ModelRequestRepositoryImpl actual constructor() :
    ModelRequestRepository {
    actual override suspend fun sendMessage(message: ModelRequest): Message? {
        val model = AIModels.actualModel
        if(model==null) return null
        val agent = AgenticServices
            .agentBuilder(ExpertAgent::class.java)
            .chatModel(JVMAIModel.getChatModel(model))
            .contentRetriever(JVMAIModel.getStorage(message.path))
            .systemMessage(message.systemPrompt)
            .outputKey("response")
            .build()
        val answer = agent.chat(message.text)
        return Message(
            id = null,
            idChat = message.chatId,
            isModel = true,
            text = answer,
            sendTime = Clock.System.now().epochSeconds
        )
    }
}
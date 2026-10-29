package ru.kulishov.sokratum.ai

import dev.langchain4j.agentic.Agent
import dev.langchain4j.invocation.InvocationParameters
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.UserMessage
import dev.langchain4j.service.V

interface ExpertAgent {
    @Agent
    fun chat(
        @MemoryId chatId: Int,
        @UserMessage message: String,
    ): String
}
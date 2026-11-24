package ru.kulishov.sokratum.ai

import dev.langchain4j.agentic.Agent
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.UserMessage

interface DebateAgent {
    @Agent
    fun speak(
        @MemoryId chatId: Int,
        @UserMessage message: String,
    ): String
}

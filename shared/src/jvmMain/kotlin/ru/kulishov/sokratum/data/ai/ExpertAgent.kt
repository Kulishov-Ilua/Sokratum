package ru.kulishov.sokratum.data.ai

import dev.langchain4j.agentic.Agent
import dev.langchain4j.service.UserMessage

interface ExpertAgent {
    @Agent
    fun chat(
        @UserMessage message: String
    ): String
}
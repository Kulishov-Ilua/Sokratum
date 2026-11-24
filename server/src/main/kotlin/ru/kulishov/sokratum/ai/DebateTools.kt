package ru.kulishov.sokratum.ai

import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool
import org.jetbrains.exposed.v1.core.exposedLogger

/** Фиксирует, с кем согласился агент в текущей реплике. */
class AgreementHolder {
    var agreedWith: String? = null
}

class DebateTools(private val holder: AgreementHolder) {

    @Tool("Вызови этот инструмент, только если ты полностью согласен с точкой зрения другого агента и признаёшь её верной. После вызова кратко подтверди согласие в реплике.")
    fun agreeWith(
        @P("Имя агента, с точкой зрения которого ты согласен") agentName: String
    ): String {
        exposedLogger.info("Agent agreed with $agentName")
        holder.agreedWith = agentName
        return "Согласие с агентом $agentName зафиксировано"
    }
}

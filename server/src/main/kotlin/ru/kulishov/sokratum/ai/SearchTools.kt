package ru.kulishov.sokratum.ai

import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool
import dev.langchain4j.rag.query.Query
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.exposedLogger
import ru.kulishov.sokratum.ai.rag.RAG

class SearchTools(
    private val spaceId: Int
) {
    @Tool("\"Ищи информацию в базе знаний пользователя, включая его личные предпочтения, привычки, историю и т.д. Используй этот инструмент, когда нужно узнать личную информацию о пользователе и всегда, когда тебе не хватает информации.\"")
    fun searchKnowledge(
        @P("Поисковый запрос") query: String,
    ): String {
        exposedLogger.info("SearchKnowledge")
        return runBlocking() {
            val retriever = RAG.getRetriever(spaceId)
            val contents = retriever.retrieve(Query.from(query))
            exposedLogger.info(contents.toString())
            if (contents.isNullOrEmpty()) "Ничего не найдено."
            contents.joinToString("\n\n") { it.textSegment().text() }
        }

    }
}
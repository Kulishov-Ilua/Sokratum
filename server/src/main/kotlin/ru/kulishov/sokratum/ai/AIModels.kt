package ru.kulishov.sokratum.ai

import dev.langchain4j.model.openai.OpenAiChatModel
import io.ktor.server.application.ApplicationEnvironment
import java.time.Duration

object AIModels {
    var env: ApplicationEnvironment? = null
    fun init(environment: ApplicationEnvironment) {
        env = environment
        models+="gemma4" to OpenAiChatModel.builder()
            .baseUrl(env!!.config.property("langchain.url").getString())
            .modelName(env!!.config.property("langchain.model").getString())
            //.responseFormat(responseFormat)
            .temperature(
                env!!.config.property("langchain.temperature").getString().toDouble()
            ).timeout(Duration.ofSeconds(360))
            .logRequests(true)
            .logResponses(true)

            .build()

    }

    val models = mutableMapOf<String, OpenAiChatModel?>()
}
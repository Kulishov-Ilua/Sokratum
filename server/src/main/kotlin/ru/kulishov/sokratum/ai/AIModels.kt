package ru.kulishov.sokratum.ai

import dev.langchain4j.model.openai.OpenAiChatModel
import io.ktor.server.application.ApplicationEnvironment
import java.time.Duration

object AIModels {
    var env: ApplicationEnvironment? = null

    var aiModels = listOf<AIModel>()
    fun init(environment: ApplicationEnvironment) {
        env = environment
        aiModels+= AIModel(
            id = 0,
            name = env!!.config.property("langchain.model").getString(),
            url =env!!.config.property("langchain.url").getString(),
            key = "",
            timeout = 360
        )
        aiModels+=AIModel(
            id = 1,
            name =env!!.config.property("langchain.smallModel").getString(),
            url =env!!.config.property("langchain.url").getString(),
            key = "",
            timeout = 360
        )

        for(model in aiModels){
            models+= model.name to OpenAiChatModel.builder()
                .baseUrl(model.url)
                .modelName(model.name)
                .temperature(
                env!!.config.property("langchain.temperature").getString().toDouble()
            ).timeout(Duration.ofSeconds(model.timeout))
            .logRequests(true)
            .logResponses(true)
                .customParameters(mapOf(
                    "chat_template_kwargs" to mapOf("enable_thinking" to true)
                ))
                .build()
        }
//        models+="gemma4" to OpenAiChatModel.builder()
//            .baseUrl(env!!.config.property("langchain.url").getString())
//            .modelName(env!!.config.property("langchain.model").getString())
//            //.responseFormat(responseFormat)
//            .temperature(
//                env!!.config.property("langchain.temperature").getString().toDouble()
//            ).timeout(Duration.ofSeconds(360))
//            .logRequests(true)
//            .logResponses(true)
//
//            .build()
//
//        models+="gemma3" to OpenAiChatModel.builder()
//            .baseUrl(env!!.config.property("langchain.url").getString())
//            .modelName(env!!.config.property("langchain.smallModel").getString())
//            //.responseFormat(responseFormat)
//            .temperature(
//                env!!.config.property("langchain.temperature").getString().toDouble()
//            ).timeout(Duration.ofSeconds(360))
//            .logRequests(true)
//            .logResponses(true)
//
//            .build()
    }

    val models = mutableMapOf<String, OpenAiChatModel?>()

}
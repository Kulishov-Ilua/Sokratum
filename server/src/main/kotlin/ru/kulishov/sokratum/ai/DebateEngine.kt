package ru.kulishov.sokratum.ai

import dev.langchain4j.agentic.AgenticServices
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.v1.core.exposedLogger
import ru.kulishov.sokratum.db.agent.Agent
import ru.kulishov.sokratum.db.chat.Chat
import ru.kulishov.sokratum.db.message.Message
import ru.kulishov.sokratum.db.message.MessageDao
import java.time.LocalDateTime

/**
 * Ведёт спор ИИ-персонажей: агенты по очереди высказываются, видя реплики
 * предыдущих, пока все не сойдутся на одной точке зрения или не выйдет
 * лимит кругов (chat.debateMaxRounds).
 */
class DebateEngine(
    private val messageDao: MessageDao
) {

    suspend fun runDebate(
        chat: Chat,
        agents: List<Agent>,
        userText: String,
        model: String,
        temperature: Double,
        onReplica: suspend (Message) -> Unit
    ) {
        // Модель создаём один раз на весь спор, а не на каждую реплику.
        // Режим размышлений отключаем: реплик много, thinking кратно замедляет спор.
        val chatModel = AIModels.getAiModel(model, temperature, enableThinking = false)
        if (chatModel == null) {
            exposedLogger.error("Спор отменён: модель «$model» не найдена")
            return
        }

        val transcript = StringBuilder("Вопрос пользователя: $userText")
        // Кто с кем согласен: имя агента -> имя «победителя»
        val agreements = mutableMapOf<String, String>()
        var winner: String? = null
        var round = 0

        while (round < chat.debateMaxRounds && winner == null) {
            round++
            for (agent in agents) {
                val holder = AgreementHolder()
                // Одна повторная попытка, если агент не ответил
                var reply = askAgent(chatModel, agent, transcript.toString(), holder)
                if (reply.isNullOrBlank()) {
                    exposedLogger.warn("Агент ${agent.name} не ответил, повторная попытка")
                    reply = askAgent(chatModel, agent, transcript.toString(), holder)
                }
                if (holder.agreedWith != null) {
                    agreements[agent.name] = holder.agreedWith!!
                }

                // Агента нельзя «проигнорировать»: даже при сбое его реплика появится в чате
                val text = if (reply.isNullOrBlank()) {
                    exposedLogger.error("Агент ${agent.name} не ответил после повторной попытки")
                    "*(не удалось получить ответ)*"
                } else reply

                val replica = messageDao.addMessage(
                    Message(
                        id = -1,
                        idChat = chat.id,
                        isModel = true,
                        text = text,
                        sendTime = LocalDateTime.now().toKotlinLocalDateTime(),
                        author = agent.name
                    )
                )
                if (replica != null) onReplica(replica)
                transcript.append("\n${agent.name}: $text")

                winner = detectWinner(agents.map { it.name }, agreements)
                if (winner != null) break
            }
        }

        val finalText = if (winner != null) {
            "Спор завершён: все агенты пришли к точке зрения «$winner»."
        } else {
            "Спор завершён: за ${chat.debateMaxRounds} круг(а) агенты не пришли к единому мнению."
        }
        val finalMessage = messageDao.addMessage(
            Message(
                id = -1,
                idChat = chat.id,
                isModel = true,
                text = finalText,
                sendTime = LocalDateTime.now().toKotlinLocalDateTime()
            )
        )
        if (finalMessage != null) onReplica(finalMessage)
    }

    private suspend fun askAgent(
        chatModel: dev.langchain4j.model.openai.OpenAiChatModel,
        agent: Agent,
        transcript: String,
        holder: AgreementHolder
    ): String? = withContext(Dispatchers.IO) {
        try {
            val debateAgent = AgenticServices
                .agentBuilder(DebateAgent::class.java)
                .chatModel(chatModel)
                .outputKey("response")
                .systemMessage(
                    """
                    Ты — участник дискуссии по имени «${agent.name}».
                    ${agent.systemPrompt}
                    У тебя есть собственная точка зрения на обсуждаемый вопрос. Высказывай её кратко и по существу,
                    учитывая реплики других участников. Если ты полностью согласен с точкой зрения другого
                    участника и готов признать её верной — вызови инструмент agreeWith с его именем.
                    Отвечай только своей репликой, без имени и без префиксов.
                    """.trimIndent()
                )
                .tools(DebateTools(holder))
                // Окно должно вмещать промпт + вызов инструмента + ответ инструмента,
                // иначе модель теряет контекст и отвечает пусто
                .chatMemoryProvider { MessageWindowChatMemory.withMaxMessages(10) }
                .build()
            debateAgent.speak(agent.id, transcript)
        } catch (e: Exception) {
            exposedLogger.error("Ошибка агента ${agent.name}: ${e.message}")
            null
        }
    }
}

/**
 * Спор окончен, когда все агенты сошлись на одной точке зрения:
 * каждый либо сам является «победителем», либо согласился с ним.
 */
fun detectWinner(agentNames: List<String>, agreements: Map<String, String>): String? {
    val targets = agreements.values.distinct()
    if (targets.size != 1) return null
    val target = targets.first()
    if (target !in agentNames) return null
    val others = agentNames.filter { it != target }
    return if (others.isNotEmpty() && others.all { agreements[it] == target }) target else null
}

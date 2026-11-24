package ru.kulishov.sokratum.domain.usecase.agent

import ru.kulishov.sokratum.domain.model.Agent
import ru.kulishov.sokratum.domain.repository.AgentRepository

class GetAgentsUseCase(private val repository: AgentRepository) {
    suspend operator fun invoke(chatId: Long): List<Agent> = repository.getAgents(chatId)
}

class AddAgentUseCase(private val repository: AgentRepository) {
    suspend operator fun invoke(agent: Agent): Agent? = repository.addAgent(agent)
}

class DeleteAgentUseCase(private val repository: AgentRepository) {
    suspend operator fun invoke(agentId: Int): Boolean = repository.deleteAgent(agentId)
}

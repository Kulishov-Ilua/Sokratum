package ru.kulishov.sokratum.domain.repository

import ru.kulishov.sokratum.domain.model.Agent

interface AgentRepository {
    suspend fun getAgents(chatId: Long): List<Agent>
    suspend fun addAgent(agent: Agent): Agent?
    suspend fun deleteAgent(agentId: Int): Boolean
}

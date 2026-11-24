package ru.kulishov.sokratum.data.remote.repository

import ru.kulishov.sokratum.data.remote.ApiClient
import ru.kulishov.sokratum.domain.model.Agent
import ru.kulishov.sokratum.domain.repository.AgentRepository

class AgentRepositoryImpl : AgentRepository {

    override suspend fun getAgents(chatId: Long): List<Agent> =
        ApiClient.get("/getAgents/$chatId") ?: emptyList()

    override suspend fun addAgent(agent: Agent): Agent? =
        ApiClient.post("/addAgent", agent)

    override suspend fun deleteAgent(agentId: Int): Boolean =
        ApiClient.postRaw("/deleteAgent/$agentId") != null
}

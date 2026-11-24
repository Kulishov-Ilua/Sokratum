package ru.kulishov.sokratum.db.agent

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import ru.kulishov.sokratum.db.query

class AgentDao {

    private fun rowToAgent(row: ResultRow) = Agent(
        id = row[Agents.id],
        chatId = row[Agents.chatId],
        name = row[Agents.name],
        systemPrompt = row[Agents.systemPrompt]
    )

    suspend fun getAgents(chatId: Int): List<Agent> = query {
        Agents.selectAll().where { Agents.chatId eq chatId }.map { rowToAgent(it) }
    }

    suspend fun addAgent(agent: Agent): Agent? = query {
        Agents.insert {
            it[chatId] = agent.chatId
            it[name] = agent.name
            it[systemPrompt] = agent.systemPrompt
        }.resultedValues?.map { rowToAgent(it) }?.firstOrNull()
    }

    suspend fun deleteAgent(agentId: Int): Boolean = query {
        Agents.deleteWhere { Agents.id eq agentId }
        true
    }
}

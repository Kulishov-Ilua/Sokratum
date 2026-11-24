package ru.kulishov.sokratum.ui.redactChatScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.InjectedParam
import ru.kulishov.sokratum.domain.model.Agent
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.usecase.agent.AddAgentUseCase
import ru.kulishov.sokratum.domain.usecase.agent.DeleteAgentUseCase
import ru.kulishov.sokratum.domain.usecase.agent.GetAgentsUseCase
import ru.kulishov.sokratum.domain.usecase.chat.AddChatUseCase
import ru.kulishov.sokratum.domain.usecase.chat.DeleteChatUseCase
import ru.kulishov.sokratum.domain.usecase.chat.GetChatByIdUseCase
import ru.kulishov.sokratum.domain.usecase.chat.UpdateChatUseCase
import ru.kulishov.sokratum.ui.BaseViewModel
import kotlin.time.Clock

class RedactChatScreenViewModel(
    private val addChatUseCase: AddChatUseCase,
    private val updateChatUseCase: UpdateChatUseCase,
    private val getChatByIdUseCase: GetChatByIdUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val getAgentsUseCase: GetAgentsUseCase,
    private val addAgentUseCase: AddAgentUseCase,
    private val deleteAgentUseCase: DeleteAgentUseCase,
    @InjectedParam val chatId: Long,
    @InjectedParam val spaceId: Long
) : BaseViewModel() {

    private val _uiState =
        MutableStateFlow<RedactChatScreenUiState>(RedactChatScreenUiState.Loading)
    val uiState: StateFlow<RedactChatScreenUiState> = _uiState.asStateFlow()

    var name by mutableStateOf("")
    var systemPrompt by mutableStateOf("")

    var debateEnabled by mutableStateOf(false)
    var debateMaxRounds by mutableStateOf("3")
    var agents by mutableStateOf<List<Agent>>(emptyList())
        private set

    init {
        launch {
            if (chatId < 0) {
                _uiState.value = RedactChatScreenUiState.Success
            } else {
                val chat = getChatByIdUseCase(chatId)
                if (chat != null) {
                    name = chat.name
                    systemPrompt = chat.systemPrompt
                    debateEnabled = chat.debateEnabled
                    debateMaxRounds = chat.debateMaxRounds.toString()
                    agents = getAgentsUseCase(chatId)
                    _uiState.value = RedactChatScreenUiState.Success
                } else {
                    _uiState.value = RedactChatScreenUiState.Error("")
                }
            }
        }
    }

    fun addAgent(agentName: String, agentPrompt: String) {
        if (agentName.isBlank() || chatId < 0) return
        launch {
            val agent = addAgentUseCase(
                Agent(
                    id = -1,
                    chatId = chatId.toInt(),
                    name = agentName.trim(),
                    systemPrompt = agentPrompt.trim()
                )
            )
            if (agent != null) {
                agents = agents + agent
            }
        }
    }

    fun deleteAgent(agent: Agent) {
        launch {
            if (deleteAgentUseCase(agent.id)) {
                agents = agents.filterNot { it.id == agent.id }
            }
        }
    }

    fun delete() {
        launch {
            deleteChatUseCase(buildChat())
            _uiState.value = RedactChatScreenUiState.Navigation(RedactChatScreenNavigationRoutes.Back)
        }
    }

    fun redact() {
        launch {
            val chat = if (chatId == -1L) {
                addChatUseCase(buildChat().copy(id = -1))
            } else {
                updateChatUseCase(buildChat())
            }
            if (chat != null) {
                _uiState.value =
                    RedactChatScreenUiState.Navigation(RedactChatScreenNavigationRoutes.Chat(chat.id, spaceId, name))
            } else {
                _uiState.value = RedactChatScreenUiState.Error("")
            }
        }
    }

    private fun buildChat() = Chat(
        id = chatId,
        name = name,
        systemPrompt = systemPrompt,
        spaceId = spaceId,
        lastOpen = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        debateEnabled = debateEnabled,
        debateMaxRounds = debateMaxRounds.toIntOrNull()?.coerceIn(1, 20) ?: 3
    )

    sealed class RedactChatScreenNavigationRoutes {
        object Back : RedactChatScreenNavigationRoutes()
        data class Chat(val chatId: Long, val spaceId: Long, val chatName: String) : RedactChatScreenNavigationRoutes()
    }

    sealed class RedactChatScreenUiState {
        object Loading : RedactChatScreenUiState()
        object Success : RedactChatScreenUiState()
        data class Navigation(val route: RedactChatScreenNavigationRoutes) :
            RedactChatScreenUiState()

        data class Error(val e: String) : RedactChatScreenUiState()
    }
}

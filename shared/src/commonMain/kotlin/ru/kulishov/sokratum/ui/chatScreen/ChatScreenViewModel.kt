package ru.kulishov.sokratum.ui.chatScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import org.koin.core.annotation.InjectedParam
import ru.kulishov.sokratum.data.ai.AIModels
import ru.kulishov.sokratum.data.remote.model.chats.SendMessageRequest
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.usecase.chat.GetChatUseCase
import ru.kulishov.sokratum.domain.usecase.chat.MessagesUseCase
import ru.kulishov.sokratum.domain.usecase.chat.SendMessageUseCase
import ru.kulishov.sokratum.domain.usecase.chat.UpdateMessagesUseCase
import ru.kulishov.sokratum.ui.BaseViewModel
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock

class ChatScreenViewModel(
    private val messageUseCase: MessagesUseCase,
    private val getChatUseCase: GetChatUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val updateMessagesUseCase: UpdateMessagesUseCase,
    @InjectedParam val chatId: Long,
    @InjectedParam val chatName: String,
    @InjectedParam val spaceId: Long,
    @InjectedParam val message: String
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<ChatScreenUiState>(ChatScreenUiState.Loading)
    val uiState: StateFlow<ChatScreenUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    var digitTemperature = AIModels.temperature
    var temperature by mutableStateOf(digitTemperature.toString())

    private var pollIntervalMs = 5000L
    private var updateTime = 0L

    init {
        launch {
            getChatUseCase(chatId, spaceId, message.ifBlank { null })
                .catch { e -> _uiState.value = ChatScreenUiState.Error(e.message ?: "unknown") }
                .collect { result ->
                    _uiState.update {
                        when (result) {
                            is RefreshResult.Cached<Message> -> {
                                _messages.value = result.items
                                ChatScreenUiState.Success()
                            }

                            is RefreshResult.Fresh<Message> -> {
                                _messages.value = result.items
                                ChatScreenUiState.Success()
                            }

                            is RefreshResult.Offline<Message> -> {
                                _messages.value = result.items
                                ChatScreenUiState.Success("offline")
                            }

                            is RefreshResult.Error -> ChatScreenUiState.Error("unknown error")
                        }
                    }
                }
            updateTime = Clock.System.now().epochSeconds
            while (isActive) {
                delay(pollIntervalMs)
                if (_uiState.value !is ChatScreenUiState.Success) continue
                val newMessage = try {
                    updateMessagesUseCase(chatId, updateTime)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _uiState.value = ChatScreenUiState.Error(e.message ?: "500")
                    break
                }
                if (newMessage == null) {
                    _uiState.value = ChatScreenUiState.Error("500")
                    break
                }
                updateTime = newMessage.updateTime
                if (newMessage.messages.isNotEmpty()) {
                    pollIntervalMs = 3000L
                    val existing = _messages.value.mapTo(HashSet()) { it.id }
                    _messages.update { it + newMessage.messages.filterNot { m -> m.id in existing } }
                } else {
                    pollIntervalMs = nextPollInterval(pollIntervalMs)
                }
            }
        }
    }

    fun setTemperature() {
        AIModels.temperature = digitTemperature
    }

    fun request(text: String) {
        launch {
            val modelName = AIModels.model?.name
            if (modelName == null) {
                _uiState.value = ChatScreenUiState.Success(e = "offline")
                return@launch
            }
            val answer = sendMessageUseCase(
                SendMessageRequest(
                    chatId.toInt(), modelName,
                    AIModels.temperature, text
                )
            )
            if (answer != null) {
                _messages.value += answer
                pollIntervalMs = 3000L
                _uiState.value = ChatScreenUiState.Success()
            } else {
                _uiState.value = ChatScreenUiState.Success(
                    e = "offline"
                )
            }
        }
    }

    sealed class ChatScreenNavigationRoutes {
        object Back : ChatScreenNavigationRoutes()
        data class ChatSettings(val chatId: Int) : ChatScreenNavigationRoutes()
    }

    sealed class ChatScreenUiState {
        object Loading : ChatScreenUiState()
        data class Success(val e: String? = null) : ChatScreenUiState()
        data class Error(val e: String) : ChatScreenUiState()
    }
}

/**
 * Экспоненциальное (ступенчатое) увеличение интервала опроса новых сообщений:
 * 3000 -> 5000 -> 7000 и дальше держится на 7000 мс.
 */
internal fun nextPollInterval(current: Long): Long = when (current) {
    3000L -> 5000L
    5000L -> 7000L
    else -> 7000L
}

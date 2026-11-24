package ru.kulishov.sokratum.ui.chatScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.InjectedParam
import ru.kulishov.sokratum.data.ai.AIModels
import ru.kulishov.sokratum.data.remote.model.chats.SendMessageRequest
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.usecase.chat.MessagesUseCase
import ru.kulishov.sokratum.ui.BaseViewModel

class ChatScreenViewModel(
    private val messageUseCase: MessagesUseCase,
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

    private val outgoing = Channel<SendMessageRequest>(Channel.BUFFERED)

    init {
        launch {
            messageUseCase(chatId.toInt(), spaceId, null, outgoing.receiveAsFlow())
                .catch { e -> _uiState.value = ChatScreenUiState.Error(e.message ?: "unknown") }
                .collect { result ->
                    when (result) {
                        is RefreshResult.Cached<Message> -> {
                            mergeMessages(result.items)
                            _uiState.value = ChatScreenUiState.Success()
                        }

                        is RefreshResult.Fresh<Message> -> {
                            mergeMessages(result.items)
                            _uiState.value = ChatScreenUiState.Success()
                        }

                        is RefreshResult.Offline<Message> -> {
                            mergeMessages(result.items)
                            _uiState.value = ChatScreenUiState.Success("offline")
                        }

                        is RefreshResult.Error -> {
                            if (_messages.value.isEmpty()) {
                                _uiState.value = ChatScreenUiState.Error("unknown error")
                            } else {
                                _uiState.value = ChatScreenUiState.Success("offline")
                            }
                        }
                    }
                }
        }
        // Первое сообщение при создании чата извне (например, с главного экрана)
        if (message.isNotBlank()) {
            request(message)
        }
    }

    private fun mergeMessages(items: List<Message>) {
        val existing = _messages.value.mapTo(HashSet()) { it.id }
        _messages.update { it + items.filterNot { m -> m.id in existing } }
    }

    fun setTemperature() {
        AIModels.temperature = digitTemperature
    }

    fun request(text: String) {
        val modelName = AIModels.model?.name
        if (modelName == null) {
            _uiState.value = ChatScreenUiState.Success(e = "offline")
            return
        }
        outgoing.trySend(
            SendMessageRequest(
                chatId = chatId.toInt(),
                model = modelName,
                temperature = AIModels.temperature,
                text = text
            )
        )
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

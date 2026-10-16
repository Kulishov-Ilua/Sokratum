package ru.kulishov.sokratum.ui.chatScreen

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.update
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.InjectedParam
import ru.kulishov.sokratum.data.ai.AIModels
import ru.kulishov.sokratum.data.remote.model.chats.SendMessageRequest
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.usecase.chat.GetChatUseCase
import ru.kulishov.sokratum.domain.usecase.chat.MessagesUseCase
import ru.kulishov.sokratum.domain.usecase.chat.SendMessageUseCase
import ru.kulishov.sokratum.ui.BaseViewModel
import kotlin.time.Clock

class ChatScreenViewModel(
    private val messageUseCase: MessagesUseCase,
    private val getChatUseCase: GetChatUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    @InjectedParam val chatId: Long,
    @InjectedParam val chatName: String,
    @InjectedParam val spaceId: Long,
    @InjectedParam val message: String
) : BaseViewModel() {

//    private val outgoing = Channel<Message>(Channel.BUFFERED)
//
//    val uiState: Flow<ChatScreenUiState> =
//        messageUseCase(
//            chatId = chatId.toInt(),
//            spaceId = spaceId,
//            afterMessageId = null,
//            outgoing = outgoing.receiveAsFlow()
//        ).runningFold(ChatScreenUiState.Loading as ChatScreenUiState) { state, result ->
//            reduce(state, result)
//        }

    private val _uiState = MutableStateFlow<ChatScreenUiState>(ChatScreenUiState.Loading)
    val uiState: StateFlow<ChatScreenUiState> = _uiState.asStateFlow()

    init {
        launch {
            getChatUseCase(chatId, spaceId, message.ifBlank { null })
                .catch { e -> _uiState.value = ChatScreenUiState.Error(e.message ?: "unknown") }
                .collect { result ->
                    _uiState.update {
                        when (result) {
                            is RefreshResult.Cached<Message>  -> ChatScreenUiState.Success(result.items)
                            is RefreshResult.Fresh<Message>   -> ChatScreenUiState.Success(result.items)
                            is RefreshResult.Offline<Message> -> ChatScreenUiState.Success(result.items, "offline")
                            is RefreshResult.Error            -> ChatScreenUiState.Error("unknown error")
                        }
                    }
                }
        }
    }
    fun request(text: String){
        launch {
            if(_uiState.value is ChatScreenUiState.Success){
                _uiState.value= ChatScreenUiState.Success((_uiState.value as ChatScreenUiState.Success).items+ Message(
                    id = -1,
                    idChat = chatId.toInt(),
                    isModel = false,
                    text = text,
                    sendTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                ), "send")

            }
            val answer = sendMessageUseCase(SendMessageRequest(chatId.toInt(), AIModels.model.name, text))
            if(answer!=null){
                _uiState.value= ChatScreenUiState.Success(items =if(_uiState.value is ChatScreenUiState.Success) (_uiState.value as ChatScreenUiState.Success).items + answer else listOf(answer))
            }else{
                _uiState.value= ChatScreenUiState.Success(items =if(_uiState.value is ChatScreenUiState.Success) (_uiState.value as ChatScreenUiState.Success).items else emptyList(), e = "offline")
            }
        }


    }
//    fun send(text: String) {
//        outgoing.trySend(
//            Message(
//                id = -1,
//                idChat = chatId.toInt(),
//                isModel = false,
//                text = text,
//                sendTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
//            )
//        )
//    }

    private fun reduce(
        state: ChatScreenUiState,
        result: RefreshResult<Message>
    ): ChatScreenUiState = when (result) {
        is RefreshResult.Cached<Message> -> ChatScreenUiState.Success(
            items = result.items
        )

        RefreshResult.Error -> ChatScreenUiState.Error("unknow error")
        is RefreshResult.Fresh<Message> -> {
            val currentItems = (state as? ChatScreenUiState.Success)?.items ?: emptyList()
            val isSingleWsMessage = result.items.size == 1 && currentItems.size > 1
            val merged = if (isSingleWsMessage) {
                val msg = result.items.first()
                if (currentItems.any { it.id == msg.id }) currentItems
                else currentItems + msg
            } else {
                result.items
            }
            ChatScreenUiState.Success(items = merged, e = "")
        }

        is RefreshResult.Offline<Message> -> ChatScreenUiState.Success(
            items = result.items,
            e = "offline"
        )
    }

    sealed class ChatScreenNavigationRoutes {
        object Back : ChatScreenNavigationRoutes()
        data class ChatSettings(val chatId: Int) : ChatScreenNavigationRoutes()
    }

    sealed class ChatScreenUiState {
        object Loading : ChatScreenUiState()
        data class Success(val items: List<Message>, val e: String? = null) : ChatScreenUiState()
        data class Error(val e: String) : ChatScreenUiState()
    }
}
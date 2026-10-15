package ru.kulishov.sokratum.ui.chatScreen

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.InjectedParam
import ru.kulishov.sokratum.domain.model.Message
import ru.kulishov.sokratum.domain.usecase.chat.MessagesUseCase
import ru.kulishov.sokratum.ui.BaseViewModel
import kotlin.time.Clock

class ChatScreenViewModel(
    private val messageUseCase: MessagesUseCase,
    @InjectedParam val chatId: Long
): BaseViewModel() {
    private val _uiState = MutableStateFlow<ChatScreenUiState>(value = ChatScreenUiState.Loading)
    val uiState: StateFlow<ChatScreenUiState> = _uiState.asStateFlow()
    private val outgoing = Channel<Message>(Channel.BUFFERED)


    fun send(text: String) {
        outgoing.trySend(Message(
            id = -1,
            idChat = chatId.toInt(),
            isModel = false,
            text = text,
            sendTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        ))
    }

    sealed class ChatScreenNavigationRoutes{
        object Back: ChatScreenNavigationRoutes()
        data class ChatSettings(val chatId:Int): ChatScreenNavigationRoutes()
    }
    sealed class ChatScreenUiState{
        object Loading: ChatScreenUiState()
        data class Success(val items:List<Message>): ChatScreenUiState()
        data class Error(val e: String)
    }
}
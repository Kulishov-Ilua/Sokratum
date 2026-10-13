package ru.kulishov.sokratum.ui.redactChatScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.InjectedParam
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.model.SpaceRedactTransfer
import ru.kulishov.sokratum.domain.usecase.chat.AddChatUseCase
import ru.kulishov.sokratum.domain.usecase.chat.DeleteChatUseCase
import ru.kulishov.sokratum.domain.usecase.chat.GetChatByIdUseCase
import ru.kulishov.sokratum.domain.usecase.chat.UpdateChatUseCase
import ru.kulishov.sokratum.domain.usecase.space.DeleteSpaceUseCase
import ru.kulishov.sokratum.ui.BaseViewModel
import ru.kulishov.sokratum.ui.redactSpaceScreen.RedactSpaceScreenViewModel.RedactSpaceNavigationRoutes
import ru.kulishov.sokratum.ui.redactSpaceScreen.RedactSpaceScreenViewModel.RedactSpaceUiState
import kotlin.time.Clock

class RedactChatScreenViewModel(
    private val addChatUseCase: AddChatUseCase,
    private val updateChatUseCase: UpdateChatUseCase,
    private val getChatByIdUseCase: GetChatByIdUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    @InjectedParam  val chatId: Long,
    @InjectedParam  val spaceId: Long
) : BaseViewModel() {

    private val _uiState =
        MutableStateFlow<RedactChatScreenUiState>(RedactChatScreenUiState.Loading)
    val uiState: StateFlow<RedactChatScreenUiState> = _uiState.asStateFlow()

    var name by mutableStateOf("")
    var systemPrompt by mutableStateOf("")

    init {
        launch {
            if (chatId < 0) {
                _uiState.value = RedactChatScreenUiState.Success
            } else {
                val chat = getChatByIdUseCase(chatId)
                if(chat!=null){
                    name=chat.name
                    systemPrompt=chat.systemPrompt
                    _uiState.value = RedactChatScreenUiState.Success
                }else{
                    _uiState.value = RedactChatScreenUiState.Error("")
                }


            }
        }

    }
    fun delete(){
        launch {
            deleteChatUseCase(Chat(chatId,spaceId,name,systemPrompt, Clock.System.now().toLocalDateTime(
                TimeZone.UTC)))
            _uiState.value= RedactChatScreenUiState.Navigation(RedactChatScreenNavigationRoutes.Back)
        }
    }

    fun redact() {
        launch {
            val chat = if (chatId == -1.toLong()) {
                addChatUseCase(
                    Chat(
                        id = -1,
                        name = name,
                        systemPrompt = systemPrompt,
                        spaceId = spaceId,
                        lastOpen = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                    )
                )
            } else {
                updateChatUseCase(
                    Chat(
                        id = chatId,
                        name = name,
                        systemPrompt = systemPrompt,
                        spaceId = spaceId,
                        lastOpen = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                    )
                )
            }
            if (chat!= null) {
                _uiState.value =
                    RedactChatScreenUiState.Navigation(RedactChatScreenNavigationRoutes.Chat(chat.id))
            } else {
                _uiState.value = RedactChatScreenUiState.Error("")
            }
        }

    }

    sealed class RedactChatScreenNavigationRoutes {
        object Back : RedactChatScreenNavigationRoutes()
        data class Chat(val chatId: Long) : RedactChatScreenNavigationRoutes()
    }

    sealed class RedactChatScreenUiState {
        object Loading : RedactChatScreenUiState()
        object Success : RedactChatScreenUiState()
        data class Navigation(val route: RedactChatScreenNavigationRoutes) :
            RedactChatScreenUiState()

        data class Error(val e: String) : RedactChatScreenUiState()
    }
}
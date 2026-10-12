package ru.kulishov.sokratum.ui.homeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import ru.kulishov.sokratum.data.remote.KtorClient
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.usecase.chat.GetLastChatsUseCase
import ru.kulishov.sokratum.domain.usecase.space.GetSpacesDataUseCase
import ru.kulishov.sokratum.ui.BaseViewModel

class HomeScreenViewModel(
    getLastChatsUseCase: GetLastChatsUseCase,
    getSpaceUseCase: GetSpacesDataUseCase
): BaseViewModel() {

    private val spacesData: Flow<RefreshResult<Space>> = getSpaceUseCase()
    private val chatsData: Flow<RefreshResult<Chat>> = getLastChatsUseCase()

    var searchText by mutableStateOf("")
    var messageText by mutableStateOf("")

    val uiState: Flow<HomeScreenUiState> = combine(
        spacesData,
        chatsData,
    ){ spaces, chats ->
        val state: HomeScreenUiState = when {
            spaces is RefreshResult.Error || chats is RefreshResult.Error -> HomeScreenUiState.Error("")
            spaces is RefreshResult.Offline || chats is RefreshResult.Offline -> HomeScreenUiState.Success(spaces.itemsOrEmpty(), chats.itemsOrEmpty(), "offline")
            else -> HomeScreenUiState.Success(spaces.itemsOrEmpty(), chats.itemsOrEmpty())
        }
        state
    }

    private fun <T> RefreshResult<T>.itemsOrEmpty(): List<T> = when (this) {
        is RefreshResult.Cached -> items
        is RefreshResult.Fresh -> items
        is RefreshResult.Offline -> items
        RefreshResult.Error -> emptyList()
    }


    sealed class HomeScreenNavigationRoutes{
        object Settings: HomeScreenNavigationRoutes()
        data class Chat(val chatID: Long): HomeScreenNavigationRoutes()
        object NewChat: HomeScreenNavigationRoutes()
        data class Space(val spaceId: Long): HomeScreenNavigationRoutes()
        data class SpaceRedactor(val spaceId: Long): HomeScreenNavigationRoutes()
        object Auth: HomeScreenNavigationRoutes()
    }

    sealed class HomeScreenUiState{
        object Loading: HomeScreenUiState()
        data class Success(val spaces: List<Space>, val chats: List<Chat>, val status: String = ""): HomeScreenUiState()
        class Error(e:String): HomeScreenUiState()
    }
}
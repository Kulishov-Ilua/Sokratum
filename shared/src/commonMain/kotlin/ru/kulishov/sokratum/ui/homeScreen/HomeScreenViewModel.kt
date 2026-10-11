package ru.kulishov.sokratum.ui.homeScreen

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    val spacesData: Flow<RefreshResult<Space>> = getSpaceUseCase()
    val chatsData: Flow<RefreshResult<Chat>> = getLastChatsUseCase()

    private val _uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()


    fun goToSettings(){
        _uiState.value= HomeScreenUiState.Navigation(HomeScreenNavigationRoutes.Settings)
    }

    fun goToChat(chatId:Int){
        _uiState.value= HomeScreenUiState.Navigation(HomeScreenNavigationRoutes.Chat(chatId))
    }

    fun goToNewChat(){
        _uiState.value= HomeScreenUiState.Navigation(HomeScreenNavigationRoutes.NewChat)
    }

    fun goToSpace(spaceId:Int){
        _uiState.value= HomeScreenUiState.Navigation(HomeScreenNavigationRoutes.Space(spaceId))
    }

    fun goToSpaceRedactor(spaceId: Int){
        _uiState.value= HomeScreenUiState.Navigation(HomeScreenNavigationRoutes.SpaceRedactor(spaceId))
    }


    sealed class HomeScreenNavigationRoutes{
        object Settings: HomeScreenNavigationRoutes()
        class Chat(chatID:Int): HomeScreenNavigationRoutes()
        object NewChat: HomeScreenNavigationRoutes()
        class Space(spaceId:Int): HomeScreenNavigationRoutes()
        class SpaceRedactor(spaceId: Int): HomeScreenNavigationRoutes()
        object Auth: HomeScreenNavigationRoutes()
    }

    sealed class HomeScreenUiState{
        object Loading: HomeScreenUiState()
        object Success: HomeScreenUiState()
        class Navigation(route: HomeScreenNavigationRoutes): HomeScreenUiState()
        class Error(e:String): HomeScreenUiState()
    }
}
package ru.kulishov.sokratum.ui.spaceScreen

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.InjectedParam
import ru.kulishov.sokratum.data.remote.model.auth.DeviceDataResponse
import ru.kulishov.sokratum.domain.model.Chat
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.model.SpaceAndChats
import ru.kulishov.sokratum.domain.usecase.space.GetSpaceAndChatsUseCase
import ru.kulishov.sokratum.ui.BaseViewModel

class SpaceScreenViewModel(
    private val getSpaceAndChatsUseCase: GetSpaceAndChatsUseCase,
    @InjectedParam private val spaceId: Long
): BaseViewModel() {
    val uiState: Flow<SpaceScreenUiState> = getSpaceAndChatsUseCase(spaceId).map { spaceAndChats->
        val state: SpaceScreenUiState = when(spaceAndChats){
            is RefreshResult.Fresh<SpaceAndChats> -> {
                if(spaceAndChats.items.isNotEmpty()){
                    SpaceScreenUiState.Success(spaceAndChats.items.first().chats, spaceAndChats.items.first().name, spaceAndChats.items.first().id.toLong() )
                }else{
                    SpaceScreenUiState.Error("unknow Error")
                }

            }
            is RefreshResult.Cached<SpaceAndChats> -> {
                if(spaceAndChats.items.isNotEmpty()){
                    SpaceScreenUiState.Success(spaceAndChats.items.first().chats,spaceAndChats.items.first().name, spaceAndChats.items.first().id.toLong() )
                }else{
                    SpaceScreenUiState.Error("unknow Error")
                }
            }
            RefreshResult.Error -> SpaceScreenUiState.Error("unknow Error")
            is RefreshResult.Offline<SpaceAndChats> -> {
                if(spaceAndChats.items.isNotEmpty()){
                    SpaceScreenUiState.Success(spaceAndChats.items.first().chats,spaceAndChats.items.first().name, spaceAndChats.items.first().id.toLong(),"offline", )
                }else{
                    SpaceScreenUiState.Error("unknow Error")
                }
            }
        }
        state
    }


    sealed class SpaceScreenNavigationRoutes{
        object Back: SpaceScreenNavigationRoutes()
        data class SpaceRedact(val id:Long, val name: String): SpaceScreenNavigationRoutes()
        data class ChatScreen(val chatId:Long): SpaceScreenNavigationRoutes()
        data class ChatWithMessageSend(val message:String, val spaceId:Long): SpaceScreenNavigationRoutes()
        data class ChatRedact(val chatId: Long, val chatName: String, val spaceId: Long): SpaceScreenNavigationRoutes()
    }
    sealed class SpaceScreenUiState{
        object Loading: SpaceScreenUiState()
        data class Success(val chats: List<Chat>,val spaceName:String, val spaceId:Long,val status: String=""): SpaceScreenUiState()
        data class Error(val e: String): SpaceScreenUiState()
    }
}
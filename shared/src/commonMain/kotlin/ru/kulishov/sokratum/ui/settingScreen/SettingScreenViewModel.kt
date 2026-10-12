package ru.kulishov.sokratum.ui.settingScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.kulishov.sokratum.data.remote.model.auth.DeviceDataResponse
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.usecase.auth.GetDeviceDataUseCase
import ru.kulishov.sokratum.domain.usecase.auth.GetKeyUseCase
import ru.kulishov.sokratum.ui.BaseViewModel
import ru.kulishov.sokratum.ui.settingScreen.SettingScreenViewModel.SettingScreenUiState.*

class SettingScreenViewModel(
    private val getDeviceDataUseCase: GetDeviceDataUseCase,
    private val getKeyUseCase: GetKeyUseCase
): BaseViewModel() {
    val uiState: Flow<SettingScreenUiState> = getDeviceDataUseCase().map { device->
        val state: SettingScreenUiState = when(device){
            is RefreshResult.Fresh<DeviceDataResponse> -> {
                Success(device.items)
            }
            is RefreshResult.Cached<*> -> Error("CashedData")
            RefreshResult.Error -> Error("unknow Error")
            is RefreshResult.Offline<*> -> Error("Offline")
        }
        state
    }

    var key by mutableStateOf("")

    fun getKey(){
        launch {
            key=getKeyUseCase()?:""
        }
    }
    fun deleteKey(){
        key=""
    }
    sealed class SettingScreenNavigationRoutes{
        object Back: SettingScreenNavigationRoutes()
        object LogOut: SettingScreenNavigationRoutes()
    }

    sealed class SettingScreenUiState{
        object Loading: SettingScreenUiState()
        data class Success(val items: List<DeviceDataResponse>): SettingScreenUiState()
        data class Error(val e: String): SettingScreenUiState()
    }
}
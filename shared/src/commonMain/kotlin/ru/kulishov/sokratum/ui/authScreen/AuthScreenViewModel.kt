package ru.kulishov.sokratum.ui.authScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.KoinViewModel
import ru.kulishov.sokratum.data.remote.model.auth.AuthRequest
import ru.kulishov.sokratum.data.remote.model.auth.RegistrationRequest
import ru.kulishov.sokratum.domain.usecase.auth.AuthUseCase
import ru.kulishov.sokratum.domain.usecase.auth.RegistrationUseCase
import ru.kulishov.sokratum.getPlatform
import ru.kulishov.sokratum.ui.BaseViewModel

class AuthScreenViewModel(
    private val authUseCase: AuthUseCase,
    private val registrationUseCase: RegistrationUseCase
): BaseViewModel()
{
    private val _uiState = MutableStateFlow<AuthScreenState>(AuthScreenState.AuthInput)
    val uiState: StateFlow<AuthScreenState> = _uiState.asStateFlow()

    var login by mutableStateOf("")
    var password by mutableStateOf("")
    var key by mutableStateOf("")
    var passwordDuplicate by mutableStateOf("")

    fun auth(){
        launch {
            if(login.isEmpty()||password.isEmpty()){
                _uiState.value= AuthScreenState.BadAuthData
            }else{
                val response = authUseCase(AuthRequest(login,password))
                if(response == HttpStatusCode.OK){
                    _uiState.value= AuthScreenState.AuthSuccesss
                }else{
                    _uiState.value= AuthScreenState.BadAuthRequest(response)
                }
            }
        }
    }

    fun registration(){
        launch {
            if(login.isEmpty()||password.isEmpty()||key.isEmpty()||passwordDuplicate.isEmpty()||password!=passwordDuplicate){
                _uiState.value= AuthScreenState.BadRegistrationData
            }else{
                val response = registrationUseCase(RegistrationRequest(
                    key = key,
                    login = login,
                    password = password,
                    device = getPlatform().name
                ))
                if(response == HttpStatusCode.OK){
                    _uiState.value= AuthScreenState.AuthInput
                }else{
                    _uiState.value= AuthScreenState.BadRegistrationRequest(response)
                }
            }
        }
    }

    fun goToRegistration(){
        _uiState.value= AuthScreenState.RegistrationInput
    }

    fun goToAuth(){
        _uiState.value= AuthScreenState.AuthInput
    }

    sealed class AuthScreenState{
        object AuthInput: AuthScreenState()
        object AuthLoading: AuthScreenState()
        object BadAuthData: AuthScreenState()
        class BadAuthRequest(
            val type: HttpStatusCode
        ): AuthScreenState()


        object RegistrationInput: AuthScreenState()
        object RegistrationLoading: AuthScreenState()
        object BadRegistrationData: AuthScreenState()
        class BadRegistrationRequest(
            val type: HttpStatusCode
        ): AuthScreenState()

        object AuthSuccesss: AuthScreenState()
    }
}
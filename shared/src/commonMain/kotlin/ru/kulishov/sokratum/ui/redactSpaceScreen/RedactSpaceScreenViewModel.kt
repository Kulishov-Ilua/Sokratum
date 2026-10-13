package ru.kulishov.sokratum.ui.redactSpaceScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.model.SpaceRedactTransfer
import ru.kulishov.sokratum.domain.usecase.space.AddSpaceUseCase
import ru.kulishov.sokratum.domain.usecase.space.DeleteSpaceUseCase
import ru.kulishov.sokratum.domain.usecase.space.UpdateSpaceUseCase
import ru.kulishov.sokratum.ui.BaseViewModel
import kotlin.time.Clock

class RedactSpaceScreenViewModel(
    private val redactSpaceUseCase: UpdateSpaceUseCase,
    private val createSpaceUseCase: AddSpaceUseCase,
    private val deleteSpaceUseCase: DeleteSpaceUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<RedactSpaceUiState>(RedactSpaceUiState.Loading)
    val uiState: StateFlow<RedactSpaceUiState> = _uiState.asStateFlow()

    var name by mutableStateOf("")
    var spaceId: Long = -1

    fun init(space: SpaceRedactTransfer) {
        if (space.spaceId < 0) {
            _uiState.value = RedactSpaceUiState.Success
        } else {
            name = space.spaceName
            spaceId = space.spaceId
            _uiState.value = RedactSpaceUiState.Success
        }
    }

    fun redact() {
        launch {
            val space = if (spaceId == -1.toLong()) {
                createSpaceUseCase(
                    Space(
                        id = -1,
                        name = name,
                        lastOpen = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                    )
                )
            } else {
                redactSpaceUseCase(
                    Space(
                        id = spaceId,
                        name = name,
                        lastOpen = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                    )
                )
            }
            if (space != null) {
                _uiState.value =
                    RedactSpaceUiState.Navigation(RedactSpaceNavigationRoutes.Space(space.id, space.name))
            } else {
                _uiState.value = RedactSpaceUiState.Error("")
            }
        }

    }
    fun delete(){
        launch {
            deleteSpaceUseCase(Space(spaceId,name, Clock.System.now().toLocalDateTime(TimeZone.UTC)))
            _uiState.value= RedactSpaceUiState.Navigation(RedactSpaceNavigationRoutes.Home)
        }
    }

    sealed class RedactSpaceNavigationRoutes {
        object Home : RedactSpaceNavigationRoutes()
        data class Space(val spaceId: Long, val spaceName:String) : RedactSpaceNavigationRoutes()
    }

    sealed class RedactSpaceUiState {
        object Loading : RedactSpaceUiState()
        object Success : RedactSpaceUiState()
        data class Navigation(val route: RedactSpaceNavigationRoutes) : RedactSpaceUiState()
        data class Error(val e: String) : RedactSpaceUiState()
    }
}
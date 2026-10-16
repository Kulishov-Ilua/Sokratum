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
import ru.kulishov.sokratum.data.remote.model.space.getFile
import ru.kulishov.sokratum.domain.model.File
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.model.SpaceRedactTransfer
import ru.kulishov.sokratum.domain.usecase.space.AddFileUseCase
import ru.kulishov.sokratum.domain.usecase.space.AddSpaceUseCase
import ru.kulishov.sokratum.domain.usecase.space.DeleteFileUseCase
import ru.kulishov.sokratum.domain.usecase.space.DeleteSpaceUseCase
import ru.kulishov.sokratum.domain.usecase.space.GetSpacesAndFilesUseCase
import ru.kulishov.sokratum.domain.usecase.space.UpdateSpaceUseCase
import ru.kulishov.sokratum.ui.BaseViewModel
import kotlin.time.Clock

class RedactSpaceScreenViewModel(
    private val redactSpaceUseCase: UpdateSpaceUseCase,
    private val createSpaceUseCase: AddSpaceUseCase,
    private val deleteSpaceUseCase: DeleteSpaceUseCase,
    private val getSpacesAndFilesUseCase: GetSpacesAndFilesUseCase,
    private val addFilesUseCase: AddFileUseCase,
    private val deleteFileUseCase: DeleteFileUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<RedactSpaceUiState>(RedactSpaceUiState.Loading)
    val uiState: StateFlow<RedactSpaceUiState> = _uiState.asStateFlow()

    var name by mutableStateOf("")
    var spaceId: Long = -1

    fun init(space: SpaceRedactTransfer) {
        launch {
            if (space.spaceId < 0) {
                _uiState.value = RedactSpaceUiState.Success(emptyList())
            } else {
                val space = getSpacesAndFilesUseCase(space.spaceId)
                if(space!=null){
                    name = space.space.name
                    spaceId = space.space.id
                    _uiState.value = RedactSpaceUiState.Success(space.files)
                }else{
                    _uiState.value= RedactSpaceUiState.Error("offline")
                }

            }
        }

    }

    fun addFiles(){
        launch {
            val file = getFile()
            if(file!=null){
                _uiState.value= RedactSpaceUiState.LoadingFiles
                val space = addFilesUseCase(spaceId,file)
                if(space!=null){
                    name = space.space.name
                    spaceId = space.space.id
                    _uiState.value = RedactSpaceUiState.Success(space.files)
                }else{
                    _uiState.value= RedactSpaceUiState.Error("offline")
                }
            }

        }
    }

    fun deleteFile(fileId: String){
        launch {
            _uiState.value= RedactSpaceUiState.LoadingFiles
            val space = deleteFileUseCase(spaceId,fileId)
            if(space!=null){
                name = space.space.name
                spaceId = space.space.id
                _uiState.value = RedactSpaceUiState.Success(space.files)
            }else{
                _uiState.value= RedactSpaceUiState.Error("offline")
            }
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
        if(spaceId!=0.toLong()){
            launch {
                deleteSpaceUseCase(Space(spaceId,name, Clock.System.now().toLocalDateTime(TimeZone.UTC)))
                _uiState.value= RedactSpaceUiState.Navigation(RedactSpaceNavigationRoutes.Home)
            }
        }

    }

    sealed class RedactSpaceNavigationRoutes {
        object Home : RedactSpaceNavigationRoutes()
        data class Space(val spaceId: Long, val spaceName:String) : RedactSpaceNavigationRoutes()
    }

    sealed class RedactSpaceUiState {
        object Loading : RedactSpaceUiState()
        data class Success(val items:List<File>) : RedactSpaceUiState()
        object LoadingFiles: RedactSpaceUiState()
        data class Navigation(val route: RedactSpaceNavigationRoutes) : RedactSpaceUiState()
        data class Error(val e: String) : RedactSpaceUiState()
    }
}
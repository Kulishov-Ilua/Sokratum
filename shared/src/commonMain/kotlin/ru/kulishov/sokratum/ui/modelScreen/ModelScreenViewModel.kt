//package ru.kulishov.sokratum.ui.modelScreen
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.receiveAsFlow
//import org.koin.core.annotation.KoinViewModel
//import ru.kulishov.sokratum.data.ai.AIModels
//import ru.kulishov.sokratum.domain.model.Chat
//import ru.kulishov.sokratum.domain.model.Model
//import ru.kulishov.sokratum.domain.model.ModelRequest
//import ru.kulishov.sokratum.domain.model.Space
//import ru.kulishov.sokratum.domain.usecase.chat.AddChatUseCase
//import ru.kulishov.sokratum.domain.usecase.chat.ModelRequestUseCase
//import ru.kulishov.sokratum.domain.usecase.model.AddModelUseCase
//import ru.kulishov.sokratum.domain.usecase.model.GetModelsUseCase
//import ru.kulishov.sokratum.domain.usecase.space.AddSpaceUseCase
//import ru.kulishov.sokratum.ui.BaseViewModel
//import ru.kulishov.sokratum.ui.navigation.NavigationRoutes
//import kotlin.time.Clock
//
//@KoinViewModel
//class ModelScreenViewModel(
//    val getModelsUseCase: GetModelsUseCase,
//    val modelRequestUseCase: ModelRequestUseCase,
//    val addModelUseCase: AddModelUseCase,
//    val addChatUseCase: AddChatUseCase,
//    val addSpaceUseCase: AddSpaceUseCase
//) : BaseViewModel() {
//    private val _uiState = MutableStateFlow<LoadScreenState>(LoadScreenState.Loading)
//    val uiState: StateFlow<LoadScreenState> = _uiState.asStateFlow()
//
//     var helpWindow by mutableStateOf(false)
//
//    private val _navigation = Channel<NavigationRoutes>()
//    val navigation = _navigation.receiveAsFlow()
//
//    var modelName by mutableStateOf("Gemma4")
//    var modelUrl by mutableStateOf("http://localhost:12434")
//    var modelKey by mutableStateOf("")
//    var timeout by mutableStateOf(300L)
//
//    var message by mutableStateOf("")
//
//
//    init {
//        launch {
//            getModelsUseCase()
//                .catch { e ->
//                    _uiState.value = LoadScreenState.Error(e.message ?: "Unknow error")
//                }
//                .collect { models ->
//                    if (models.isNotEmpty()) {
//                        AIModels.modelList = models
//                        goHome()
//                    } else {
//                        _uiState.value = LoadScreenState.ModelAddHelloMessage
//                    }
//
//                }
//        }
//    }
//
//    fun setModel() {
//        _uiState.value = LoadScreenState.ModelAdd
//    }
//
//    fun addModel() {
//        launch {
//            if (modelName.isEmpty() || modelUrl.isEmpty() || timeout < 1 || timeout > 5000) {
//            } else {
//                _uiState.value = LoadScreenState.LoadingModel
//                val answer = modelRequestUseCase(
//                    ModelRequest(
//                        model = Model(
//                            id = -1,
//                            name = modelName,
//                            url = modelUrl,
//                            key = modelKey,
//                            timeout = timeout
//                        ),
//                        chatId = -1,
//                        systemPrompt = "",
//                        path = "",
//                        text = " Ты подключена к приложению Sokratum, которое позволяет расширить твои знания заметками пользователя и любыми документами на его выбор, для эффективного выполнения задач, обучения и общения с тобой . Поздоровайся с пользователем и кратко расскажи о себе и приложении"
//                    )
//                )
//                if(answer==null||answer.isEmpty()){
//                    _uiState.value= LoadScreenState.ModelError
//                }else{
//                    addModelUseCase(
//                        Model(
//                            id = -1,
//                            name = modelName,
//                            url = modelUrl,
//                            key = modelKey,
//                            timeout = timeout
//                        )
//                    )
//                    val space = addSpaceUseCase(
//                        Space(
//                            id = -1,
//                            name = "Info",
//                            path = "",
//                            storage = "",
//                            lastOpen = Clock.System.now().epochSeconds
//                        )
//                    )
//                    if(space==null){
//                        _uiState.value= LoadScreenState.ModelError
//                    }else{
//                       val chat = addChatUseCase(
//                           chat = Chat(
//                               id = -1,
//                               idSpace = space.id,
//                               name = "Hello",
//                               systemPrompt = "",
//                               lastOpen = Clock.System.now().epochSeconds
//                           )
//                       )
//                        if(chat==null){
//                            _uiState.value= LoadScreenState.ModelError
//                        }else{
//                            message=answer
//                            _uiState.value= LoadScreenState.ModelAddMessage
//                        }
//
//                    }
//                }
//            }
//
//        }
//    }
//
//    fun goHome(){
//        launch {
//            _navigation.send(NavigationRoutes.HomeScreen)
//        }
//    }
//
//    fun goTutorial(){
//        //TODO add navigation
//    }
//
//    fun openHelpWindow(){
//        helpWindow=true
//    }
//
//    fun closeHelpWindow(){
//        helpWindow=false
//    }
//
//    fun goUiState(state: LoadScreenState){
//        _uiState.value=state
//    }
//    sealed class LoadScreenState {
//        object Loading : LoadScreenState()
//
//        object ModelAddHelloMessage : LoadScreenState()
//        object ModelAddMessage : LoadScreenState()
//        object ModelAdd : LoadScreenState()
//
//        object LoadingModel : LoadScreenState()
//
//        object ModelError: LoadScreenState()
//        data class Error(val e: String) : LoadScreenState()
//    }
//}
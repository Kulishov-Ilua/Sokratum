package ru.kulishov.sokratum.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel
import ru.kulishov.sokratum.ui.modelScreen.ModelScreenViewModel

val UiModule = module{
    viewModel<ModelScreenViewModel>{
        ModelScreenViewModel(
            getModelsUseCase = TODO(),
            modelRequestUseCase = TODO(),
            addModelUseCase = TODO(),
            addChatUseCase = TODO(),
            addSpaceUseCase = TODO()
        )
    }
}
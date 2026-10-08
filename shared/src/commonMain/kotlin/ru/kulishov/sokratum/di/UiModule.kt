package ru.kulishov.sokratum.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.kulishov.sokratum.ui.authScreen.AuthScreenViewModel


val uiModule = module {
    viewModelOf(::AuthScreenViewModel)
}
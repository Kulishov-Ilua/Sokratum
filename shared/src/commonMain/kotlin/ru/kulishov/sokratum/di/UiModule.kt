package ru.kulishov.sokratum.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.kulishov.sokratum.ui.authScreen.AuthScreenViewModel
import ru.kulishov.sokratum.ui.homeScreen.HomeScreenViewModel
import ru.kulishov.sokratum.ui.settingScreen.SettingScreenViewModel


val uiModule = module {
    viewModelOf(::AuthScreenViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::SettingScreenViewModel)
}
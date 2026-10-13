package ru.kulishov.sokratum.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.kulishov.sokratum.ui.authScreen.AuthScreenViewModel
import ru.kulishov.sokratum.ui.homeScreen.HomeScreenViewModel
import ru.kulishov.sokratum.ui.redactChatScreen.RedactChatScreenViewModel
import ru.kulishov.sokratum.ui.redactSpaceScreen.RedactSpaceScreenViewModel
import ru.kulishov.sokratum.ui.settingScreen.SettingScreenViewModel
import ru.kulishov.sokratum.ui.spaceScreen.SpaceScreenViewModel


val uiModule = module {
    viewModelOf(::AuthScreenViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::SettingScreenViewModel)
    viewModelOf(::RedactSpaceScreenViewModel)
    viewModelOf(::SpaceScreenViewModel)
    viewModelOf(::RedactChatScreenViewModel)
}
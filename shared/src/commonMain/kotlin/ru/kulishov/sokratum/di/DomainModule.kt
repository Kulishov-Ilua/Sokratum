package ru.kulishov.sokratum.di

import org.koin.dsl.module
import ru.kulishov.sokratum.data.remote.repository.AuthRepositoryImpl
import ru.kulishov.sokratum.data.remote.repository.ChatRepositoryImpl
import ru.kulishov.sokratum.data.remote.repository.SpaceRepositoryImpl
import ru.kulishov.sokratum.domain.repository.AuthRepository
import ru.kulishov.sokratum.domain.repository.ChatRepository
import ru.kulishov.sokratum.domain.repository.SpaceRepository
import ru.kulishov.sokratum.domain.usecase.auth.AuthUseCase
import ru.kulishov.sokratum.domain.usecase.auth.GetDeviceDataUseCase
import ru.kulishov.sokratum.domain.usecase.auth.GetKeyUseCase
import ru.kulishov.sokratum.domain.usecase.auth.RegistrationUseCase
import ru.kulishov.sokratum.domain.usecase.chat.GetLastChatsUseCase
import ru.kulishov.sokratum.domain.usecase.space.GetSpacesDataUseCase


val domainModlule = module {
    single<AuthRepository> { AuthRepositoryImpl() }
    single<SpaceRepository> { SpaceRepositoryImpl(get()) }
    single<ChatRepository> { ChatRepositoryImpl(get()) }

    factory { AuthUseCase(get()) }
    factory { RegistrationUseCase(get()) }
    factory { GetDeviceDataUseCase(get()) }
    factory { GetKeyUseCase(get()) }
    factory { GetLastChatsUseCase(get()) }
    factory { GetSpacesDataUseCase(get()) }
}
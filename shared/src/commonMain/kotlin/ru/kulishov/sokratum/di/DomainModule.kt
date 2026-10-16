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
import ru.kulishov.sokratum.domain.usecase.auth.GetModelsUseCase
import ru.kulishov.sokratum.domain.usecase.auth.RegistrationUseCase
import ru.kulishov.sokratum.domain.usecase.chat.AddChatUseCase
import ru.kulishov.sokratum.domain.usecase.chat.DeleteChatUseCase
import ru.kulishov.sokratum.domain.usecase.chat.GetChatByIdUseCase
import ru.kulishov.sokratum.domain.usecase.chat.GetChatUseCase
import ru.kulishov.sokratum.domain.usecase.chat.GetLastChatsUseCase
import ru.kulishov.sokratum.domain.usecase.chat.MessagesUseCase
import ru.kulishov.sokratum.domain.usecase.chat.SendMessageUseCase
import ru.kulishov.sokratum.domain.usecase.chat.UpdateChatUseCase
import ru.kulishov.sokratum.domain.usecase.space.AddFileUseCase
import ru.kulishov.sokratum.domain.usecase.space.AddSpaceUseCase
import ru.kulishov.sokratum.domain.usecase.space.DeleteFileUseCase
import ru.kulishov.sokratum.domain.usecase.space.DeleteSpaceUseCase
import ru.kulishov.sokratum.domain.usecase.space.GetSpaceAndChatsUseCase
import ru.kulishov.sokratum.domain.usecase.space.GetSpacesAndFilesUseCase
import ru.kulishov.sokratum.domain.usecase.space.GetSpacesDataUseCase
import ru.kulishov.sokratum.domain.usecase.space.UpdateSpaceUseCase


val domainModlule = module {
    single<AuthRepository> { AuthRepositoryImpl() }
    single<SpaceRepository> { SpaceRepositoryImpl(get(), get()) }
    single<ChatRepository> { ChatRepositoryImpl(get(),get()) }

    factory { AuthUseCase(get()) }
    factory { RegistrationUseCase(get()) }
    factory { GetDeviceDataUseCase(get()) }
    factory { GetKeyUseCase(get()) }
    factory { GetLastChatsUseCase(get()) }
    factory { GetSpacesDataUseCase(get()) }
    factory { UpdateSpaceUseCase(get()) }
    factory { AddSpaceUseCase(get()) }
    factory { GetSpaceAndChatsUseCase(get()) }
    factory { DeleteSpaceUseCase(get()) }
    factory { DeleteChatUseCase(get()) }
    factory { AddChatUseCase(get()) }
    factory { UpdateChatUseCase(get()) }
    factory { GetChatByIdUseCase(get()) }
    factory { GetSpacesAndFilesUseCase(get()) }
    factory { AddFileUseCase(get()) }
    factory { DeleteFileUseCase(get()) }
    factory { MessagesUseCase(get()) }
    factory { GetChatUseCase(get()) }
    factory { SendMessageUseCase(get()) }
    factory { GetModelsUseCase(get()) }
}
package ru.kulishov.sokratum.di

import org.koin.dsl.module
import ru.kulishov.sokratum.data.remote.repository.AuthRepositoryImpl
import ru.kulishov.sokratum.domain.repository.AuthRepository
import ru.kulishov.sokratum.domain.usecase.auth.AuthUseCase
import ru.kulishov.sokratum.domain.usecase.auth.GetDeviceDataUseCase
import ru.kulishov.sokratum.domain.usecase.auth.GetKeyUseCase
import ru.kulishov.sokratum.domain.usecase.auth.RegistrationUseCase


val domainModlule = module {
    single<AuthRepository> { AuthRepositoryImpl() }

    factory { AuthUseCase(get()) }
    factory { RegistrationUseCase(get()) }
    factory { GetDeviceDataUseCase(get()) }
    factory { GetKeyUseCase(get()) }
}
package ru.kulishov.sokratum.di

import org.koin.dsl.module
import ru.kulishov.sokratum.data.ai.ModelRequestRepositoryImpl
import ru.kulishov.sokratum.domain.repository.ModelRequestRepository

val aiModule = module {
    //single<ModelRequestRepository> { ModelRequestRepositoryImpl() }
}
package ru.kulishov.sokratum.di

import org.koin.dsl.module
import ru.kulishov.sokratum.domain.usecase.chat.AddChatUseCase
import ru.kulishov.sokratum.domain.usecase.chat.AddMessageUseCase
import ru.kulishov.sokratum.domain.usecase.chat.DeleteChatUseCase
import ru.kulishov.sokratum.domain.usecase.chat.GetChatDataUseCase
import ru.kulishov.sokratum.domain.usecase.chat.GetChatUseCase
import ru.kulishov.sokratum.domain.usecase.chat.GetLastChatsUseCase
import ru.kulishov.sokratum.domain.usecase.chat.SendMessageUseCase
import ru.kulishov.sokratum.domain.usecase.chat.UpdateChatUseCase
import ru.kulishov.sokratum.domain.usecase.chat.UpdateMessagesUseCase
import ru.kulishov.sokratum.domain.usecase.model.AddModelUseCase
import ru.kulishov.sokratum.domain.usecase.model.DeleteModelUseCase
import ru.kulishov.sokratum.domain.usecase.model.GetModelsUseCase
import ru.kulishov.sokratum.domain.usecase.space.AddSpaceUseCase
import ru.kulishov.sokratum.domain.usecase.space.DeleteSpaceUseCase
import ru.kulishov.sokratum.domain.usecase.space.GetSpaceAndChatsUseCase
import ru.kulishov.sokratum.domain.usecase.space.GetSpacesDataUseCase
import ru.kulishov.sokratum.domain.usecase.space.UpdateSpaceUseCase

val domainModlule = module {
    factory {
        AddModelUseCase(
            repository = get()
        )
    }
    factory {
        DeleteModelUseCase(
            repository = get()
        )
    }
    factory {
        GetModelsUseCase(
            repository = get()
        )
    }


    factory {
        AddSpaceUseCase(
            repository = get()
        )
    }
    factory {
        DeleteSpaceUseCase(
            repository = get()
        )
    }
    factory {
        GetSpaceAndChatsUseCase(
            repository = get()
        )
    }
    factory {
        GetSpacesDataUseCase(
            repository = get()
        )
    }
    factory {
        UpdateSpaceUseCase(
            repository = get()
        )
    }



    factory {
        AddChatUseCase(
            repository = get()
        )
    }
    factory {
        AddMessageUseCase(
            repository = get()
        )
    }
    factory {
        DeleteChatUseCase(
            repository = get()
        )
    }
    factory {
        GetChatDataUseCase(
            repository = get()
        )
    }
    factory {
        GetChatUseCase(
            repository = get()
        )
    }
    factory {
        GetLastChatsUseCase(
            repository = get()
        )
    }
    factory {
        SendMessageUseCase(
            modelRequestRepository = get(),
            chatRepository = get()
        )
    }
    factory {
        UpdateChatUseCase(
            repository = get()
        )
    }
    factory {
        UpdateMessagesUseCase(
            repository = get()
        )
    }
















}
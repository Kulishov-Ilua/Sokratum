package ru.kulishov.sokratum.domain.usecase.model

import ru.kulishov.sokratum.domain.model.Model
import ru.kulishov.sokratum.domain.repository.ModelRepository

class AddModelUseCase(
    private val repository: ModelRepository
) {
    suspend operator fun invoke(model: Model): Model? = repository.addModel(model)
}
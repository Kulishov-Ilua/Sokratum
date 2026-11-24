package ru.kulishov.sokratum.data.ai

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.kulishov.sokratum.domain.model.Model


object AIModels {
    private var _models by mutableStateOf<List<Model>>(emptyList())

    var temperature = 0.6

    var modelList: List<Model>
        get() = _models
        set(models: List<Model>) {
            _models = models
        }

    val model: Model?
        get() = actualModel ?: _models.firstOrNull()

    private var _actualModel by mutableStateOf<Model?>(null)

    var actualModel: Model?
        get() = _actualModel ?: _models.firstOrNull()
        set(value) {
            _actualModel = value
        }


    fun addModel(value: Model) {
        val exists = _models.any { it.name == value.name && it.url == value.url }
        if (!exists) {
            _models += value
        }
    }
}

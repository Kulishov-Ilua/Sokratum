package ru.kulishov.sokratum.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

actual abstract class BaseViewModel {
    actual val coroutineScope: CoroutineScope = CoroutineScope(Main)

    actual protected open fun onCleared() {}

    actual fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        return coroutineScope.launch(block = block)
    }

    fun clear() {
        coroutineScope.cancel()
    }
}
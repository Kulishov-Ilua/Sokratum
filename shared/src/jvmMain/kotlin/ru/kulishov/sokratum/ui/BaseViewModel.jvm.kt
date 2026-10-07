package ru.kulishov.sokratum.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


actual abstract class BaseViewModel actual constructor(): ViewModel(){
    actual val coroutineScope: CoroutineScope = viewModelScope

    actual override fun onCleared() {
        coroutineScope.cancel()
        super.onCleared()
    }

    actual fun launch(block: suspend CoroutineScope.()->Unit): Job = coroutineScope.launch { block() }
}
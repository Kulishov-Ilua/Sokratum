package ru.kulishov.sokratum.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

expect abstract class BaseViewModel(): ViewModel {
    val coroutineScope: CoroutineScope

    override protected open fun onCleared()

    fun launch(block: suspend CoroutineScope.()->Unit): Job
}
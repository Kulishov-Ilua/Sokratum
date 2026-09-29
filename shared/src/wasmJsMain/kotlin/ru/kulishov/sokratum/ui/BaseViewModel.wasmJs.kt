package ru.kulishov.sokratum.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

actual abstract class BaseViewModel actual constructor() {
    actual val coroutineScope: kotlinx.coroutines.CoroutineScope
        get() = MainScope()

    private val jobs = mutableListOf<Job>()

    protected actual open fun onCleared() {
        jobs.forEach { it.cancel() }
        jobs.clear()

    }

    actual fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        val job = coroutineScope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                println(e)
            } catch (e: Exception) {
                println(e)
            }
        }
        jobs.add(job)  
        return job
    }

}
package ru.kulishov.sokratum.domain.model

sealed interface RefreshResult<out T> {
    data class Fresh<T>(val items: List<T>) : RefreshResult<T>
    data class Cached<T>(val items: List<T>) : RefreshResult<T>
    data object Error: RefreshResult<Nothing>
    data class Offline<T>(val items: List<T>, val error: RefreshError): RefreshResult<T>
}
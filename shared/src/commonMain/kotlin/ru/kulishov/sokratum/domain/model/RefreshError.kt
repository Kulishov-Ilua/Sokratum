package ru.kulishov.sokratum.domain.model

interface RefreshError {
    data object Network : RefreshError
    data object Unauthorized : RefreshError
    data class Server(val code: Int) : RefreshError
    data class Unknown(val message: String?) : RefreshError
}

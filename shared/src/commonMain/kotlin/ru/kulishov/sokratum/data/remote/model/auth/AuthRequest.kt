package ru.kulishov.sokratum.data.remote.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val login: String,
    val password: String
)

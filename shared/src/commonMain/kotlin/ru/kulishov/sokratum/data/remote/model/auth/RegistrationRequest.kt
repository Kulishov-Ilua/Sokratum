package ru.kulishov.sokratum.data.remote.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val key:String,
    val login: String,
    val password: String,
    val device:String,
)

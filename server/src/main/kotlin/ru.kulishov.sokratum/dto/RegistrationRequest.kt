package ru.kulishov.sokratum.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val login:String,
    val password:String,
    val key: String,
    val device:String
)

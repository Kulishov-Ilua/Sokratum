package ru.kulishov.sokratum.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val login: String,
    val role:String,
    val expiresIn: Long,
    val userId:Int
)

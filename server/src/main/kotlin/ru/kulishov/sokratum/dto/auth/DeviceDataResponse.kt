package ru.kulishov.sokratum.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class DeviceDataResponse(
    val id:Int,
    val login:String,
    val device:String
)

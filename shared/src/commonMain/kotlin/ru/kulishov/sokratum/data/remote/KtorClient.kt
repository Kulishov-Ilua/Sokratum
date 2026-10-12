package ru.kulishov.sokratum.data.remote

import io.ktor.client.HttpClient

object KtorClient {
    val client: HttpClient = getKtorClient()

    var JWT_KEY = ""
    var username = ""
    val BASE_URL = "http://10.0.2.2:3000"

    fun unAuth() {
        JWT_KEY = ""
        username=""
    }

}

expect fun getKtorClient(): HttpClient


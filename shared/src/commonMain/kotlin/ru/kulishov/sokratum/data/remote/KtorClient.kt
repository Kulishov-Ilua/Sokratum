package ru.kulishov.sokratum.data.remote

import io.ktor.client.HttpClient

object KtorClient {
    val client: HttpClient = getKtorClient()

    var JWT_KEY = ""
    val BASE_URL = "http://localhost:3000"

    fun unAuth() {
        JWT_KEY = ""
    }

}

expect fun getKtorClient(): HttpClient
package ru.kulishov.sokratum.data.remote

import io.ktor.client.HttpClient

object KtorClient {
    val client: HttpClient = getKtorClient()

    var JWT_KEY = ""
    var username = ""

    val BASE_URL: String = apiBaseUrl

    fun unAuth() {
        JWT_KEY = ""
        username = ""
    }

}

expect fun getKtorClient(): HttpClient

expect val apiBaseUrl: String

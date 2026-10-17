package ru.kulishov.sokratum.data.remote

import io.ktor.client.HttpClient

object KtorClient {
    val client: HttpClient = getKtorClient()

    var JWT_KEY = ""
    var username = ""

    private const val debugPhone = "http://10.0.2.2:3000"
    private const val debugDesctop = "http://localhost:3000"

    val BASE_URL = debugDesctop

    fun unAuth() {
        JWT_KEY = ""
        username=""
    }

}

expect fun getKtorClient(): HttpClient


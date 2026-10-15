package ru.kulishov.sokratum.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun getKtorClient(): HttpClient = HttpClient(CIO){
    expectSuccess=true
    install(HttpTimeout) {
        requestTimeoutMillis = 30000
        connectTimeoutMillis = 5000
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    install(WebSockets) {
        pingIntervalMillis = 20_000
    }


}
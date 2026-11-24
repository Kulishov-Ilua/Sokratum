package ru.kulishov.sokratum.data.remote

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.io.IOException
import ru.kulishov.sokratum.domain.model.RefreshError
import ru.kulishov.sokratum.domain.model.RefreshResult

fun HttpRequestBuilder.authHeader() {
    if (KtorClient.JWT_KEY.isNotBlank()) {
        headers { append(HttpHeaders.Authorization, "Bearer ${KtorClient.JWT_KEY}") }
    }
}


object ApiClient {

    suspend inline fun <reified T> get(path: String): T? = safeCall {
        KtorClient.client.get("${KtorClient.BASE_URL}$path") { authHeader() }
    }

    suspend inline fun <reified T> post(path: String, body: Any? = null): T? = safeCall {
        KtorClient.client.post("${KtorClient.BASE_URL}$path") {
            contentType(ContentType.Application.Json)
            authHeader()
            if (body != null) setBody(body)
        }
    }

    suspend fun postRaw(path: String, body: Any? = null): HttpResponse? = try {
        KtorClient.client.post("${KtorClient.BASE_URL}$path") {
            contentType(ContentType.Application.Json)
            authHeader()
            if (body != null) setBody(body)
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: ClientRequestException) {
        logHttpError(e)
        e.response
    } catch (e: Exception) {
        println("Error: ${e.message}")
        null
    }

    suspend inline fun <reified T> safeCall(block: () -> HttpResponse): T? {
        return try {
            val response = block()
            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                println("HTTP error: ${response.status}")
                null
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: ClientRequestException) {
            logHttpError(e)
            null
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }

    suspend fun logHttpError(e: ClientRequestException) {
        println("HTTP error: ${e.response.status}")
        println("Error body: ${e.response.bodyAsText()}")
    }
}

/**
 * Выполняет сетевой запрос внутри flow и мапит типовые ошибки на [RefreshResult]:
 * кэш есть — Offline(кэш, ошибка), кэша нет — Error.
 * Возвращает результат блока или null, если запрос завершился ошибкой.
 */
internal suspend fun <T> fetchWithCache(
    cached: List<T>,
    emit: suspend (RefreshResult<T>) -> Unit,
    fetch: suspend () -> List<T>
): List<T>? {
    return try {
        val fresh = fetch()
        emit(RefreshResult.Fresh(fresh))
        fresh
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        if (cached.isNotEmpty()) {
            emit(RefreshResult.Offline(cached, RefreshError.Network))
        } else {
            emit(RefreshResult.Error)
        }
        null
    } catch (e: ClientRequestException) {
        val error = if (e.response.status == HttpStatusCode.Unauthorized) {
            RefreshError.Unauthorized
        } else {
            RefreshError.Server(e.response.status.value)
        }
        if (cached.isNotEmpty()) {
            emit(RefreshResult.Offline(cached, error))
        } else {
            emit(RefreshResult.Error)
        }
        null
    } catch (e: ServerResponseException) {
        if (cached.isNotEmpty()) {
            emit(RefreshResult.Offline(cached, RefreshError.Server(e.response.status.value)))
        } else {
            emit(RefreshResult.Error)
        }
        null
    } catch (e: Exception) {
        e.printStackTrace()
        emit(RefreshResult.Error)
        null
    }
}

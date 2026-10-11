package ru.kulishov.sokratum.data.remote.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.io.IOException
import ru.kulishov.sokratum.data.local.dao.SpaceDao
import ru.kulishov.sokratum.data.local.data.mapper.SpaceMapper
import ru.kulishov.sokratum.data.remote.KtorClient.BASE_URL
import ru.kulishov.sokratum.data.remote.KtorClient.JWT_KEY
import ru.kulishov.sokratum.data.remote.KtorClient.client
import ru.kulishov.sokratum.domain.model.RefreshError
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.model.SpaceAndChats
import ru.kulishov.sokratum.domain.model.SpaceWithoutStorage
import ru.kulishov.sokratum.domain.repository.SpaceRepository

class SpaceRepositoryImpl(
    private val spaceDao: SpaceDao
): SpaceRepository {
    override  fun getSpacesData():Flow<RefreshResult<Space>> = flow {
        val cached = spaceDao.getSpaces()
        if (cached.isNotEmpty()) {
            emit(RefreshResult.Cached(cached.map { SpaceMapper.toDomain(it) }))
        }
        try{
            val response = client.get("${BASE_URL}/getSpaces"){
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
            }
            val body:List<Space> = response.body()
            spaceDao.replaceAll(body.map { SpaceMapper.toEntity(it) })
            emit(RefreshResult.Fresh(body))
        }
        catch (e: CancellationException){
            throw e
        }
        catch (e: IOException){
             if(cached.isNotEmpty()){
                 emit(RefreshResult.Offline(cached.map { SpaceMapper.toDomain(it) }, RefreshError.Network))
             }else{
                 emit(RefreshResult.Error)
             }
        }
        catch (e: ClientRequestException){
            val error = if (e.response.status == HttpStatusCode.Unauthorized) {
                RefreshError.Unauthorized
            } else {
                RefreshError.Server(e.response.status.value)
            }
            if(cached.isNotEmpty()){
                emit(RefreshResult.Offline(cached.map { SpaceMapper.toDomain(it) }, error))
            }else{
                emit(RefreshResult.Error)
            }
        }
        catch (e: ServerResponseException){
            if(cached.isNotEmpty()){
                emit(RefreshResult.Offline(cached.map { SpaceMapper.toDomain(it) }, RefreshError.Server(e.response.status.value)))
            }else{
                emit(RefreshResult.Error)
            }
        }
        catch (e: Exception){
            emit(RefreshResult.Error)
        }
    }


    override suspend fun getSpaceAndChats(spaceId: Long): SpaceAndChats? {
        var ret: SpaceAndChats? = null
        try {
            val response = client.get("${BASE_URL}/getSpaceAndChats/$spaceId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
            }
            if (response.status == HttpStatusCode.OK) {
                ret = response.body()
            }
        }catch (e: ClientRequestException) {
            println("HTTP error: ${e.response.status}")
            val errorBody = e.response.bodyAsText()
            println("Error body: $errorBody")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
        return ret
    }

    override suspend fun addSpace(space: Space): Space? {
        var ret: Space? = null
        try {
            val response = client.post("${BASE_URL}/addSpace") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
                setBody(space)

            }
            if (response.status == HttpStatusCode.OK) {
                ret = response.body()
            }
        }catch (e: ClientRequestException) {
            println("HTTP error: ${e.response.status}")
            val errorBody = e.response.bodyAsText()
            println("Error body: $errorBody")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
        return ret
    }

    override suspend fun updateSpace(space: Space): Space? {
        var ret: Space? = null
        try {
            val response = client.post("${BASE_URL}/updateSpace") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
                setBody(space)

            }
            if (response.status == HttpStatusCode.OK) {
                ret = response.body()
            }
        }catch (e: ClientRequestException) {
            println("HTTP error: ${e.response.status}")
            val errorBody = e.response.bodyAsText()
            println("Error body: $errorBody")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
        return ret
    }

    override suspend fun deleteSpace(space: Space) {
        try {
            val response = client.post("${BASE_URL}/deleteSpace") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $JWT_KEY")
                }
                setBody(space)

            }
            if (response.status == HttpStatusCode.OK) {
            }
        }catch (e: ClientRequestException) {
            println("HTTP error: ${e.response.status}")
            val errorBody = e.response.bodyAsText()
            println("Error body: $errorBody")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}
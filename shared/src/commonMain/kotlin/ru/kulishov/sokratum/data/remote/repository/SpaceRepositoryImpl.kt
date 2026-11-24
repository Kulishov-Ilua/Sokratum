package ru.kulishov.sokratum.data.remote.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.IOException
import ru.kulishov.sokratum.data.local.dao.ChatDao
import ru.kulishov.sokratum.data.local.dao.SpaceDao
import ru.kulishov.sokratum.data.local.data.mapper.ChatMapper
import ru.kulishov.sokratum.data.local.data.mapper.SpaceMapper
import ru.kulishov.sokratum.data.remote.ApiClient
import ru.kulishov.sokratum.data.remote.KtorClient.BASE_URL
import ru.kulishov.sokratum.data.remote.KtorClient.JWT_KEY
import ru.kulishov.sokratum.data.remote.KtorClient.client
import ru.kulishov.sokratum.data.remote.authHeader
import ru.kulishov.sokratum.data.remote.fetchWithCache
import ru.kulishov.sokratum.data.remote.model.space.SelectedFile
import ru.kulishov.sokratum.data.remote.model.space.SpaceAndFiles
import ru.kulishov.sokratum.domain.model.RefreshError
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.domain.model.SpaceAndChats
import ru.kulishov.sokratum.domain.repository.SpaceRepository

class SpaceRepositoryImpl(
    private val spaceDao: SpaceDao,
    private val chatDao: ChatDao
) : SpaceRepository {

    override fun getSpacesData(): Flow<RefreshResult<Space>> = flow {
        val cached = spaceDao.getSpaces()
        if (cached.isNotEmpty()) {
            emit(RefreshResult.Cached(cached.map { SpaceMapper.toDomain(it) }))
        }
        val cachedDomain = cached.map { SpaceMapper.toDomain(it) }
        fetchWithCache(cachedDomain, { emit(it) }) {
            val body: List<Space> = client.get("${BASE_URL}/getSpaces") {
                headers { append(HttpHeaders.Authorization, "Bearer $JWT_KEY") }
            }.body()
            spaceDao.replaceAll(body.map { SpaceMapper.toEntity(it) })
            body
        }
    }

    override fun getSpaceAndChats(spaceId: Long): Flow<RefreshResult<SpaceAndChats>> = flow {
        val cached = spaceDao.getSpaceAndChats(spaceId)
        if (cached != null) {
            emit(RefreshResult.Cached(listOf(cached)))
        }
        val cachedList = listOfNotNull(cached)
        fetchWithCache(cachedList, { emit(it) }) {
            val body: SpaceAndChats = client.get("${BASE_URL}/getSpaceAndChats/$spaceId") {
                headers { append(HttpHeaders.Authorization, "Bearer $JWT_KEY") }
            }.body()
            chatDao.replaceAll(body.chats.map { ChatMapper.toEntity(it) })
            listOf(body)
        }
    }

    override suspend fun getSpaceAndFiles(spaceId: Long): SpaceAndFiles? =
        ApiClient.get("/getSpaceAndFiles/$spaceId")

    override suspend fun deleteFile(spaceId: Long, fileId: String): SpaceAndFiles? =
        ApiClient.post("/deleteFile/$spaceId/$fileId")

    override suspend fun addFile(spaceId: Int, file: SelectedFile): SpaceAndFiles? =
        ApiClient.safeCall {
            client.post("${BASE_URL}/addFile/$spaceId") {
                authHeader()
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                key = "file",
                                value = file.bytes,
                                headers = Headers.build {
                                    append(HttpHeaders.ContentType, file.type)
                                    append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                                }
                            )
                        }
                    )
                )
            }
        }

    override suspend fun addSpace(space: Space): Space? =
        ApiClient.post("/addSpace", space)

    override suspend fun updateSpace(space: Space): Space? =
        ApiClient.post("/updateSpace", space)

    override suspend fun deleteSpace(space: Space) {
        ApiClient.postRaw("/deleteSpace", space)
    }
}

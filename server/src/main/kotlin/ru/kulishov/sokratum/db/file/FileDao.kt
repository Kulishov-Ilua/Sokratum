package ru.kulishov.sokratum.db.file

import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import ru.kulishov.sokratum.db.query
import java.time.LocalDateTime
import kotlin.uuid.Uuid

class FileDao {
    fun rowToFile(row: ResultRow): File = File(
        id = row[Files.id].toString(),
        spaceId = row[Files.spaceId],
        name = row[Files.name],
        size = row[Files.size],
        contentType = row[Files.contentType],
        path = row[Files.path],
        uploadedAt = row[Files.uploadedAt].toKotlinLocalDateTime(),
        updatedAt = row[Files.updatedAt]?.toKotlinLocalDateTime()
    )

    suspend fun addFile(
        id: Uuid,
        spaceId: Int,
        name: String,
        size: Long,
        contentType: String,
        path: String,
    ): File? = query {
        val document = Files.insert {
            it[Files.id] = id
            it[Files.spaceId] = spaceId
            it[Files.name] = name
            it[Files.size] = size
            it[Files.contentType] = contentType
            it[Files.path] = path
            it[Files.uploadedAt] = LocalDateTime.now()
        }.resultedValues?.map { rowToFile(it) }?.firstOrNull() ?: return@query null
        return@query document
    }

    suspend fun getSpaceFiles(spaceId: Int): List<File> = query {
        return@query Files.selectAll().where { Files.spaceId eq spaceId }.map { rowToFile(it) }
    }

    suspend fun deleteFile(id: Int) {

    }
}
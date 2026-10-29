package ru.kulishov.sokratum.db.file

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.datetime
import ru.kulishov.sokratum.db.space.Spaces

object Files : Table() {
    val id = uuid("id")
    val spaceId = integer("spaceId").references(Spaces.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 255)
    val size = long("size")
    val contentType = varchar("content_type", 128)
    val path = varchar("path", 1024)
    val uploadedAt = datetime("upload_time")
    val updatedAt = datetime("updated_at").nullable()

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class File(
    val id: String,
    val spaceId:Int,
    val name: String,
    val size:Long,
    val contentType: String,
    val path: String,
    val uploadedAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null
)


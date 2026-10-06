package ru.kulishov.sokratum.ai.rag

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.uuid.Uuid


object FileStorrage {
    private var baseDir: File? = null

    fun init() {
        baseDir = File("documents").apply { mkdirs() }
    }

    fun save(id: Uuid, provider: ByteReadChannel): Long {
        val file = fileOf(id)
        file.outputStream().buffered().use { out ->
            runBlocking { provider.copyTo(out) }
        }
        return file.length()
    }

    fun fileOf(id: Uuid): File = File(baseDir, id.toString())

    fun delete(id: Uuid): Boolean = fileOf(id).delete()

    fun exists(id: Uuid): Boolean = fileOf(id).exists()

}
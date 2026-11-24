package ru.kulishov.sokratum.ai.rag

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore
import org.jetbrains.exposed.v1.core.exposedLogger
import ru.kulishov.sokratum.db.DBFactory.fileDao
import java.io.File

object RAG {
    private val storages = java.util.concurrent.ConcurrentHashMap<Int, EmbeddingStoreContentRetriever>()

    fun deleteStorage(spaceId: Int) {
        storages.remove(spaceId)
    }

    suspend fun getRetriever(spaceId: Int): EmbeddingStoreContentRetriever {
        storages[spaceId]?.let { return it }
        val built = buildRetriever(spaceId)
        return storages.putIfAbsent(spaceId, built) ?: built
    }

    private suspend fun buildRetriever(spaceId: Int): EmbeddingStoreContentRetriever {
        val documents = mutableListOf<Document>()
        fileDao.getSpaceFiles(spaceId).forEach { file ->
            try {
                val text = File(file.path).readText()
                if (text.isNotBlank()) {
                    documents.add(Document.from(text))
                }
            } catch (e: Exception) {
                exposedLogger.warn("Не удалось прочитать ${file.path}", e)
            }
        }
        val embeddingStore = InMemoryEmbeddingStore<TextSegment?>()
        if (documents.isNotEmpty()) EmbeddingStoreIngestor.ingest(documents, embeddingStore)
        return EmbeddingStoreContentRetriever.from(embeddingStore)
    }
}
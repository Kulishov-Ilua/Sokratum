//package ru.kulishov.sokratum.data.ai
//
//import dev.langchain4j.data.document.Document
//import dev.langchain4j.data.segment.TextSegment
//import dev.langchain4j.model.openai.OpenAiChatModel
//import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
//import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
//import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore
//import ru.kulishov.sokratum.domain.model.Model
//import java.io.File
//import java.time.Duration
//
//object AndroidAIModel {
//    private val _models = mutableMapOf<String, OpenAiChatModel>()
//    private val _storaages = mutableMapOf<String, EmbeddingStoreContentRetriever>()
//
//    fun getChatModel(model: Model): OpenAiChatModel {
//        var findModel = _models[model.name]
//        if (findModel == null) {
//            findModel = OpenAiChatModel.builder()
//                .baseUrl(model.url)
//                .apiKey(model.key)
//                .modelName(model.name)
//                .logRequests(true)
//                .logResponses(true)
//                .timeout(Duration.ofSeconds(model.timeout))
//                .build()
//            _models[model.name] = findModel
//        }
//        return findModel
//    }
//
//    suspend fun getStorage(path: String): EmbeddingStoreContentRetriever {
//        var findStorrage = _storaages[path]
//        if (findStorrage == null) {
//            val dir = File(path)
//            val embeddingStore: InMemoryEmbeddingStore<TextSegment?> =
//                InMemoryEmbeddingStore<TextSegment?>()
//            val documents = dir.walkTopDown()
//                .filter { it.isFile }
//                .map { file ->
//                    Document.from(file.readText())
//                }
//                .toList()
//
//            if(documents.isNotEmpty()){
//                EmbeddingStoreIngestor.ingest(documents,embeddingStore)
//            }
//            findStorrage = EmbeddingStoreContentRetriever.from(embeddingStore)
//            _storaages[path]=findStorrage
//        }
//        return findStorrage
//    }
//}
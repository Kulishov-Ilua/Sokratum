package ru.kulishov.sokratum.data.ai

import ru.kulishov.sokratum.domain.model.Model


object AIModels {
    private val _models = mutableListOf<Model>()

    val modelList: List<Model>
        get() = _models

    var model: Model
        get() = _models.first()
        set(value) {
            val exists = _models.any {
                it.name == value.name && it.url == value.url
            }
            if (!exists) {
                _models.add(value)
            }
        }
    private var _actualModel: Model? = null

    var actualModel: Model?
        get() = _actualModel ?: _models.firstOrNull()
        set(value) {
            _actualModel = value
        }

//    fun getModelApi(model: Model): OpenAILLMClient {
//        var findApi = modelsMap["${model.url}${model.name}"]
//        if (findApi == null) {
//            findApi = OpenAILLMClient(
//                apiKey = model.key,
//                settings = OpenAIClientSettings(
//                    baseUrl = model.url,
//                    embeddingsPath = "v1/embeddings"
//                ),
//                httpClientFactory = KtorKoogHttpClient.Factory(),
//            )
//            modelsMap += "${model.url}${model.name}" to findApi
//        }
//        return findApi
//    }
//
//
//
//    private val _eStorrage = mutableMapOf<String, EmbeddingStorage>()
//
//    fun getStorage(path: String): EmbeddingStorage?{
//        var strorageFind = _eStorrage[path]
//        if(_actualModel!=null){
//            if(strorageFind==null){
//                val embedder = LLMEmbedder(
//                    client = ,
//                    model = OpenAIModels.Embeddings.TextEmbeddingAda002
//                )
//                val storage = EmbeddingStorage(
//                    embedder = OpenAIEmbeddingProvider(),
//                    storage = InMemoryVectorStorageBackend()
//                )
//            }
//        }
//
//        return strorageFind
//    }

}
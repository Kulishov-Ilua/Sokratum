package ru.kulishov.sokratum.data.remote.model.space

expect suspend fun getFile(): SelectedFile?

fun getMimeType(fileName: String): String {
    //val extension = fileName.substringAfterLast('.', "").lowercase()
    return when (fileName) {
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "gif" -> "image/gif"
        "pdf" -> "application/pdf"
        "txt" -> "text/plain"
        "json" -> "application/json"
        "xml" -> "application/xml"
        "zip" -> "application/zip"
        "mp4" -> "video/mp4"
        "mp3" -> "audio/mpeg"
        "md" -> "text/md"
        else -> "application/octet-stream"  // По умолчанию
    }
}


package ru.kulishov.sokratum.data.remote.model.space

data class SelectedFile(
    val name: String,
    val type: String,
    val bytes: ByteArray,
)
package ru.kulishov.sokratum.data.remote.model.space

import java.nio.file.Files
import javax.swing.JFileChooser

actual suspend fun getFile(): SelectedFile? {
    val fileChooser = JFileChooser()

    val result = fileChooser.showOpenDialog(null)
    val selectedFile = if (result == JFileChooser.APPROVE_OPTION) {
        val file = fileChooser.selectedFile
        SelectedFile(
            name = file.name,
            type = getMimeType(file.extension),
            bytes = Files.readAllBytes(file.toPath())
        )
    } else {
        null
    }
    return selectedFile
}
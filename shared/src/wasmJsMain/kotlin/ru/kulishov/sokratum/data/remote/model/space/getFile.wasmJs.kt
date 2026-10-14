package ru.kulishov.sokratum.data.remote.model.space


import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.khronos.webgl.DataView
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader

actual suspend fun getFile(): SelectedFile? {
    return suspendCancellableCoroutine { continuation ->
        val input = document.createElement("input") as HTMLInputElement
        input.type = "file"
        input.style.display = "none"

        input.addEventListener("change", {
            val file = input.files?.item(0)
            if (file != null) {
                val reader = FileReader()

                reader.onload = {
                    val arrayBuffer = reader.result as org.khronos.webgl.ArrayBuffer
                    val bytes = arrayBufferToByteArray(arrayBuffer)

                    val selectedFile = SelectedFile(
                        name = file.name,
                        type = file.type.ifEmpty { getMimeType(file.name.substringAfterLast(".")) },
                        bytes = bytes
                    )
                    continuation.resume(selectedFile) { cause ->
                        println("Resume cancelled: $cause")
                    }
                    document.body?.removeChild(input)
                }

                reader.onerror = {
                    continuation.resume(null) { cause ->
                        println("Resume cancelled: $cause")
                    }
                    document.body?.removeChild(input)
                }

                reader.readAsArrayBuffer(file)
            } else {
                continuation.resume(null) { cause ->
                    println("Resume cancelled: $cause")
                }
                document.body?.removeChild(input)
            }
        })

        document.body?.appendChild(input)
        input.click()

        continuation.invokeOnCancellation {
            document.body?.removeChild(input)
        }
    }
}

private fun arrayBufferToByteArray(arrayBuffer: org.khronos.webgl.ArrayBuffer): ByteArray {
    val byteArray = ByteArray(arrayBuffer.byteLength.toInt())
    val view = DataView(arrayBuffer)
    for (i in 0 until arrayBuffer.byteLength) {
        byteArray[i] = view.getUint8(i).toByte()
    }
    return byteArray
}
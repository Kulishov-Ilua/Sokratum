package ru.kulishov.sokratum.data.remote.model.space

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object AndroidFilePickerSetup {
    internal var launcher: ActivityResultLauncher<String>? = null
    internal var continuation: CancellableContinuation<SelectedFile?>? = null
    internal var appContext: Context? = null

    fun register(activity: ComponentActivity) {
        appContext = activity.applicationContext
        launcher = activity.registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            val cont = continuation ?: return@registerForActivityResult
            continuation = null
            if (uri == null) {
                cont.resume(null)
                return@registerForActivityResult
            }
            try {
                cont.resume(readPicked(activity, uri))
            } catch (t: Throwable) {
                cont.resumeWithException(t)
            }
        }
    }

    private fun readPicked(ctx: Context, uri: Uri): SelectedFile {
        val resolver = ctx.contentResolver
        val name = resolver.query(uri, null, null, null, null)?.use { c ->
            val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx >= 0 && c.moveToFirst()) c.getString(idx) else null
        } ?: uri.lastPathSegment ?: "unnamed"

        val mime = resolver.getType(uri) ?: "application/octet-stream"
        val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
        return SelectedFile(name, mime, bytes)
    }
}

actual suspend fun getFile(): SelectedFile?  = suspendCancellableCoroutine { cont ->
    val launcher = AndroidFilePickerSetup.launcher
    if (launcher == null) {
        cont.resume(null)
        return@suspendCancellableCoroutine
    }
    AndroidFilePickerSetup.continuation = cont
    launcher.launch("*/*")
    cont.invokeOnCancellation { AndroidFilePickerSetup.continuation = null }
}


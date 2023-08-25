package com.stebitto.uploadimages

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

fun Context.getTmpFileUri(filename: String): Uri {
    val tmpFile = File.createTempFile(filename, ".png", cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }

    return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
}

/**
 * Copy text to clipboard and return true if user was informed about it, false otherwise.
 * Users expect visual feedback when an app copies content to the clipboard.
 * This is done automatically for users in Android 13 and higher, but it must be manually
 * implemented in prior versions.
 */
fun Context.copyTextToClipboard(textToCopy: String): Boolean {
    val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newPlainText("", textToCopy))

    return Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2
}

//fun Context.getBitmap(uri: Uri): Bitmap {
//    val decoder = ImageDecoder.createSource(contentResolver, uri)
//    return ImageDecoder.decodeBitmap(decoder)
//}

//fun Bitmap.encodeImage(): String {
//    val outputStream = ByteArrayOutputStream()
//    compress(Bitmap.CompressFormat.JPEG, 10, outputStream)
//    val b = outputStream.toByteArray()
//    return Base64.encodeToString(b, Base64.DEFAULT)
//}

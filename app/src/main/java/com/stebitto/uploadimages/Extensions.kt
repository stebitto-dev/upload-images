package com.stebitto.uploadimages

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun Context.getTmpFileUri(): Uri {
    val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }

    return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
}

fun Context.copyTextToClipboard(textToCopy: String) {
    val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newPlainText("", textToCopy))
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

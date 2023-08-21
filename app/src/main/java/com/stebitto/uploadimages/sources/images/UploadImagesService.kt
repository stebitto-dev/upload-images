package com.stebitto.uploadimages.sources.images

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.IOException

interface UploadImagesService {
    @Multipart
    @POST("user/api.php")
    suspend fun uploadImage(
        @Part("reqtype") requestType: RequestBody,
        @Part fileToUpload: MultipartBody.Part
    ): String
}

class ContentUriRequestBody(
    private val contentResolver: ContentResolver,
    private val contentUri: Uri
) : RequestBody() {

    override fun contentType(): MediaType? {
        val contentType = contentResolver.getType(contentUri)
        return contentType?.toMediaTypeOrNull()
    }

    override fun writeTo(sink: BufferedSink) {
        val inputStream = contentResolver.openInputStream(contentUri)
            ?: throw IOException("Couldn't open content URI for reading")

        inputStream.source().use { source ->
            sink.writeAll(source)
        }

        inputStream.close()
    }
}
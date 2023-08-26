package com.stebitto.uploadimages.sources.images

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

interface IUploadImagesRepository {
    suspend fun uploadImage(contentUri: Uri): String
}

class UploadImagesRepository @Inject constructor(
    private val context: Context,
    private val uploadImagesService: UploadImagesService
) : IUploadImagesRepository {

    override suspend fun uploadImage(contentUri: Uri): String {
        val requestFile = ContentUriRequestBody(context.contentResolver, contentUri)
        val requestType = "fileupload".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData(
            "fileToUpload",
            contentUri.lastPathSegment,
            requestFile
        )
        return uploadImagesService.uploadImage(requestType, multipartBody)
    }
}
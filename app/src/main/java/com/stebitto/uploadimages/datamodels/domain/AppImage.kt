package com.stebitto.uploadimages.datamodels.domain

import android.net.Uri
import java.util.UUID

data class AppImage(
    val contentUri: Uri,
    val status: UploadImageStatus = UploadImageStatus.TO_UPLOAD,
    val url: String? = null
) {
    val id:String = UUID.randomUUID().toString()
}

enum class UploadImageStatus {
    TO_UPLOAD, IS_LOADING, UPLOADED, ERROR
}

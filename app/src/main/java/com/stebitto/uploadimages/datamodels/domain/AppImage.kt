package com.stebitto.uploadimages.datamodels.domain

import android.net.Uri

data class AppImage(
    val id: String,
    val contentUri: Uri,
    val name: String,
    val status: UploadImageStatus = UploadImageStatus.TO_UPLOAD,
    val url: String? = null
)

enum class UploadImageStatus {
    TO_UPLOAD, IS_LOADING, UPLOADED, ERROR
}

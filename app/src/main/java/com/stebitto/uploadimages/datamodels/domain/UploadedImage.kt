package com.stebitto.uploadimages.datamodels.domain

data class UploadedImage(
    val id: String,
    val contentUri: String,
    val name: String,
    val isLoading: Boolean = false,
    val isUploaded: Boolean = false,
    val url: String? = null
)

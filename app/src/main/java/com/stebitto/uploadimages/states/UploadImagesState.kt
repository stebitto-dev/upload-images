package com.stebitto.uploadimages.states

import com.stebitto.uploadimages.datamodels.domain.AppImage

sealed interface UploadImagesState : AppState {

    object PickImages : UploadImagesState

    data class UploadedImages(
        val images: List<AppImage>
    ) : UploadImagesState

    data class Error(val message: String?) : UploadImagesState
}
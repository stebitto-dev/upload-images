package com.stebitto.uploadimages.states

import com.stebitto.uploadimages.datamodels.domain.UploadedImage

sealed interface UploadImagesState : AppState {

    object PickImages : UploadImagesState

    data class UploadedImages(
        val images: List<UploadedImage>
    ) : UploadImagesState

    data class Error(val message: String?) : UploadImagesState
}
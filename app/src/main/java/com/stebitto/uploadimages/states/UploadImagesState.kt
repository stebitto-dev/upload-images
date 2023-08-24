package com.stebitto.uploadimages.states

import com.stebitto.uploadimages.datamodels.domain.AppImage

sealed interface UploadImagesState : AppState {

    data class PickImages(
        val uploadedImages: List<AppImage> = emptyList(),
        val imagesToUpload: List<AppImage> = emptyList()
    ) : UploadImagesState

    data class UploadingImages(
        val uploadedImages: List<AppImage> = emptyList(),
        val uploadingImages: List<AppImage>
    ) : UploadImagesState
}
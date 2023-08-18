package com.stebitto.uploadimages.states

sealed interface UploadImagesState : AppState {

    object PickImages : UploadImagesState

    data class Error(val message: String?) : UploadImagesState
}
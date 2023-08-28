package com.stebitto.uploadimages.actions

import com.stebitto.uploadimages.datamodels.domain.AppImage

data class PickedImages(
    val images: List<AppImage>
): Action

data class RemoveImage(
    val image: AppImage
): Action

object UploadImages : Action

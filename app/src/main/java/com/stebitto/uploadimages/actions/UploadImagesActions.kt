package com.stebitto.uploadimages.actions

import android.net.Uri
import com.stebitto.uploadimages.datamodels.domain.AppImage

data class PickedImages(
    val images: List<Uri>
): Action

data class RemoveImage(
    val image: AppImage
): Action

object UploadImages : Action

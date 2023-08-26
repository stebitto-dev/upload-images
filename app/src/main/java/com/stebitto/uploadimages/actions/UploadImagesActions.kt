package com.stebitto.uploadimages.actions

import android.net.Uri

data class PickedImages(
    val images: List<Uri>
): Action

object UploadImages : Action

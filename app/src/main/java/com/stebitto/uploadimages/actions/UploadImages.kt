package com.stebitto.uploadimages.actions

import android.net.Uri
import java.io.InputStream

data class UploadImages(
    val images: List<Uri>
) : Action

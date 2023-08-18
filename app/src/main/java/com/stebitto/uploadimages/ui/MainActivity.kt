package com.stebitto.uploadimages.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.stebitto.uploadimages.ui.screens.UploadImagesApp
import com.stebitto.uploadimages.ui.theme.UploadImagesTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Registers a photo picker activity launcher in multi-select mode.
    val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            // Callback is invoked after the user selects media items or closes the
            // photo picker.
            if (uris.isNotEmpty()) {
                Timber.d("Number of items selected: ${uris.size}")
            } else {
                Timber.d("No media selected")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UploadImagesTheme {
                UploadImagesApp()
            }
        }
    }
}
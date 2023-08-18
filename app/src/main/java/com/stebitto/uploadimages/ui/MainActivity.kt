package com.stebitto.uploadimages.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.stebitto.uploadimages.ui.screens.UploadImagesApp
import com.stebitto.uploadimages.ui.theme.UploadImagesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UploadImagesTheme {
                UploadImagesApp()
            }
        }
    }
}
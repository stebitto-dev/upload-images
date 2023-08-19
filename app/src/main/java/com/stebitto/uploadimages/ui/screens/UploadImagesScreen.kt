package com.stebitto.uploadimages.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.stebitto.uploadimages.R
import com.stebitto.uploadimages.states.UploadImagesState
import com.stebitto.uploadimages.ui.theme.UploadImagesTheme

@Composable
fun UploadImagesFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() }
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_cloud_upload),
            contentDescription = stringResource(R.string.bottom_bar_upload_description)
        )
    }
}

@Composable
fun UploadImagesBottomBar(
    onGalleryClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onFABClick: () -> Unit = {}
) {
    BottomAppBar(
        actions = {
            // Gallery
            IconButton(onClick = { onGalleryClick() }) {
                Icon(
                    painter = painterResource(R.drawable.icon_gallery),
                    contentDescription = stringResource(R.string.bottom_bar_gallery_description)
                )
            }
            // Camera
            IconButton(onClick = { onCameraClick() }) {
                Icon(
                    painter = painterResource(R.drawable.icon_photo_camera),
                    contentDescription = stringResource(R.string.bottom_bar_camera_description)
                )
            }
        },
        floatingActionButton = { UploadImagesFAB { onFABClick() } }
    )
}

@Composable
fun UploadImagesScreen(
    uiState: UploadImagesState,
    modifier: Modifier
) {
    when (uiState) {
        is UploadImagesState.PickImages -> { EmptyListLabel(modifier) }
        is UploadImagesState.Error -> {}
    }
}

@Composable
fun EmptyListLabel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.upload_images_empty_list),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    widthDp = 320
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 320,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun UploadImagesBottomBarPreview() {
    UploadImagesTheme {
        UploadImagesBottomBar()
    }
}
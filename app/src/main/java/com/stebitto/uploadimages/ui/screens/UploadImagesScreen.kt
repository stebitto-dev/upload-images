package com.stebitto.uploadimages.ui.screens

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.stebitto.uploadimages.R
import com.stebitto.uploadimages.datamodels.domain.AppImage
import com.stebitto.uploadimages.datamodels.domain.UploadImageStatus
import com.stebitto.uploadimages.states.UploadImagesState
import com.stebitto.uploadimages.ui.theme.UploadImagesTheme

const val TEST_TAG_EMPTY_LABEL = "Empty label"
const val TEST_TAG_IMAGE_CARD = "Images list"
const val TEST_TAG_IMAGE_CARD_REMOVE_ACTION = "Remove action"

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
    onGooglePhotoClick: () -> Unit = {},
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
            // Google Photo
            IconButton(onClick = { onGooglePhotoClick() }) {
                Icon(
                    painter = painterResource(R.drawable.icon_google_logo),
                    contentDescription = stringResource(R.string.bottom_bar_google_photo_description)
                )
            }
        },
        floatingActionButton = { UploadImagesFAB { onFABClick() } }
    )
}

@Composable
fun UploadImagesScreen(
    uiState: UploadImagesState,
    modifier: Modifier = Modifier,
    onCopyClick: (AppImage) -> Unit = {},
    onRemoveClick: (AppImage) -> Unit = {}
) {
    when (uiState) {
        is UploadImagesState.PickImages -> {
            val imageList = uiState.uploadedImages + uiState.imagesToUpload
            if (imageList.isEmpty())
                EmptyListLabel(modifier)
            else
                UploadImagesList(
                    images = imageList,
                    modifier = modifier,
                    onCopyClick = onCopyClick,
                    onRemoveClick = onRemoveClick
                )
        }
        is UploadImagesState.UploadingImages -> {
            UploadImagesList(
                images = uiState.uploadedImages + uiState.uploadingImages,
                modifier = modifier,
                onCopyClick = onCopyClick,
                showRemoveAction = false
            )
        }
    }
}

@Composable
fun EmptyListLabel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.testTag(TEST_TAG_EMPTY_LABEL),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.upload_images_empty_list),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun UploadImagesList(
    images: List<AppImage>,
    modifier: Modifier = Modifier,
    onCopyClick: (AppImage) -> Unit = {},
    showRemoveAction: Boolean = true,
    onRemoveClick: (AppImage) -> Unit = {}
) {
    Surface(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(count = 3),
            contentPadding = PaddingValues(all = 2.dp)
        ) {
            items(items = images) { image: AppImage ->
                UploadedImageCard(
                    appImage = image,
                    onCopyUrlClick = onCopyClick,
                    showRemoveIcon = showRemoveAction,
                    onRemoveImageClick = onRemoveClick
                )
            }
        }
    }
}

@Composable
fun UploadedImageCard(
    appImage: AppImage,
    onCopyUrlClick: (AppImage) -> Unit = {},
    showRemoveIcon: Boolean = true,
    onRemoveImageClick: (AppImage) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(all = 2.dp)
            .testTag(TEST_TAG_IMAGE_CARD),
        contentAlignment = Alignment.BottomCenter
    ) {
        AsyncImage(
            model = appImage.contentUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.aspectRatio(1.0f)
        )

        Row(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.onBackground
                        )
                    )
                )
                .height(60.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val overlayElementModifier = Modifier
                .size(26.dp)
                .padding(all = 2.dp)
            val overlayElementColor = MaterialTheme.colorScheme.background

            if (showRemoveIcon) {
                IconButton(
                    onClick = { onRemoveImageClick(appImage) },
                    modifier = Modifier
                        .size(30.dp)
                        .testTag(TEST_TAG_IMAGE_CARD_REMOVE_ACTION)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(id = R.string.upload_images_remove_image_description),
                        modifier = overlayElementModifier,
                        tint = overlayElementColor
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { onCopyUrlClick(appImage) },
                    modifier = Modifier.size(30.dp),
                    enabled = appImage.status == UploadImageStatus.UPLOADED
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_content_copy),
                        contentDescription = stringResource(id = R.string.upload_images_copy_text_description),
                        modifier = overlayElementModifier,
                        tint = overlayElementColor
                    )
                }

                when (appImage.status) {
                    UploadImageStatus.TO_UPLOAD -> {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_cloud_off),
                            contentDescription = null,
                            modifier = overlayElementModifier,
                            tint = overlayElementColor
                        )
                    }

                    UploadImageStatus.IS_LOADING -> {
                        CircularProgressIndicator(
                            modifier = overlayElementModifier,
                            color = overlayElementColor
                        )
                    }

                    UploadImageStatus.UPLOADED -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(id = R.string.upload_images_copy_text_description),
                            modifier = overlayElementModifier,
                            tint = overlayElementColor
                        )
                    }

                    UploadImageStatus.ERROR -> {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = stringResource(id = R.string.upload_images_error_description),
                            modifier = overlayElementModifier,
                            tint = overlayElementColor
                        )
                    }
                }
            }
        }
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
fun UploadedImagesListPreview() {
    UploadImagesTheme {
        UploadImagesList(List(10) {
            AppImage(
                contentUri = Uri.EMPTY,
                status = UploadImageStatus.TO_UPLOAD
            )
        },
            showRemoveAction = false)
    }
}